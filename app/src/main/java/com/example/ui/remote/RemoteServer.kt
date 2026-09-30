package com.example.ui.remote

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.data.local.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder

/**
 * HTTP Server mini untuk Remote Control via HP.
 *
 * Fitur:
 *   - Dashboard HTML (browser)
 *   - GET  /api/status          → status JSON
 *   - GET  /api/settings        → settings JSON (ringkas)
 *   - POST /api/running-text    → update running text
 *   - POST /api/pin             → update PIN
 *   - POST /api/restart         → restart app
 *
 * iO Control (BARU):
 *   - POST /api/io/handshake    → cek device lain hidup
 *   - POST /api/io/receive      → terima settings lengkap dari device lain
 *                                (apply + restart otomatis)
 */
class RemoteServer(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val port: Int = 8080,
    private val authToken: String = "masjid-io",
    private val onRestart: (() -> Unit)? = null,
    private val onSettingsReceived: ((String) -> Unit)? = null
) {
    companion object {
        private const val TAG = "RemoteServer"
    }

    private var serverSocket: ServerSocket? = null

    @Volatile
    private var isRunning = false
    private var serverJob: Job? = null

    fun start(scope: CoroutineScope) {
        if (isRunning) return
        serverJob = scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(port)
                isRunning = true
                Log.d(TAG, "Remote Server started on port $port")
                while (isRunning) {
                    try {
                        val client = serverSocket?.accept() ?: break
                        handleClient(client)
                    } catch (e: Exception) {
                        if (isRunning) {
                            Log.e(TAG, "Error accepting client: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Server error: ${e.message}")
                isRunning = false
            }
        }
    }

    fun stop() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing server: ${e.message}")
        }
        serverSocket = null
        serverJob?.cancel()
        Log.d(TAG, "Remote Server stopped")
    }

    fun isRunning(): Boolean = isRunning

    private fun handleClient(client: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(client.getInputStream()))
            val writer = OutputStreamWriter(client.getOutputStream())

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) {
                client.close()
                return
            }

            val method = parts[0]
            val path = parts[1]

            // ============ BACA HEADER ============
            val headers = mutableMapOf<String, String>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) break
                val headerParts = line!!.split(":", limit = 2)
                if (headerParts.size == 2) {
                    headers[headerParts[0].trim().lowercase()] = headerParts[1].trim()
                }
            }

            // ============ BACA BODY ============
            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            val body = if (contentLength > 0) {
                val buffer = CharArray(contentLength)
                reader.read(buffer, 0, contentLength)
                String(buffer)
            } else ""

            // ============ AUTH ============
            val authHeader = headers["authorization"]
            val urlToken = path.substringAfter("token=", "").substringBefore("&")
            val isAuthorized = authHeader == "Bearer $authToken" ||
                    urlToken == authToken ||
                    path == "/"
            if (!isAuthorized) {
                sendResponse(writer, 401, "text/plain", "Unauthorized")
                client.close()
                return
            }

            // ============ ROUTING ============
            when {
                // ---- Remote Control lama ----
                method == "GET" && path == "/" -> {
                    sendResponse(writer, 200, "text/html", getDashboardHtml())
                }
                method == "GET" && path.startsWith("/api/status") -> {
                    sendResponse(writer, 200, "application/json", getStatusJson())
                }
                method == "GET" && path.startsWith("/api/settings") -> {
                    sendResponse(writer, 200, "application/json", getSettingsJson())
                }
                method == "POST" && path.startsWith("/api/running-text") -> {
                    val params = parseFormData(body)
                    val text = params["text"] ?: ""
                    updateRunningText(text)
                    sendResponse(writer, 200, "application/json", """{"success":true}""")
                }
                method == "POST" && path.startsWith("/api/pin") -> {
                    val params = parseFormData(body)
                    val newPin = params["pin"] ?: ""
                    if (newPin.length == 4 && newPin.all { it.isDigit() }) {
                        updatePin(newPin)
                        sendResponse(writer, 200, "application/json", """{"success":true}""")
                    } else {
                        sendResponse(writer, 400, "application/json", """{"success":false,"error":"PIN harus 4 digit"}""")
                    }
                }
                method == "POST" && path.startsWith("/api/restart") -> {
                    sendResponse(writer, 200, "application/json", """{"success":true,"message":"Restart dijadwalkan"}""")
                    writer.flush()
                    client.close()
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            Log.d(TAG, "Restarting app via Remote Control...")
                            if (onRestart != null) {
                                onRestart.invoke()
                            } else {
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Restart error: ${e.message}")
                        }
                    }, 1000)
                    return
                }
                
                // ---- iO Control: handshake (cek device hidup) ----
                method == "POST" && path.startsWith("/api/io/handshake") -> {
                    sendResponse(
                        writer, 200, "application/json",
                        """{"success":true,"app":"MASJID.IO"}"""
                    )
                }

                // ---- iO Control: terima settings lengkap ----
                method == "POST" && path.startsWith("/api/io/receive") -> {
                    // Kirim balasan OK cepat supaya client tahu sudah diterima
                    sendResponse(
                        writer, 200, "application/json",
                        """{"success":true,"message":"Settings diterima"}"""
                    )
                    writer.flush()
                    client.close()

                    // Proses di main thread (apply settings + restart)
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            Log.d(TAG, "Menerima settings iO Control, length=${body.length}")
                            if (onSettingsReceived != null) {
                                onSettingsReceived.invoke(body)
                            }
                            // Restart setelah settings diterapkan
                            Handler(Looper.getMainLooper()).postDelayed({
                                try {
                                    Log.d(TAG, "Restart otomatis setelah terima settings...")
                                    if (onRestart != null) {
                                        onRestart.invoke()
                                    } else {
                                        android.os.Process.killProcess(android.os.Process.myPid())
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Restart error setelah receive: ${e.message}")
                                }
                            }, 1500)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error process received settings: ${e.message}")
                        }
                    }, 200)
                    return
                }

                // ---- Default: 404 ----
                else -> {
                    sendResponse(writer, 404, "text/plain", "Not Found")
                }
            }

            writer.flush()
            client.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error handling client: ${e.message}")
            try {
                client.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun sendResponse(
        writer: OutputStreamWriter,
        code: Int,
        contentType: String,
        body: String
    ) {
        val statusText = when (code) {
            200 -> "OK"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            404 -> "Not Found"
            else -> "Unknown"
        }

        val bodyBytes = body.toByteArray(Charsets.UTF_8)
        writer.write("HTTP/1.1 $code $statusText\r\n")
        writer.write("Content-Type: $contentType; charset=utf-8\r\n")
        writer.write("Content-Length: ${bodyBytes.size}\r\n")
        writer.write("Access-Control-Allow-Origin: *\r\n")
        writer.write("Connection: close\r\n")
        writer.write("\r\n")
        writer.write(body)
    }

    private fun parseFormData(body: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        body.split("&").forEach { pair ->
            val parts = pair.split("=", limit = 2)
            if (parts.size == 2) {
                result[URLDecoder.decode(parts[0], "UTF-8")] = URLDecoder.decode(parts[1], "UTF-8")
            }
        }
        return result
    }

    private fun updateRunningText(text: String) {
        val current = settingsRepository.settingsFlow.value
        settingsRepository.updateSettings(current.copy(runningText = text))
        Log.d(TAG, "Running text updated")
    }

    private fun updatePin(newPin: String) {
        val current = settingsRepository.settingsFlow.value
        settingsRepository.updateSettings(current.copy(pinCode = newPin))
        Log.d(TAG, "PIN updated")
    }

    private fun getStatusJson(): String {
        val settings = settingsRepository.settingsFlow.value
        return """
            {
                "app":"MASJID.IO",
                "mosque":"${settings.mosqueName}",
                "city":"${settings.city}",
                "kioskMode":${settings.kioskModeEnabled},
                "waReport":${settings.whatsappReportEnabled}
            }
        """.trimIndent()
    }

    private fun getSettingsJson(): String {
        val s = settingsRepository.settingsFlow.value
        return """
            {
                "mosqueName":"${s.mosqueName}",
                "city":"${s.city}",
                "runningText":"${s.runningText.replace("\"", "\\\"")}",
                "pinCode":"****",
                "kioskMode":${s.kioskModeEnabled},
                "audioMode":"${s.audioMode}"
            }
        """.trimIndent()
    }

    private fun getDashboardHtml(): String = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>MASJID.IO Remote</title>
            <style>
                body { font-family: Arial, sans-serif; background: #0A1929; color: #fff; padding: 20px; }
                h1 { color: #FFD700; }
                h2 { color: #64B5F6; }
                button { background: #2196F3; color: #fff; border: none; padding: 12px 24px; border-radius: 8px; cursor: pointer; }
                input, textarea { background: #132F4C; color: #fff; border: 1px solid #2196F3; padding: 8px; border-radius: 6px; width: 100%; }
                .card { background: #132F4C; padding: 16px; border-radius: 12px; margin-bottom: 16px; }
            </style>
        </head>
        <body>
            <h1>MASJID.IO Remote</h1>

            <div class="card">
                <h2>Running Text</h2>
                <textarea id="rt" rows="3"></textarea>
                <br><br>
                <button onclick="saveRT()">SIMPAN</button>
            </div>

            <div class="card">
                <h2>PIN Pengaturan</h2>
                <input id="pin" maxlength="4" placeholder="4 digit">
                <br><br>
                <button onclick="savePin()">GANTI PIN</button>
            </div>

            <div class="card">
                <h2>Status</h2>
                <div id="status">Loading...</div>
            </div>

            <div class="card">
                <h2>Restart Aplikasi</h2>
                <p>Restart aplikasi MASJID.IO. Aplikasi akan menutup dan membuka lagi.</p>
                <button onclick="restart()">RESTART SEKARANG</button>
            </div>

            <div class="card">
                <h2>ℹ️ Info</h2>
                <p>Remote ini untuk mengatur aplikasi MASJID.IO dari HP.<br>
                Pastikan HP dan TV dalam 1 WiFi yang sama.</p>
            </div>

            <script>
                const token = new URLSearchParams(location.search).get('token') || '';
                fetch('/api/status?token=' + token).then(r => r.json()).then(d => {
                    document.getElementById('status').textContent = JSON.stringify(d, null, 2);
                });
                fetch('/api/settings?token=' + token).then(r => r.json()).then(d => {
                    document.getElementById('rt').value = d.runningText || '';
                });
                function saveRT() {
                    const text = document.getElementById('rt').value;
                    fetch('/api/running-text?token=' + token, {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        body: 'text=' + encodeURIComponent(text)
                    }).then(r => r.json()).then(d => alert(JSON.stringify(d)));
                }
                function savePin() {
                    const pin = document.getElementById('pin').value;
                    fetch('/api/pin?token=' + token, {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        body: 'pin=' + encodeURIComponent(pin)
                    }).then(r => r.json()).then(d => alert(JSON.stringify(d)));
                }
                function restart() {
                    if (!confirm('Yakin restart aplikasi?')) return;
                    fetch('/api/restart?token=' + token, {method: 'POST'});
                }
            </script>
        </body>
        </html>
    """.trimIndent()
}
