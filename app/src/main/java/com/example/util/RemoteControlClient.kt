package com.example.util

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
 * V1.30.7: Verifikasi + error detail + retry file gagal
 *   - Setiap catch simpan stack trace lengkap (Kotlin asli)
 *   - MediaTransferResult berisi daftar failures + log lengkap
 *   - Tambah retryFailedMedia() untuk kirim ulang file gagal saja
 *   - Tambah queryServerMediaStatus() untuk cek status di server TV
 */
object RemoteControlClient {

    private const val TAG = "RemoteControlClient"
    private const val TIMEOUT_MS = 30000
    private const val MEDIA_TIMEOUT_MS = 60000
    private const val MAX_RETRY = 3

    // ============================================================
    // DATA CLASS
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
    // HELPER — BUILD STACK TRACE (Kotlin asli)
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
                Log.d(TAG, "✅ Settings terkirim")
                TransferResult(
                    success = true,
                    message = "Settings berhasil dikirim.",
                    bytesSent = bytes.size
                )
            } else {
                Log.e(TAG, "❌ Server tolak: $responseCode - $responseBody")
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
    // KIRIM SINYAL FINALIZE (dipanggil setelah user klik KONFIRMASI RESTART)
    // ============================================================
    suspend fun sendFinalizeSignal(
        targetIp: String,
        targetPort: Int
    ): TransferResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🎬 Mengirim sinyal FINALIZE ke $targetIp:$targetPort")

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
                Log.d(TAG, "✅ Finalize terkirim — TV akan restart dalam 5 detik")
                TransferResult(success = true, message = "Finalize terkirim")
            } else {
                Log.e(TAG, "❌ Finalize gagal: $responseCode - $responseBody")
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
    // KIRIM MEDIA FILES (CHUNKED)
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

                val fileResult = sendSingleMediaFile(
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
                    Log.d(TAG, "✅ File ${index + 1}/$totalFiles OK: ${media.displayName}")
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
                    Log.e(TAG, "❌ File ${index + 1}/$totalFiles GAGAL: ${media.displayName} - ${fileResult.message}")

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
    // KIRIM ULANG HANYA FILE YANG GAGAL
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

        Log.d(TAG, "🔄 Retry ${retryList.size} file yang gagal")

        // Panggil ulang fungsi kirim dengan list yang sudah difilter
        sendMediaFilesChunked(
            context = context,
            targetIp = targetIp,
            targetPort = targetPort,
            mediaList = retryList,
            onProgress = onProgress
        )
    }

    // ============================================================
    // QUERY STATUS MEDIA DI SERVER (untuk verifikasi)
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
    // KIRIM 1 FILE MEDIA (dengan chunk + retry + error detail)
    // ============================================================
    private suspend fun sendSingleMediaFile(
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

            // ===== BACA FILE =====
            val rawBytes: ByteArray? = try {
                if (media.fileType == "photo") {
                    MediaTransferHelper.compressPhoto(context, media.uri)
                        ?: MediaTransferHelper.readFileBytes(context, media.uri)
                } else {
                    MediaTransferHelper.readFileBytes(context, media.uri)
                }
            } catch (e: Exception) {
                val failure = MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = e.javaClass.name,
                    exceptionMessage = e.message ?: "(no message)",
                    fullStackTrace = buildStackTrace(e),
                    failedAt = "membaca file",
                    timestamp = nowString()
                )
                return@withContext SingleFileResult(
                    success = false,
                    message = "Gagal baca file: ${e.message}",
                    failure = failure
                )
            }

            if (rawBytes == null || rawBytes.isEmpty()) {
                val e = IllegalStateException("File kosong atau null setelah dibaca")
                val failure = MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = e.javaClass.name,
                    exceptionMessage = e.message ?: "File kosong",
                    fullStackTrace = buildStackTrace(e),
                    failedAt = "baca file (hasil kosong)",
                    timestamp = nowString()
                )
                return@withContext SingleFileResult(
                    success = false,
                    message = "File kosong atau gagal dibaca",
                    failure = failure
                )
            }

            onFileProgress(0.15f, "reading", "File dibaca (${MediaTransferHelper.formatSize(rawBytes.size.toLong())})")

            // ===== SPLIT CHUNK =====
            val chunks = MediaTransferHelper.splitIntoChunks(rawBytes)
            val totalChunks = chunks.size
            val fileId = UUID.randomUUID().toString()
            Log.d(TAG, "File ${media.displayName}: ${rawBytes.size} bytes → $totalChunks chunks")

            // ===== START =====
            currentPhase = "start transfer di server"
            onFileProgress(0.18f, "starting", "Memulai transfer...")

            val startOk = mediaStart(
                targetIp, targetPort,
                fileId = fileId,
                fieldKey = media.fieldKey,
                fileName = "${media.fieldKey}_${System.currentTimeMillis()}.${if (media.fileType == "photo") "jpg" else "mp4"}",
                fileType = media.fileType,
                totalChunks = totalChunks,
                mimeType = media.mimeType
            )

            if (!startOk) {
                val e = RuntimeException("Server TV menolak start transfer (media-start)")
                val failure = MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = e.javaClass.name,
                    exceptionMessage = e.message ?: "media-start gagal",
                    fullStackTrace = buildStackTrace(e),
                    failedAt = "media-start",
                    timestamp = nowString()
                )
                return@withContext SingleFileResult(
                    success = false,
                    message = "Gagal start transfer di server",
                    failure = failure
                )
            }

            // ===== CHUNK LOOP =====
            val chunkStartProgress = 0.18f
            val chunkEndProgress = 0.95f
            var lastException: Exception? = null
            var failedChunkIndex = -1

            for (i in chunks.indices) {
                val chunk = chunks[i]
                var attempt = 0
                var sent = false

                while (attempt < MAX_RETRY && !sent) {
                    try {
                        val ok = mediaChunk(
                            targetIp, targetPort,
                            fileId = fileId,
                            chunkIndex = i,
                            chunkData = chunk
                        )
                        if (ok) {
                            sent = true
                        } else {
                            attempt++
                            lastException = RuntimeException("mediaChunk return false (percobaan $attempt/$MAX_RETRY)")
                            failedChunkIndex = i
                            Log.w(TAG, "Retry chunk $i (attempt $attempt/$MAX_RETRY)")
                            if (attempt < MAX_RETRY) kotlinx.coroutines.delay(1000L)
                        }
                    } catch (e: Exception) {
                        attempt++
                        lastException = e
                        failedChunkIndex = i
                        Log.w(TAG, "Chunk $i exception (attempt $attempt/$MAX_RETRY): ${e.message}")
                        if (attempt < MAX_RETRY) kotlinx.coroutines.delay(1000L)
                    }
                }

                if (!sent) {
                    val e = lastException ?: RuntimeException("Chunk $i gagal setelah $MAX_RETRY percobaan")
                    val failure = MediaFileFailure(
                        fieldKey = media.fieldKey,
                        displayName = media.displayName,
                        exceptionClass = e.javaClass.name,
                        exceptionMessage = e.message ?: "Chunk gagal",
                        fullStackTrace = buildStackTrace(e),
                        failedAt = "chunk $failedChunkIndex/$totalChunks",
                        timestamp = nowString()
                    )
                    return@withContext SingleFileResult(
                        success = false,
                        message = "Chunk $failedChunkIndex gagal terkirim",
                        failure = failure
                    )
                }

                val fileProgress = chunkStartProgress +
                        (chunkEndProgress - chunkStartProgress) * (i + 1) / totalChunks
                onFileProgress(
                    fileProgress,
                    "transferring",
                    "Mengirim ${i + 1}/$totalChunks (${((i + 1) * 100 / totalChunks)}%)"
                )
            }

            // ===== FINISH =====
            currentPhase = "finish di server"
            onFileProgress(0.97f, "finishing", "Menyelesaikan...")

            val finishOk = try {
                mediaFinish(targetIp, targetPort, fileId)
            } catch (e: Exception) {
                val failure = MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = e.javaClass.name,
                    exceptionMessage = e.message ?: "(no message)",
                    fullStackTrace = buildStackTrace(e),
                    failedAt = "media-finish (exception)",
                    timestamp = nowString()
                )
                return@withContext SingleFileResult(
                    success = false,
                    message = "Gagal finish: ${e.message}",
                    failure = failure
                )
            }

            if (!finishOk) {
                val e = RuntimeException("Server TV gagal menyimpan file (media-finish)")
                val failure = MediaFileFailure(
                    fieldKey = media.fieldKey,
                    displayName = media.displayName,
                    exceptionClass = e.javaClass.name,
                    exceptionMessage = e.message ?: "media-finish gagal",
                    fullStackTrace = buildStackTrace(e),
                    failedAt = "media-finish",
                    timestamp = nowString()
                )
                return@withContext SingleFileResult(
                    success = false,
                    message = "Server gagal menyimpan file",
                    failure = failure
                )
            }

            onFileProgress(1.0f, "done", "✅ ${media.displayName} selesai")

            SingleFileResult(
                success = true,
                message = "OK",
                bytesSent = rawBytes.size
            )
        } catch (e: Exception) {
            Log.e(TAG, "sendSingleMediaFile error (fase=$currentPhase): ${e.message}", e)
            val failure = MediaFileFailure(
                fieldKey = media.fieldKey,
                displayName = media.displayName,
                exceptionClass = e.javaClass.name,
                exceptionMessage = e.message ?: "(no message)",
                fullStackTrace = buildStackTrace(e),
                failedAt = currentPhase,
                timestamp = nowString()
            )
            SingleFileResult(
                success = false,
                message = "Error: ${e.message ?: "Unknown"}",
                failure = failure
            )
        }
    }

    // ============================================================
    // HTTP REQUEST HELPERS
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
}
