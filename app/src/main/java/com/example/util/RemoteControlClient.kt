package com.example.util

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * RemoteControlClient — HTTP client untuk kirim settings + media via iO Control.
 *
 * V1.30.4: Tambah sendMediaFilesChunked() untuk transfer file media.
 */
object RemoteControlClient {

    private const val TAG = "RemoteControlClient"
    private const val TIMEOUT_MS = 30000
    private const val MEDIA_TIMEOUT_MS = 60000
    private const val MAX_RETRY = 3

    data class TransferResult(
        val success: Boolean,
        val message: String,
        val bytesSent: Int = 0
    )

    data class MediaTransferResult(
        val success: Boolean,
        val message: String,
        val filesTransferred: Int = 0,
        val filesFailed: Int = 0,
        val totalBytes: Long = 0L
    )

    // ============================================================
    // KIRIM SETTINGS (existing)
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
    // V1.30.4 BARU — KIRIM MEDIA FILES (CHUNKED)
    // ============================================================
    /**
     * Kirim semua file media dari HP ke TV via chunk upload.
     *
     * @param mediaList List MediaFileInfo dari MediaTransferHelper.collectMediaFiles()
     * @param onProgress callback (currentIndex, totalFiles, currentFileName, fileProgress, overallProgress, phase, message)
     */
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

        try {
            // Fase collecting: hitung semua ukuran file dulu
            onProgress(MediaTransferHelper.TransferProgress(
                currentFileIndex = 0,
                totalFiles = totalFiles,
                currentFileName = "",
                currentFileProgress = 0f,
                overallProgress = 0f,
                phase = "collecting",
                message = "Menyiapkan data..."
            ))

            // Cek handshake dulu
            if (!handshake(targetIp, targetPort)) {
                return@withContext MediaTransferResult(
                    success = false,
                    message = "Tidak bisa terhubung ke $targetIp:$targetPort"
                )
            }

            // Proses tiap file
            mediaList.forEachIndexed { index, media ->
                val overallStart = index.toFloat() / totalFiles
                val overallEnd = (index + 1).toFloat() / totalFiles

                val fileSuccess = sendSingleMediaFile(
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

                if (fileSuccess.success) {
                    transferred++
                    totalBytes += fileSuccess.bytesSent
                    Log.d(TAG, "✅ File ${index + 1}/$totalFiles OK: ${media.displayName}")
                } else {
                    failed++
                    Log.e(TAG, "❌ File ${index + 1}/$totalFiles GAGAL: ${media.displayName} - ${fileSuccess.message}")
                    // Update progress dengan pesan error
                    onProgress(MediaTransferHelper.TransferProgress(
                        currentFileIndex = index + 1,
                        totalFiles = totalFiles,
                        currentFileName = media.displayName,
                        currentFileProgress = 1f,
                        overallProgress = overallEnd,
                        phase = "error",
                        message = "Gagal: ${fileSuccess.message}"
                    ))
                }
            }

            // Done
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
                totalBytes = totalBytes
            )
        } catch (e: Exception) {
            Log.e(TAG, "sendMediaFilesChunked error: ${e.message}", e)
            MediaTransferResult(
                success = false,
                message = "Error: ${e.message ?: "Unknown"}",
                filesTransferred = transferred,
                filesFailed = failed + 1,
                totalBytes = totalBytes
            )
        }
    }

    // ============================================================
    // KIRIM 1 FILE MEDIA (dengan chunk + retry)
    // ============================================================
    private suspend fun sendSingleMediaFile(
        context: Context,
        targetIp: String,
        targetPort: Int,
        media: MediaTransferHelper.MediaFileInfo,
        fileIndex: Int,
        totalFiles: Int,
        onFileProgress: (Float, String, String) -> Unit
    ): TransferResult = withContext(Dispatchers.IO) {

        try {
            // 1. Baca + kompres
            onFileProgress(0.0f, "reading", "Membaca ${media.displayName}...")

            val rawBytes: ByteArray? = if (media.fileType == "photo") {
                MediaTransferHelper.compressPhoto(context, media.uri)
                    ?: MediaTransferHelper.readFileBytes(context, media.uri)
            } else {
                MediaTransferHelper.readFileBytes(context, media.uri)
            }

            if (rawBytes == null || rawBytes.isEmpty()) {
                return@withContext TransferResult(
                    success = false,
                    message = "File kosong atau gagal dibaca"
                )
            }

            onFileProgress(0.15f, "reading", "File dibaca (${MediaTransferHelper.formatSize(rawBytes.size.toLong())})")

            // 2. Split jadi chunk
            val chunks = MediaTransferHelper.splitIntoChunks(rawBytes)
            val totalChunks = chunks.size
            val fileId = UUID.randomUUID().toString()
            Log.d(TAG, "File ${media.displayName}: ${rawBytes.size} bytes → $totalChunks chunks")

            // 3. Media start
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
                return@withContext TransferResult(
                    success = false,
                    message = "Gagal start transfer di server"
                )
            }

            // 4. Kirim chunk satu-satu (0.18 → 0.95)
            val chunkStartProgress = 0.18f
            val chunkEndProgress = 0.95f
            var allChunksOk = true

            for (i in chunks.indices) {
                val chunk = chunks[i]
                var attempt = 0
                var sent = false

                while (attempt < MAX_RETRY && !sent) {
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
                        Log.w(TAG, "Retry chunk $i (attempt $attempt/$MAX_RETRY)")
                        if (attempt < MAX_RETRY) kotlinx.coroutines.delay(1000L)
                    }
                }

                if (!sent) {
                    allChunksOk = false
                    Log.e(TAG, "Chunk $i GAGAL setelah $MAX_RETRY percobaan")
                    break
                }

                val fileProgress = chunkStartProgress +
                        (chunkEndProgress - chunkStartProgress) * (i + 1) / totalChunks
                onFileProgress(
                    fileProgress,
                    "transferring",
                    "Mengirim ${i + 1}/$totalChunks (${((i + 1) * 100 / totalChunks)}%)"
                )
            }

            if (!allChunksOk) {
                return@withContext TransferResult(
                    success = false,
                    message = "Beberapa chunk gagal terkirim"
                )
            }

            // 5. Media finish
            onFileProgress(0.97f, "finishing", "Menyelesaikan...")
            val finishOk = mediaFinish(targetIp, targetPort, fileId)

            if (!finishOk) {
                return@withContext TransferResult(
                    success = false,
                    message = "Server gagal menyimpan file"
                )
            }

            onFileProgress(1.0f, "done", "✅ ${media.displayName} selesai")

            TransferResult(
                success = true,
                message = "OK",
                bytesSent = rawBytes.size
            )
        } catch (e: Exception) {
            Log.e(TAG, "sendSingleMediaFile error: ${e.message}", e)
            TransferResult(
                success = false,
                message = "Error: ${e.message ?: "Unknown"}"
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
            false
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
            false
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
