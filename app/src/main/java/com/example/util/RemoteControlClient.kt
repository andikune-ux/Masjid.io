package dev.andikune.masjidio.util

import android.content.Context
import android.util.Base64
import android.util.Log
import dev.andikune.masjidio.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * RemoteControlClient — HTTP client untuk kirim settings + media via iO Control.
 *
 * V1.04.426 — STREAMING UPLOAD:
 *   - Streaming upload 1 request/file (bukan chunk + Base64)
 *   - Baca file dari disk langsung ke socket
 *   - TCP handle retransmisi otomatis
 *   - Tetap dukung metode lama (backward compat)
 */
object RemoteControlClient {

    private const val TAG = "RemoteControlClient"
    private const val TIMEOUT_MS = 30000
    private const val MEDIA_TIMEOUT_MS = 600000
    private const val MAX_RETRY = 3
    private const val STREAM_BUFFER_SIZE = 64 * 1024

    // ============================================================
    // DATA CLASSES
    // ============================================================
    data class TransferResult(
        val success: Boolean,
        val message: String,
        val bytesSent: Int = 0
    )

    data class MediaFileFailure(
        val fieldKey: String,
        val displayName: String,
        val exceptionClass: String,
        val exceptionMessage: String,
        val fullStackTrace: String,
        val failedAt: String,
        val timestamp: String
    )

    data class MediaTransferResult(
        val success: Boolean,
        val message: String,
        val filesTransferred: Int = 0,
        val filesFailed: Int = 0,
        val totalBytes: Long = 0L,
        val failures: List<MediaFileFailure> = emptyList(),
        val allFailuresLog: String = ""
    )

    private data class SingleFileResult(
        val success: Boolean,
        val message: String,
        val bytesSent: Int = 0,
        val failure: MediaFileFailure? = null
    )

    data class ServerMediaFile(
        val fileId: String,
        val fileName: String,
        val received: Int,
        val total: Int,
        val finished: Boolean,
        val savedPath: String
    )

    // ============================================================
    // HELPER — STACK TRACE
    // ============================================================
    private fun buildStackTrace(e: Throwable): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    private fun nowString(): String {
        return SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    }

    private fun buildFailuresLog(failures: List<MediaFileFailure>): String {
        if (failures.isEmpty()) return ""
        return buildString {
            appendLine("=".repeat(60))
            appendLine("LOG KEGAGALAN TRANSFER MEDIA")
            appendLine("Total gagal: ${failures.size} file")
            appendLine("=".repeat(60))
            appendLine()
            failures.forEachIndexed { idx, f ->
                appendLine("[${idx + 1}] ${f.displayName}")
                appendLine("    Field      : ${f.fieldKey}")
                appendLine("    Waktu      : ${f.timestamp}")
                appendLine("    Gagal di   : ${f.failedAt}")
                appendLine("    Exception  : ${f.exceptionClass}")
                appendLine("    Pesan      : ${f.exceptionMessage}")
                appendLine()
                appendLine("    Stack Trace:")
                appendLine(f.fullStackTrace)
                appendLine("-".repeat(60))
                appendLine()
            }
        }
    }

    // ============================================================
    // HELPER — Deteksi path lokal vs content:// URI
    // ============================================================
    private fun isLocalPath(uriString: String): Boolean {
        return uriString.startsWith("/") || uriString.startsWith("file://")
    }

