package com.example.util

import android.util.Log
import com.example.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * RemoteControlClient — HTTP client untuk kirim settings ke device lain via iO Control.
 *
 * Cara kerja:
 *   1. Serialize AppSettings → JSON (via SettingsTransferHelper)
 *   2. POST ke http://{targetIp}:{targetPort}/api/io/receive
 *   3. Report progress via callback
 *   4. Device penerima apply settings + restart otomatis
 */
object RemoteControlClient {

    private const val TAG = "RemoteControlClient"
    private const val TIMEOUT_MS = 30000

    /**
     * Hasil transfer.
     */
    data class TransferResult(
        val success: Boolean,
        val message: String,
        val bytesSent: Int = 0
    )

    /**
     * Kirim settings ke device target.
     *
     * @param targetIp IP device target
     * @param targetPort Port HTTP device target (biasanya 8080)
     * @param settings AppSettings yang akan dikirim
     * @param onProgress callback (0.0 - 1.0) untuk update UI
     */
    suspend fun sendSettings(
        targetIp: String,
        targetPort: Int,
        settings: AppSettings,
        onProgress: (Float) -> Unit = {}
    ): TransferResult = withContext(Dispatchers.IO) {

        try {
            // ==== STEP 1: Serialize ====
            onProgress(0.05f)
            val jsonPayload = SettingsTransferHelper.serializeSettings(settings)
            val bytes = jsonPayload.toByteArray(Charsets.UTF_8)
            Log.d(TAG, "Payload size: ${bytes.size} bytes")

            onProgress(0.15f)

            // ==== STEP 2: Handshake dulu (pastikan target hidup) ====
            val pingOk = handshake(targetIp, targetPort)
            if (!pingOk) {
                return@withContext TransferResult(
                    success = false,
                    message = "Tidak bisa terhubung ke $targetIp:$targetPort"
                )
            }

            onProgress(0.25f)

            // ==== STEP 3: Kirim settings ====
            val url = URL("http://$targetIp:$targetPort/api/io/receive")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.setRequestProperty("Content-Length", bytes.size.toString())
            conn.setRequestProperty("User-Agent", "Masjid.io-iOControl")

            // Kirim body + progress
            conn.outputStream.use { out ->
                val chunkSize = 8192
                var sent = 0
                while (sent < bytes.size) {
                    val end = minOf(sent + chunkSize, bytes.size)
                    out.write(bytes, sent, end - sent)
                    sent = end
                    // Progress dari 0.3 ke 0.9
                    val prog = 0.3f + (sent.toFloat() / bytes.size) * 0.6f
                    onProgress(prog)
                }
                out.flush()
            }

            onProgress(0.92f)

            // ==== STEP 4: Cek response ====
            val responseCode = conn.responseCode
            val responseBody = try {
                if (responseCode in 200..299) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                }
            } catch (e: Exception) {
                ""
            } finally {
                conn.disconnect()
            }

            onProgress(1.0f)

            if (responseCode in 200..299) {
                Log.d(TAG, "✅ Settings terkirim. Response: $responseBody")
                TransferResult(
                    success = true,
                    message = "Settings berhasil dikirim. Device akan restart.",
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
    
    /**
     * Handshake — cek apakah device target hidup.
     */
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

    /**
     * Cek status device (opsional — untuk ditampilkan di UI).
     */
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
