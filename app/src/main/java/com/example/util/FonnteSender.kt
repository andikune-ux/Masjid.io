package com.example.util

import android.os.Build
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FonnteSender {

    private const val TAG = "FonnteSender"
    private const val FONNTE_URL = "https://api.fonnte.com/send"

    data class SendResult(
        val success: Boolean,
        val message: String
    )

    /**
     * Kirim pesan WA ke grup via Fonnte.
     * Panggil dari background thread (suspend).
     */
    suspend fun sendToGroup(
        token: String,
        groupId: String,
        messageText: String
    ): SendResult = withContext(Dispatchers.IO) {
        try {
            if (token.isBlank()) {
                return@withContext SendResult(false, "Token Fonnte kosong")
            }
            if (groupId.isBlank()) {
                return@withContext SendResult(false, "ID Grup Fonnte kosong")
            }

            val connection = URL(FONNTE_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("Authorization", token)

            val postData = "target=" + URLEncoder.encode(groupId, "UTF-8") +
                    "&message=" + URLEncoder.encode(messageText, "UTF-8")

            val outputStream = OutputStreamWriter(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            val responseMessage = try {
                connection.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            connection.disconnect()

            Log.d(TAG, "Fonnte response [$responseCode]: $responseMessage")

            if (responseCode == 200) {
                SendResult(true, "Pesan terkirim")
            } else {
                SendResult(false, "HTTP $responseCode: $responseMessage")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal kirim ke Fonnte: ${e.message}", e)
            SendResult(false, e.message ?: "Gagal kirim")
        }
    }

    /**
     * Format pesan crash untuk WA.
     */
    fun formatCrashMessage(
        errorType: String,
        errorMessage: String,
        stackTrace: String
    ): String {
        val timestamp = SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val appVersion = try {
            BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            "Unknown"
        }

        // Potong stacktrace kalau kepanjangan (limit WA ± 4000 char)
        val shortTrace = if (stackTrace.length > 1500) {
            stackTrace.substring(0, 1500) + "\n... (dipotong)"
        } else {
            stackTrace
        }

        return """
⚠️ *CRASH MASJID.IO*

🕐 Waktu: $timestamp
📱 Versi: $appVersion
📟 Device: ${Build.MANUFACTURER} ${Build.MODEL}
🤖 Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})

❌ *Error:* $errorType
📝 *Pesan:* $errorMessage

*Stack Trace:*
$shortTrace
        """.trimIndent()
    }

    /**
     * Test kirim pesan sederhana.
     */
    suspend fun sendTestMessage(
        token: String,
        groupId: String
    ): SendResult {
        val now = SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val testMessage = """
✅ *Test Fonnte MASJID.IO*

Koneksi berhasil! 🎉
Waktu: $now

Pesan ini dikirim otomatis dari aplikasi MASJID.IO.
Kalau Anda menerima pesan ini, berarti integrasi WhatsApp sudah AKTIF.

Ke depannya, setiap kali aplikasi MASJID.IO mengalami error/crash, 
Anda akan menerima notifikasi otomatis di grup ini.
        """.trimIndent()

        return sendToGroup(token, groupId, testMessage)
    }
}