    private fun localPathFromUri(uriString: String): String {
        return if (uriString.startsWith("file://")) {
            uriString.removePrefix("file://")
        } else {
            uriString
        }
    }
    // ============================================================
// KIRIM SETTINGS
// ============================================================
suspend fun sendSettings(
    targetIp: String,
    targetPort: Int,
    settings: AppSettings,
    onProgress: (Float) -> Unit = {}
): TransferResult = withContext(Dispatchers.IO) {
    try {
        onProgress(0.05f)
        val jsonPayload = SettingsTransferHelper.serializeSettings(settings)
        val bytes = jsonPayload.toByteArray(Charsets.UTF_8)
        Log.d(TAG, "Payload size: ${bytes.size} bytes")
        onProgress(0.15f)

        val pingOk = handshake(targetIp, targetPort)
        if (!pingOk) {
            return@withContext TransferResult(
                success = false,
                message = "Tidak bisa terhubung ke $targetIp:$targetPort"
            )
        }
        onProgress(0.25f)

        val url = URL("http://$targetIp:$targetPort/api/io/receive")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = TIMEOUT_MS
        conn.readTimeout = TIMEOUT_MS
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        conn.setRequestProperty("Content-Length", bytes.size.toString())
        conn.setRequestProperty("User-Agent", "Masjid.io-iOControl")

        conn.outputStream.use { out ->
            val chunkSize = 8192
            var sent = 0
            while (sent < bytes.size) {
                val end = minOf(sent + chunkSize, bytes.size)
                out.write(bytes, sent, end - sent)
                sent = end
                val prog = 0.3f + (sent.toFloat() / bytes.size) * 0.6f
                onProgress(prog)
            }
            out.flush()
        }

        onProgress(0.92f)

        val responseCode = conn.responseCode
        val responseBody = try {
            if (responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }
        } catch (e: Exception) { "" } finally { conn.disconnect() }

        onProgress(1.0f)

        if (responseCode in 200..299) {
            Log.d(TAG, "Settings terkirim")
            TransferResult(
                success = true,
                message = "Settings berhasil dikirim.",
                bytesSent = bytes.size
            )
        } else {
            Log.e(TAG, "Server tolak: $responseCode - $responseBody")
            TransferResult(
                success = false,
                message = "Server tolak (kode $responseCode)",
                bytesSent = bytes.size
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "Transfer gagal: ${e.message}", e)
        onProgress(0f)
        TransferResult(
            success = false,
            message = "Gagal kirim: ${e.message ?: "Unknown error"}"
        )
    }
}

// ============================================================
// KIRIM SINYAL FINALIZE
// ============================================================
suspend fun sendFinalizeSignal(
    targetIp: String,
    targetPort: Int
): TransferResult = withContext(Dispatchers.IO) {
    try {
        Log.d(TAG, "Kirim sinyal FINALIZE ke $targetIp:$targetPort")

        val url = URL("http://$targetIp:$targetPort/api/io/finalize")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")

        val json = JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
        }.toString()

        conn.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }

        val responseCode = conn.responseCode
        val responseBody = try {
            if (responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }
        } catch (e: Exception) { "" } finally { conn.disconnect() }

        if (responseCode in 200..299) {
            Log.d(TAG, "Finalize terkirim")
            TransferResult(success = true, message = "Finalize terkirim")
        } else {
            Log.e(TAG, "Finalize gagal: $responseCode - $responseBody")
            TransferResult(
                success = false,
                message = "Server tolak finalize (kode $responseCode)"
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "Finalize error: ${e.message}", e)
        TransferResult(
            success = false,
            message = "Gagal kirim finalize: ${e.message ?: "Unknown"}"
        )
    }
}

// ============================================================
// RETRY FAILED MEDIA
// ============================================================
suspend fun retryFailedMedia(
    context: Context,
    targetIp: String,
    targetPort: Int,
    allMediaList: List<MediaTransferHelper.MediaFileInfo>,
    failedFieldKeys: List<String>,
    onProgress: (MediaTransferHelper.TransferProgress) -> Unit = {}
): MediaTransferResult = withContext(Dispatchers.IO) {

    val retryList = allMediaList.filter { it.fieldKey in failedFieldKeys }

    if (retryList.isEmpty()) {
        return@withContext MediaTransferResult(
            success = true,
            message = "Tidak ada file yang perlu dikirim ulang",
            filesTransferred = 0,
            filesFailed = 0
        )
    }

    Log.d(TAG, "Retry ${retryList.size} file yang gagal")

    sendMediaFilesChunked(
        context = context,
        targetIp = targetIp,
        targetPort = targetPort,
        mediaList = retryList,
        onProgress = onProgress
    )
}

// ============================================================
// QUERY STATUS MEDIA DI SERVER
// ============================================================
suspend fun queryServerMediaStatus(
    targetIp: String,
    targetPort: Int
): List<ServerMediaFile> = withContext(Dispatchers.IO) {
    try {
        val url = URL("http://$targetIp:$targetPort/api/io/media-status")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 10000
        conn.readTimeout = 10000

        if (conn.responseCode !in 200..299) {
            conn.disconnect()
            return@withContext emptyList()
        }

        val response = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()

        val arr = org.json.JSONArray(response)
        val result = mutableListOf<ServerMediaFile>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            result.add(ServerMediaFile(
                fileId = obj.optString("fileId", ""),
                fileName = obj.optString("fileName", ""),
                received = obj.optInt("received", 0),
                total = obj.optInt("total", 0),
                finished = obj.optBoolean("finished", false),
                savedPath = obj.optString("savedPath", "")
            ))
        }
        result
    } catch (e: Exception) {
        Log.w(TAG, "Query media status gagal: ${e.message}")
        emptyList()
    }
}
// ============================================================
// KIRIM MEDIA FILES — STREAMING (V1.04.426)
// ============================================================
suspend fun sendMediaFilesChunked(
    context: Context,
    targetIp: String,
    targetPort: Int,
    mediaList: List<MediaTransferHelper.MediaFileInfo>,
    onProgress: (MediaTransferHelper.TransferProgress) -> Unit = {}
): MediaTransferResult = withContext(Dispatchers.IO) {

    if (mediaList.isEmpty()) {
        return@withContext MediaTransferResult(
            success = true,
            message = "Tidak ada media untuk dikirim",
            filesTransferred = 0
        )
    }

    val totalFiles = mediaList.size
    var transferred = 0
    var failed = 0
    var totalBytes: Long = 0L
    val failures = mutableListOf<MediaFileFailure>()

    try {
        onProgress(MediaTransferHelper.TransferProgress(
            currentFileIndex = 0,
            totalFiles = totalFiles,
            currentFileName = "",
            currentFileProgress = 0f,
            overallProgress = 0f,
            phase = "collecting",
            message = "Menyiapkan data..."
        ))

        if (!handshake(targetIp, targetPort)) {
            return@withContext MediaTransferResult(
                success = false,
                message = "Tidak bisa terhubung ke $targetIp:$targetPort"
            )
        }

        mediaList.forEachIndexed { index, media ->
            val overallStart = index.toFloat() / totalFiles
            val overallEnd = (index + 1).toFloat() / totalFiles

            val fileResult = sendSingleMediaFileStreaming(
                context = context,
                targetIp = targetIp,
                targetPort = targetPort,
                media = media,
                fileIndex = index,
                totalFiles = totalFiles,
                onFileProgress = { fileProgress, phase, msg ->
                    val overall = overallStart + (overallEnd - overallStart) * fileProgress
                    onProgress(MediaTransferHelper.TransferProgress(
                        currentFileIndex = index + 1,
                        totalFiles = totalFiles,
                        currentFileName = media.displayName,
                        currentFileProgress = fileProgress,
                        overallProgress = overall,
                        phase = phase,
                        message = msg
                    ))
                }
            )

            if (fileResult.success) {
                transferred++
                totalBytes += fileResult.bytesSent
                Log.d(TAG, "File ${index + 1}/$totalFiles OK: ${media.displayName}")
            } else {
                failed++
                val f = fileResult.failure ?: MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = "UnknownException",
                    exceptionMessage = fileResult.message,
                    fullStackTrace = "(tidak ada stack trace)",
                    failedAt = "unknown",
                    timestamp = nowString()
                )
                failures.add(f)
                Log.e(TAG, "File ${index + 1}/$totalFiles GAGAL: ${media.displayName} - ${fileResult.message}")

                onProgress(MediaTransferHelper.TransferProgress(
                    currentFileIndex = index + 1,
                    totalFiles = totalFiles,
                    currentFileName = media.displayName,
                    currentFileProgress = 1f,
                    overallProgress = overallEnd,
                    phase = "error",
                    message = "Gagal: ${fileResult.message}"
                ))
            }
        }

        onProgress(MediaTransferHelper.TransferProgress(
            currentFileIndex = totalFiles,
            totalFiles = totalFiles,
            currentFileName = "",
            currentFileProgress = 1f,
            overallProgress = 1f,
            phase = "done",
            message = if (failed == 0) "Semua media terkirim!"
                     else "$transferred sukses, $failed gagal"
        ))

        MediaTransferResult(
            success = failed == 0,
            message = if (failed == 0) "Semua media berhasil dikirim"
                     else "$transferred sukses, $failed gagal",
            filesTransferred = transferred,
            filesFailed = failed,
            totalBytes = totalBytes,
            failures = failures,
            allFailuresLog = buildFailuresLog(failures)
        )
    } catch (e: Exception) {
        Log.e(TAG, "sendMediaFilesChunked error: ${e.message}", e)
        MediaTransferResult(
            success = false,
            message = "Error: ${e.message ?: "Unknown"}",
            filesTransferred = transferred,
            filesFailed = failed + 1,
            totalBytes = totalBytes,
            failures = failures,
            allFailuresLog = buildFailuresLog(failures)
        )
    }
}

