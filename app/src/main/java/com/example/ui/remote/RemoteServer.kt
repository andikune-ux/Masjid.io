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
 * Endpoint:
 * - GET  /                    -> Dashboard HTML
 * - GET  /api/status          -> JSON status app
 * - GET  /api/settings        -> JSON semua settings
 * - POST /api/running-text    -> Update running text
 * - POST /api/pin             -> Update PIN
 * - POST /api/restart         -> Restart app
 */
class RemoteServer(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val port: Int = 8080,
    private val authToken: String = "masjid-io",
    private val onRestart: (() -> Unit)? = null
) {

    companion object {
        private const val TAG = "RemoteServer"
    }

    private var serverSocket: ServerSocket? = null
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

    // ============================================================
    // HANDLE CLIENT
    // ============================================================

    private fun handleClient(client: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(client.getInputStream()))
            val writer = OutputStreamWriter(client.getOutputStream())

            // Baca request line
            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) {
                client.close()
                return
            }

            val method = parts[0]
            val path = parts[1]

            // Baca headers
            val headers = mutableMapOf<String, String>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) break
                val headerParts = line!!.split(":", limit = 2)
                if (headerParts.size == 2) {
                    headers[headerParts[0].trim().lowercase()] = headerParts[1].trim()
                }
            }

            // Baca body
            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            val body = if (contentLength > 0) {
                val buffer = CharArray(contentLength)
                reader.read(buffer, 0, contentLength)
                String(buffer)
            } else ""

            // Auth check
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

            // Routing
            when {
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
                        sendResponse(writer, 400, "application/json",
                            """{"success":false,"error":"PIN harus 4 digit"}""")
                    }
                }
                method == "POST" && path.startsWith("/api/restart") -> {
                    // Kirim response dulu, baru restart
                    sendResponse(writer, 200, "application/json",
                        """{"success":true,"message":"Restart dijadwalkan"}""")
                    writer.flush()
                    client.close()

                    // Restart setelah 1 detik (biar response terkirim)
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            Log.d(TAG, "Restarting app via Remote Control...")
                            if (onRestart != null) {
                                onRestart.invoke()
                            } else {
                                // Fallback: kill process
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Restart error: ${e.message}")
                        }
                    }, 1000)
                    return
                }
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
            } catch (_: Exception) { }
        }
    }

    // ============================================================
    // RESPONSE HELPER
    // ============================================================

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
                result[URLDecoder.decode(parts[0], "UTF-8")] =
                    URLDecoder.decode(parts[1], "UTF-8")
            }
        }
        return result
    }

    // ============================================================
    // API HANDLERS
    // ============================================================

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

    // ============================================================
    // DASHBOARD HTML
    // ============================================================

    private fun getDashboardHtml(): String = """
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>MASJID.IO Remote</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
    background: linear-gradient(135deg, #0D1B2A, #1B263B);
    color: #fff;
    min-height: 100vh;
    padding: 20px;
  }
  .container { max-width: 600px; margin: 0 auto; }
  h1 { color: #D4AF37; text-align: center; margin-bottom: 20px; }
  .card {
    background: rgba(0,0,0,0.4);
    border: 1px solid rgba(212,175,55,0.4);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 16px;
  }
  .card h2 { color: #D4AF37; font-size: 16px; margin-bottom: 12px; }
  input, textarea, button {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: 1px solid rgba(255,255,255,0.2);
    background: rgba(0,0,0,0.3);
    color: #fff;
    font-size: 14px;
    margin-bottom: 10px;
  }
  button {
    background: #D4AF37;
    color: #09141D;
    font-weight: bold;
    border: none;
    cursor: pointer;
  }
  button:active { transform: scale(0.98); }
  button.danger {
    background: #C62828;
    color: #fff;
  }
  .status { padding: 10px; border-radius: 8px; margin-top: 10px; display: none; }
  .status.success { background: #2E7D32; display: block; }
  .status.error { background: #C62828; display: block; }
</style>
</head>
<body>
  <div class="container">
    <h1>🕌 MASJID.IO Remote</h1>

    <div class="card">
      <h2>📝 Running Text</h2>
      <textarea id="runningText" rows="4" placeholder="Masukkan teks berjalan..."></textarea>
      <button onclick="updateRunningText()">SIMPAN</button>
    </div>

    <div class="card">
      <h2>🔒 PIN Pengaturan</h2>
      <input type="text" id="pin" maxlength="4" inputmode="numeric" placeholder="4 digit PIN baru">
      <button onclick="updatePin()">GANTI PIN</button>
    </div>

    <div class="card">
      <h2>📊 Status</h2>
      <div id="status"></div>
    </div>

    <div class="card">
      <h2>🔄 Restart Aplikasi</h2>
      <p style="font-size:13px;color:#aaa;margin-bottom:10px;">
        Restart aplikasi MASJID.IO. Aplikasi akan menutup dan membuka lagi.
      </p>
      <button class="danger" onclick="restartApp()">RESTART SEKARANG</button>
    </div>

    <div class="card">
      <h2>ℹ️ Info</h2>
      <p style="font-size:13px; color:#aaa;">
        Remote ini untuk mengatur aplikasi MASJID.IO dari HP.<br>
        Pastikan HP dan TV dalam 1 WiFi yang sama.
      </p>
    </div>
  </div>

<script>
const TOKEN = prompt('Masukkan Token Remote:') || '';
const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };

async function updateRunningText() {
  const text = document.getElementById('runningText').value;
  try {
    const res = await fetch('/api/running-text?token=' + TOKEN, {
      method: 'POST',
      headers,
      body: 'text=' + encodeURIComponent(text)
    });
    showStatus(res.ok ? 'Teks berhasil disimpan!' : 'Gagal simpan', res.ok);
  } catch(e) {
    showStatus('Error: ' + e.message, false);
  }
}

async function updatePin() {
  const pin = document.getElementById('pin').value;
  if (pin.length !== 4) { showStatus('PIN harus 4 digit', false); return; }
  try {
    const res = await fetch('/api/pin?token=' + TOKEN, {
      method: 'POST',
      headers,
      body: 'pin=' + pin
    });
    showStatus(res.ok ? 'PIN berhasil diubah!' : 'Gagal ubah PIN', res.ok);
  } catch(e) {
    showStatus('Error: ' + e.message, false);
  }
}

async function restartApp() {
  if (!confirm('Yakin mau restart aplikasi MASJID.IO?')) return;
  try {
    await fetch('/api/restart?token=' + TOKEN, { method: 'POST' });
    showStatus('Restart dijadwalkan. Aplikasi akan menutup...', true);
  } catch(e) {
    showStatus('Error: ' + e.message, false);
  }
}

function showStatus(msg, ok) {
  const el = document.getElementById('status');
  el.textContent = msg;
  el.className = 'status ' + (ok ? 'success' : 'error');
  setTimeout(() => { el.className = 'status'; }, 3000);
}

async function loadStatus() {
  try {
    const res = await fetch('/api/status?token=' + TOKEN);
    const data = await res.json();
    document.getElementById('status').innerHTML =
      '🕌 ' + data.mosque + '<br>📍 ' + data.city;
  } catch(e) {}
}
loadStatus();
</script>
</body>
</html>
    """.trimIndent()
}