// ============================================================
// V1.04.426 — KIRIM 1 FILE MEDIA via STREAMING
// Satu HTTP POST, file dibaca langsung dari disk → socket.
// ============================================================
private suspend fun sendSingleMediaFileStreaming(
    context: Context,
    targetIp: String,
    targetPort: Int,
    media: MediaTransferHelper.MediaFileInfo,
    fileIndex: Int,
    totalFiles: Int,
    onFileProgress: (Float, String, String) -> Unit
): SingleFileResult = withContext(Dispatchers.IO) {

    var currentPhase = "starting"

    try {
        onFileProgress(0.0f, "reading", "Membaca ${media.displayName}...")

        // ===== TENTUKAN FILE SUMBER =====
        val sourceFile: File
        val isTempFile: Boolean

        if (isLocalPath(media.uri)) {
            val f = File(localPathFromUri(media.uri))
            if (!f.exists()) {
                val e = IllegalStateException("File tidak ditemukan: ${f.absolutePath}")
                return@withContext SingleFileResult(
                    success = false,
                    message = "File tidak ditemukan",
                    failure = MediaFileFailure(
                        fieldKey = media.fieldKey,
                        displayName = media.displayName,
                        exceptionClass = e.javaClass.name,
                        exceptionMessage = e.message ?: "",
                        fullStackTrace = buildStackTrace(e),
                        failedAt = "buka file lokal",
                        timestamp = nowString()
                    )
                )
            }
            sourceFile = f
            isTempFile = false
        } else {
            // Content URI — copy dulu ke cache supaya bisa stream
            val cacheDir = File(context.cacheDir, "masjidio_stream")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val tmpFile = File(cacheDir, "stream_${System.currentTimeMillis()}_${media.fieldKey}")

            try {
                val input = context.contentResolver.openInputStream(
                    android.net.Uri.parse(media.uri)
                ) ?: throw IllegalStateException("Tidak bisa buka URI")

                FileOutputStream(tmpFile).use { out ->
                    input.use { inp ->
                        val buf = ByteArray(STREAM_BUFFER_SIZE)
                        var read: Int
                        while (inp.read(buf).also { read = it } > 0) {
                            out.write(buf, 0, read)
                        }
                    }
                }
                sourceFile = tmpFile
                isTempFile = true
            } catch (e: Exception) {
                return@withContext SingleFileResult(
                    success = false,
                    message = "Gagal copy URI ke cache: ${e.message}",
                    failure = MediaFileFailure(
                        fieldKey = media.fieldKey,
                        displayName = media.displayName,
                        exceptionClass = e.javaClass.name,
                        exceptionMessage = e.message ?: "",
                        fullStackTrace = buildStackTrace(e),
                        failedAt = "copy URI ke cache",
                        timestamp = nowString()
                    )
                )
            }
        }

        val fileSize = sourceFile.length()
        if (fileSize <= 0L) {
            if (isTempFile) try { sourceFile.delete() } catch (_: Exception) {}
            val e = IllegalStateException("File kosong: ${sourceFile.absolutePath}")
            return@withContext SingleFileResult(
                success = false,
                message = "File kosong",
                failure = MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = e.javaClass.name,
                    exceptionMessage = e.message ?: "",
                    fullStackTrace = buildStackTrace(e),
                    failedAt = "cek ukuran file",
                    timestamp = nowString()
                )
            )
        }

        onFileProgress(0.05f, "starting", "Mulai kirim (${MediaTransferHelper.formatSize(fileSize)})...")

        // ===== BUKA KONEKSI HTTP =====
        val fileId = UUID.randomUUID().toString()
        val fileName = "${media.fieldKey}_${System.currentTimeMillis()}.${if (media.fileType == "photo") "jpg" else "mp4"}"

        val url = URL("http://$targetIp:$targetPort/api/io/upload-stream")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = TIMEOUT_MS
        conn.readTimeout = MEDIA_TIMEOUT_MS
        conn.doOutput = true
        conn.setFixedLengthStreamingMode(fileSize)
        conn.setRequestProperty("Content-Type", "application/octet-stream")
        conn.setRequestProperty("X-File-Id", fileId)
        conn.setRequestProperty("X-Field-Key", media.fieldKey)
        conn.setRequestProperty("X-File-Name", fileName)
        conn.setRequestProperty("X-File-Type", media.fileType)
        conn.setRequestProperty("X-Total-Bytes", fileSize.toString())
        conn.setRequestProperty("X-Mime-Type", media.mimeType)
        conn.setRequestProperty("User-Agent", "Masjid.io-iOControl-Stream")

        // ===== STREAMING: file → socket =====
        currentPhase = "transferring"
        var totalSent: Long = 0

        FileInputStream(sourceFile).use { fis ->
            conn.outputStream.use { out ->
                val buf = ByteArray(STREAM_BUFFER_SIZE)
                var read: Int
                var lastReport = 0L

                while (fis.read(buf).also { read = it } > 0) {
                    out.write(buf, 0, read)
                    totalSent += read

                    if (totalSent - lastReport > 256 * 1024 || totalSent == fileSize) {
                        lastReport = totalSent
                        val p = 0.05f + (totalSent.toFloat() / fileSize) * 0.90f
                        val pct = ((totalSent.toFloat() / fileSize) * 100).toInt()
                        onFileProgress(
                            p,
                            "transferring",
                            "Mengirim $pct% (${MediaTransferHelper.formatSize(totalSent)} / ${MediaTransferHelper.formatSize(fileSize)})"
                        )
                    }
                }
                out.flush()
            }
        }

        onFileProgress(0.96f, "finishing", "Menunggu respon server...")

        // ===== BACA RESPON =====
        val responseCode = conn.responseCode
        val responseBody = try {
            if (responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }
        } catch (e: Exception) { "" } finally { conn.disconnect() }

        // Hapus file temp (kalau hasil copy dari URI)
        if (isTempFile) {
            try { sourceFile.delete() } catch (_: Exception) {}
        }

        if (responseCode in 200..299) {
            onFileProgress(1.0f, "done", "${media.displayName} selesai")
            SingleFileResult(
                success = true,
                message = "OK",
                bytesSent = totalSent.toInt()
            )
        } else {
            val e = RuntimeException("Server tolak: $responseCode - $responseBody")
            SingleFileResult(
                success = false,
                message = "Server tolak (kode $responseCode)",
                failure = MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = e.javaClass.name,
                    exceptionMessage = e.message ?: "Server tolak",
                    fullStackTrace = buildStackTrace(e),
                    failedAt = "HTTP respon",
                    timestamp = nowString()
                )
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "sendSingleMediaFileStreaming error (fase=$currentPhase): ${e.message}", e)
        SingleFileResult(
            success = false,
            message = "Error: ${e.message ?: "Unknown"}",
            failure = MediaFileFailure(
                fieldKey = media.fieldKey,
                displayName = media.displayName,
                exceptionClass = e.javaClass.name,
                exceptionMessage = e.message ?: "(no message)",
                fullStackTrace = buildStackTrace(e),
                failedAt = currentPhase,
                timestamp = nowString()
            )
        )
    }
}
    // ============================================================
    // HANDLER LAMA — Chunk Upload (backward compat)
    // ============================================================
    private fun mediaStart(
        targetIp: String, targetPort: Int,
        fileId: String, fieldKey: String, fileName: String,
        fileType: String, totalChunks: Int, mimeType: String
    ): Boolean {
        return try {
            val url = URL("http://$targetIp:$targetPort/api/io/receive-media-start")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")

            val json = JSONObject().apply {
                put("fileId", fileId)
                put("fieldKey", fieldKey)
                put("fileName", fileName)
                put("fileType", fileType)
                put("totalChunks", totalChunks)
                put("mimeType", mimeType)
            }.toString()

            conn.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            conn.disconnect()
            code in 200..299
        } catch (e: Exception) {
            Log.e(TAG, "mediaStart error: ${e.message}")
            false
        }
    }

    private fun mediaChunk(
        targetIp: String, targetPort: Int,
        fileId: String, chunkIndex: Int, chunkData: ByteArray
    ): Boolean {
        return try {
            val url = URL("http://$targetIp:$targetPort/api/io/receive-media-chunk")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = MEDIA_TIMEOUT_MS
            conn.readTimeout = MEDIA_TIMEOUT_MS
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8")
            conn.setRequestProperty("X-File-Id", fileId)
            conn.setRequestProperty("X-Chunk-Index", chunkIndex.toString())

            val base64Chunk = Base64.encodeToString(chunkData, Base64.NO_WRAP)
            conn.outputStream.use { it.write(base64Chunk.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            conn.disconnect()
            code in 200..299
        } catch (e: Exception) {
            Log.e(TAG, "mediaChunk($chunkIndex) error: ${e.message}")
            throw e
        }
    }

    private fun mediaFinish(targetIp: String, targetPort: Int, fileId: String): Boolean {
        return try {
            val url = URL("http://$targetIp:$targetPort/api/io/receive-media-finish")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")

            val json = JSONObject().apply { put("fileId", fileId) }.toString()
            conn.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            conn.disconnect()
            code in 200..299
        } catch (e: Exception) {
            Log.e(TAG, "mediaFinish error: ${e.message}")
            throw e
        }
    }

    // ============================================================
    // HANDSHAKE + CHECK STATUS
    // ============================================================
    suspend fun handshake(targetIp: String, targetPort: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("http://$targetIp:$targetPort/api/io/handshake")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                val responseCode = conn.responseCode
                conn.disconnect()
                responseCode in 200..299
            } catch (e: Exception) {
                Log.w(TAG, "Handshake gagal: ${e.message}")
                false
            }
        }

    suspend fun checkStatus(targetIp: String, targetPort: Int): String? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("http://$targetIp:$targetPort/api/io/handshake")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 3000
                conn.readTimeout = 3000
                conn.doOutput = true
                val response = if (conn.responseCode in 200..299) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else null
                conn.disconnect()
                response
            } catch (e: Exception) {
                null
            }
        }

    // ============================================================
    // UPLOAD MEDIA FILES WRAPPER
    // ============================================================
    suspend fun uploadMediaFiles(
        context: Context,
        targetIp: String,
        targetPort: Int,
        settings: AppSettings,
        onProgress: (MediaTransferHelper.TransferProgress) -> Unit = {}
    ): MediaTransferResult {
        val mediaList = MediaTransferHelper.collectMediaFiles(settings)
        Log.d(TAG, "Media files to transfer: ${mediaList.size}")

        if (mediaList.isEmpty()) {
            return MediaTransferResult(
                success = true,
                message = "Tidak ada media untuk dikirim"
            )
        }

        return sendMediaFilesChunked(
            context = context,
            targetIp = targetIp,
            targetPort = targetPort,
            mediaList = mediaList,
            onProgress = onProgress
        )
    }
}
