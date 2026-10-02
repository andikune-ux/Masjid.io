package com.example.ui.remote

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import com.example.data.local.SettingsRepository
import com.example.data.model.AudioMode
import com.example.data.model.BackgroundMode
import com.example.data.model.CctvPosition
import com.example.data.model.DailyOfficerItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.BindException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.URLDecoder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class RemoteServer(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val port: Int = 14039,
    private val authToken: String = "masjid-io",
    private val onRestart: (() -> Unit)? = null,
    private val onSettingsReceived: ((String) -> Unit)? = null,
    private val onFinalize: (() -> Unit)? = null
) {
    companion object {
        private const val TAG = "RemoteServer"
        private const val MAX_RETRY = 3
        private const val RETRY_DELAY_MS = 500L
        private const val SESSION_TTL_MS = 24 * 60 * 60 * 1000L
    }

    private var serverSocket: ServerSocket? = null

    @Volatile
    private var isRunning = false
    private var serverJob: Job? = null

    @Volatile
    var lastError: String? = null
        private set

    @Volatile
    var actualPort: Int = -1
        private set

    private val sessions = ConcurrentHashMap<String, Long>()

    private data class MediaTransferState(
        val fileId: String,
        val fieldKey: String,
        val fileName: String,
        val fileType: String,
        val totalChunks: Int,
        val mimeType: String,
        val receivedChunks: MutableMap<Int, ByteArray> = ConcurrentHashMap(),
        var isFinished: Boolean = false,
        var savedPath: String? = null
    )

    private val mediaTransfers = ConcurrentHashMap<String, MediaTransferState>()

    private fun createSession(): String {
        val id = UUID.randomUUID().toString()
        sessions[id] = System.currentTimeMillis() + SESSION_TTL_MS
        cleanupExpiredSessions()
        return id
    }

    private fun isValidSession(id: String?): Boolean {
        if (id.isNullOrBlank()) return false
        val expiry = sessions[id] ?: return false
        if (expiry < System.currentTimeMillis()) {
            sessions.remove(id)
            return false
        }
        return true
    }

    private fun cleanupExpiredSessions() {
        val now = System.currentTimeMillis()
        sessions.entries.removeAll { it.value < now }
    }

    fun start(scope: CoroutineScope) {
        if (isRunning) return
        serverJob = scope.launch(Dispatchers.IO) {
            cleanupSocket()
            var bound: ServerSocket? = null
            var lastException: Exception? = null

            for (attempt in 1..MAX_RETRY) {
                try {
                    val socket = ServerSocket()
                    socket.reuseAddress = true
                    socket.bind(InetSocketAddress(port))
                    bound = socket
                    break
                } catch (e: Exception) {
                    lastException = e
                    delay(RETRY_DELAY_MS)
                }
            }

            if (bound == null) {
                isRunning = false
                actualPort = -1
                lastError = buildErrorMessage(lastException)
                return@launch
            }

            serverSocket = bound
            isRunning = true
            actualPort = port
            lastError = null

            while (isRunning) {
                try {
                    val client = serverSocket?.accept() ?: break
                    handleClient(client)
                } catch (e: Exception) {
                    if (isRunning) Log.e(TAG, "Accept error: ${e.message}")
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        cleanupSocket()
        serverJob?.cancel()
        serverJob = null
        sessions.clear()
        mediaTransfers.clear()
    }

    private fun cleanupSocket() {
        try {
            serverSocket?.let { if (!it.isClosed) it.close() }
        } catch (_: Exception) {}
        serverSocket = null
        actualPort = -1
    }

    private fun buildErrorMessage(e: Exception?): String {
        if (e == null) return "Server gagal start tanpa error jelas"
        return when (e) {
            is BindException -> {
                val msg = e.message ?: ""
                if (msg.contains("Address already in use", true) ||
                    msg.contains("EADDRINUSE", true)) {
                    "Port $port SUDAH DIPAKAI aplikasi lain.\n\nSolusi: Force close aplikasi lain / restart HP/TV."
                } else "Gagal bind port $port: $msg"
            }
            is SocketException -> "Socket error di port $port: ${e.message}"
            else -> "${e.javaClass.simpleName}: ${e.message ?: "unknown"}"
        }
    }

    fun isRunning(): Boolean = isRunning

    private fun handleClient(client: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(client.getInputStream()))
            val writer = OutputStreamWriter(client.getOutputStream())

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) { client.close(); return }

            val method = parts[0]
            val fullPath = parts[1]

            val pathOnly: String
            val qIndex = fullPath.indexOf('?')
            pathOnly = if (qIndex >= 0) fullPath.substring(0, qIndex) else fullPath

            val headers = mutableMapOf<String, String>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) break
                val hp = line!!.split(":", limit = 2)
                if (hp.size == 2) headers[hp[0].trim().lowercase()] = hp[1].trim()
            }

            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            val body = if (contentLength > 0) {
                val buf = CharArray(contentLength)
                reader.read(buf, 0, contentLength)
                String(buf)
            } else ""

            val cookieHeader = headers["cookie"] ?: ""
            val sessionId = cookieHeader.split(";")
                .mapNotNull { c ->
                    val kv = c.trim().split("=", limit = 2)
                    if (kv.size == 2 && kv[0] == "session") kv[1] else null
                }.firstOrNull()

            val isLoggedIn = isValidSession(sessionId)

            val isPublicRoute = pathOnly == "/login" ||
                    pathOnly == "/api/login" ||
                    pathOnly == "/api/io/handshake" ||
                    pathOnly == "/api/io/receive" ||
                    pathOnly == "/api/io/receive-media-start" ||
                    pathOnly == "/api/io/receive-media-chunk" ||
                    pathOnly == "/api/io/receive-media-finish" ||
                    pathOnly == "/api/io/finalize" ||
                    pathOnly == "/api/io/media-status"

            if (!isPublicRoute && !isLoggedIn) {
                if (pathOnly == "/" || pathOnly.isEmpty()) {
                    sendRedirect(writer, "/login")
                } else {
                    sendResponse(writer, 401, "application/json",
                        """{"success":false,"error":"Unauthorized","needLogin":true}""")
                }
                writer.flush()
                client.close()
                return
            }
            
        when {
            method == "GET" && pathOnly == "/login" -> {
                sendResponse(writer, 200, "text/html", getLoginHtml())
            }
            method == "POST" && pathOnly == "/api/login" -> {
                val params = parseFormData(body)
                val password = params["password"] ?: ""
                val correctPin = settingsRepository.settingsFlow.value.pinCode
                if (password == correctPin) {
                    val newSession = createSession()
                    sendResponseWithCookie(
                        writer, 200, "application/json",
                        """{"success":true,"message":"Login berhasil"}""",
                        "session=$newSession; Path=/; Max-Age=86400; SameSite=Strict"
                    )
                } else {
                    sendResponse(writer, 401, "application/json",
                        """{"success":false,"error":"PIN salah"}""")
                }
            }
            method == "POST" && pathOnly == "/api/logout" -> {
                sessionId?.let { sessions.remove(it) }
                sendResponseWithCookie(writer, 200, "application/json",
                    """{"success":true}""",
                    "session=; Path=/; Max-Age=0")
            }
            method == "GET" && (pathOnly == "/" || pathOnly.isEmpty()) -> {
                sendResponse(writer, 200, "text/html", getDashboardHtml())
            }
            method == "GET" && pathOnly == "/api/settings" -> {
                sendResponse(writer, 200, "application/json", getFullSettingsJson())
            }
            method == "POST" && pathOnly == "/api/settings" -> {
                try {
                    val json = JSONObject(body)
                    val updated = applySettingsUpdate(json)
                    settingsRepository.updateSettings(updated)
                    sendResponse(writer, 200, "application/json", """{"success":true}""")
                } catch (e: Exception) {
                    Log.e(TAG, "Update settings error: ${e.message}")
                    sendResponse(writer, 400, "application/json",
                        """{"success":false,"error":"${e.message?.replace("\"", "\\\"")}"}""")
                }
            }
            method == "POST" && pathOnly == "/api/upload" -> {
                try {
                    val params = parseFormData(body)
                    val type = params["type"] ?: "unknown"
                    val base64Data = params["data"] ?: ""
                    val fileName = params["name"] ?: "file_${System.currentTimeMillis()}"

                    if (base64Data.isBlank()) {
                        sendResponse(writer, 400, "application/json",
                            """{"success":false,"error":"Data kosong"}""")
                    } else {
                        val savedPath = saveUploadedFile(type, fileName, base64Data)
                        if (savedPath != null) {
                            sendResponse(writer, 200, "application/json",
                                """{"success":true,"path":"$savedPath"}""")
                        } else {
                            sendResponse(writer, 500, "application/json",
                                """{"success":false,"error":"Gagal simpan file"}""")
                        }
                    }
                } catch (e: Exception) {
                    sendResponse(writer, 500, "application/json",
                        """{"success":false,"error":"${e.message?.replace("\"", "\\\"")}"}""")
                }
            }
            method == "POST" && pathOnly == "/api/running-text" -> {
                val params = parseFormData(body)
                val text = params["text"] ?: ""
                val current = settingsRepository.settingsFlow.value
                settingsRepository.updateSettings(current.copy(runningText = text))
                sendResponse(writer, 200, "application/json", """{"success":true}""")
            }
            method == "POST" && pathOnly == "/api/pin" -> {
                val params = parseFormData(body)
                val newPin = params["pin"] ?: ""
                if (newPin.length == 4 && newPin.all { it.isDigit() }) {
                    val current = settingsRepository.settingsFlow.value
                    settingsRepository.updateSettings(current.copy(pinCode = newPin))
                    sendResponse(writer, 200, "application/json", """{"success":true}""")
                } else {
                    sendResponse(writer, 400, "application/json",
                        """{"success":false,"error":"PIN harus 4 digit"}""")
                }
            }
            method == "GET" && pathOnly == "/api/status" -> {
                sendResponse(writer, 200, "application/json", getStatusJson())
            }
            method == "POST" && pathOnly == "/api/restart" -> {
                sendResponse(writer, 200, "application/json",
                    """{"success":true,"message":"Restart dijadwalkan"}""")
                writer.flush(); client.close()
                Handler(Looper.getMainLooper()).postDelayed({
                    if (onRestart != null) onRestart.invoke()
                    else android.os.Process.killProcess(android.os.Process.myPid())
                }, 1000)
                return
            }

            // ============================================================
            // ENDPOINT iO CONTROL
            // ============================================================
            method == "POST" && pathOnly == "/api/io/handshake" -> {
                sendResponse(writer, 200, "application/json",
                    """{"success":true,"app":"MASJID.IO","port":$actualPort}""")
            }
            method == "POST" && pathOnly == "/api/io/receive" -> {
                try {
                    Log.d(TAG, "📥 Menerima settings dari pengirim")
                    if (onSettingsReceived != null) {
                        onSettingsReceived.invoke(body)
                    }
                    sendResponse(writer, 200, "application/json",
                        """{"success":true,"message":"Settings diterima, menunggu media..."}""")
                    Log.d(TAG, "✅ Settings di-apply. Menunggu media + finalize.")
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal apply settings: ${e.message}")
                    sendResponse(writer, 500, "application/json",
                        """{"success":false,"error":"${e.message?.replace("\"", "\\\"")}"}""")
                }
            }

            // ============================================================
            // MEDIA TRANSFER ENDPOINTS
            // ============================================================
            method == "POST" && pathOnly == "/api/io/receive-media-start" -> {
                try {
                    val json = JSONObject(body)
                    val fileId = json.optString("fileId", "")
                    val fieldKey = json.optString("fieldKey", "")
                    val fileName = json.optString("fileName", "")
                    val fileType = json.optString("fileType", "photo")
                    val totalChunks = json.optInt("totalChunks", 0)
                    val mimeType = json.optString("mimeType", "application/octet-stream")

                    if (fileId.isBlank() || totalChunks <= 0) {
                        sendResponse(writer, 400, "application/json",
                            """{"success":false,"error":"fileId atau totalChunks invalid"}""")
                    } else {
                        mediaTransfers[fileId] = MediaTransferState(
                            fileId = fileId,
                            fieldKey = fieldKey,
                            fileName = fileName,
                            fileType = fileType,
                            totalChunks = totalChunks,
                            mimeType = mimeType
                        )
                        Log.d(TAG, "Media start: $fileId ($fileName, $totalChunks chunks)")
                        sendResponse(writer, 200, "application/json",
                            """{"success":true,"fileId":"$fileId"}""")
                    }
                } catch (e: Exception) {
                    sendResponse(writer, 400, "application/json",
                        """{"success":false,"error":"${e.message?.replace("\"", "\\\"")}"}""")
                }
            }
            method == "POST" && pathOnly == "/api/io/receive-media-chunk" -> {
                try {
                    val fileId = headers["x-file-id"] ?: ""
                    val chunkIndex = headers["x-chunk-index"]?.toIntOrNull() ?: -1

                    if (fileId.isBlank() || chunkIndex < 0) {
                        sendResponse(writer, 400, "application/json",
                            """{"success":false,"error":"Header invalid"}""")
                    } else {
                        val state = mediaTransfers[fileId]
                        if (state == null) {
                            sendResponse(writer, 404, "application/json",
                                """{"success":false,"error":"fileId tidak ditemukan"}""")
                        } else {
                            val chunkBytes = try {
                                Base64.decode(body, Base64.NO_WRAP)
                            } catch (e: Exception) {
                                ByteArray(0)
                            }

                            if (chunkBytes.isEmpty()) {
                                sendResponse(writer, 400, "application/json",
                                    """{"success":false,"error":"Chunk kosong"}""")
                            } else {
                                state.receivedChunks[chunkIndex] = chunkBytes
                                Log.d(TAG, "Chunk $chunkIndex/${state.totalChunks} untuk ${state.fileName} (${chunkBytes.size} bytes)")
                                sendResponse(writer, 200, "application/json",
                                    """{"success":true,"received":${state.receivedChunks.size},"total":${state.totalChunks}}""")
                            }
                        }
                    }
                } catch (e: Exception) {
                    sendResponse(writer, 500, "application/json",
                        """{"success":false,"error":"${e.message?.replace("\"", "\\\"")}"}""")
                }
            }
            method == "POST" && pathOnly == "/api/io/receive-media-finish" -> {
                try {
                    val json = JSONObject(body)
                    val fileId = json.optString("fileId", "")
                    val state = mediaTransfers[fileId]

                    if (state == null) {
                        sendResponse(writer, 404, "application/json",
                            """{"success":false,"error":"fileId tidak ditemukan"}""")
                    } else if (state.receivedChunks.size < state.totalChunks) {
                        sendResponse(writer, 400, "application/json",
                            """{"success":false,"error":"Chunk belum lengkap: ${state.receivedChunks.size}/${state.totalChunks}"}""")
                    } else {
                        val fullBytes = try {
                            val output = ByteArrayOutputStream()
                            for (i in 0 until state.totalChunks) {
                                val chunk = state.receivedChunks[i]
                                if (chunk == null) {
                                    sendResponse(writer, 400, "application/json",
                                        """{"success":false,"error":"Chunk $i hilang"}""")
                                    writer.flush()
                                    return
                                }
                                output.write(chunk)
                            }
                            output.toByteArray()
                        } catch (e: Exception) {
                            sendResponse(writer, 500, "application/json",
                                """{"success":false,"error":"Gagal gabung chunk: ${e.message}"}""")
                            writer.flush()
                            return
                        }

                        val folder = when {
                            state.fieldKey.contains("video", true) -> "video"
                            state.fieldKey.contains("qris", true) -> "qris"
                            state.fieldKey.contains("officer", true) -> "logo"
                            state.fieldKey.contains("background", true) -> "background"
                            state.fieldKey.contains("prayer", true) -> "prayer_card"
                            state.fieldKey.contains("slideshow", true) -> "slideshow"
                            else -> "media"
                        }

                        val savedPath = saveMediaFile(context, folder, state.fileName, fullBytes)

                        if (savedPath != null) {
                            state.isFinished = true
                            state.savedPath = savedPath
                            Log.d(TAG, "Media finish: ${state.fileName} → $savedPath")

                            // ============================================================
                            // FIX: Update settings TV dengan path LOKAL baru
                            // ============================================================
                            applyMediaPathToSettings(state.fieldKey, savedPath)

                            sendResponse(writer, 200, "application/json",
                                """{"success":true,"path":"$savedPath","size":${fullBytes.size}}""")
                        } else {
                            sendResponse(writer, 500, "application/json",
                                """{"success":false,"error":"Gagal simpan file"}""")
                        }
                    }
                } catch (e: Exception) {
                    sendResponse(writer, 500, "application/json",
                        """{"success":false,"error":"${e.message?.replace("\"", "\\\"")}"}""")
                }
            }
            method == "GET" && pathOnly == "/api/io/media-status" -> {
                val arr = JSONArray()
                mediaTransfers.values.forEach { st ->
                    arr.put(JSONObject().apply {
                        put("fileId", st.fileId)
                        put("fileName", st.fileName)
                        put("received", st.receivedChunks.size)
                        put("total", st.totalChunks)
                        put("finished", st.isFinished)
                        put("savedPath", st.savedPath ?: "")
                    })
                }
                sendResponse(writer, 200, "application/json", arr.toString())
            }

            // ============================================================
            // FINALIZE
            // ============================================================
            method == "POST" && pathOnly == "/api/io/finalize" -> {
                Log.d(TAG, "🎬 Menerima sinyal FINALIZE dari pengirim")
                sendResponse(writer, 200, "application/json",
                    """{"success":true,"message":"Finalize diterima, mulai countdown restart"}""")
                writer.flush(); client.close()

                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        if (onFinalize != null) {
                            onFinalize.invoke()
                            Log.d(TAG, "✅ onFinalize dipanggil — countdown dimulai")
                        } else {
                            Log.w(TAG, "⚠️ onFinalize null — fallback restart langsung")
                            if (onRestart != null) onRestart.invoke()
                            else android.os.Process.killProcess(android.os.Process.myPid())
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Finalize error: ${e.message}")
                    }
                }, 300)
                return
            }

            else -> {
                sendResponse(writer, 404, "text/plain", "Not Found: $pathOnly")
            }
        }

        writer.flush()
        client.close()
    } catch (e: Exception) {
        Log.e(TAG, "Handle error: ${e.message}")
        try { client.close() } catch (_: Exception) {}
    }
    }
    
// ============================================================
// SEND RESPONSE HELPERS
// ============================================================
private fun sendResponse(writer: OutputStreamWriter, code: Int, contentType: String, body: String) {
    val statusText = when (code) {
        200 -> "OK"; 400 -> "Bad Request"; 401 -> "Unauthorized"
        404 -> "Not Found"; 500 -> "Internal Server Error"; else -> "Unknown"
    }
    val bodyBytes = body.toByteArray(Charsets.UTF_8)
    writer.write("HTTP/1.1 $code $statusText\r\n")
    writer.write("Content-Type: $contentType; charset=utf-8\r\n")
    writer.write("Content-Length: ${bodyBytes.size}\r\n")
    writer.write("Access-Control-Allow-Origin: *\r\n")
    writer.write("Connection: close\r\n\r\n")
    writer.write(body)
}

private fun sendResponseWithCookie(
    writer: OutputStreamWriter, code: Int, contentType: String,
    body: String, setCookie: String
) {
    val statusText = when (code) {
        200 -> "OK"; 400 -> "Bad Request"; 401 -> "Unauthorized"; else -> "Unknown"
    }
    val bodyBytes = body.toByteArray(Charsets.UTF_8)
    writer.write("HTTP/1.1 $code $statusText\r\n")
    writer.write("Content-Type: $contentType; charset=utf-8\r\n")
    writer.write("Content-Length: ${bodyBytes.size}\r\n")
    writer.write("Set-Cookie: $setCookie\r\n")
    writer.write("Access-Control-Allow-Origin: *\r\n")
    writer.write("Connection: close\r\n\r\n")
    writer.write(body)
}

private fun sendRedirect(writer: OutputStreamWriter, location: String) {
    writer.write("HTTP/1.1 302 Found\r\n")
    writer.write("Location: $location\r\n")
    writer.write("Content-Length: 0\r\n")
    writer.write("Connection: close\r\n\r\n")
}

// ============================================================
// PARSE FORM DATA
// ============================================================
private fun parseFormData(body: String): Map<String, String> {
    val result = mutableMapOf<String, String>()
    body.split("&").forEach { pair ->
        val p = pair.split("=", limit = 2)
        if (p.size == 2) {
            runCatching {
                result[URLDecoder.decode(p[0], "UTF-8")] = URLDecoder.decode(p[1], "UTF-8")
            }
        }
    }
    return result
}

// ============================================================
// APPLY SETTINGS UPDATE
// ============================================================
private fun applySettingsUpdate(json: JSONObject): com.example.data.model.AppSettings {
    var s = settingsRepository.settingsFlow.value

    json.keys().forEach { key ->
        try {
            when (key) {
                "mosqueName" -> s = s.copy(mosqueName = json.getString(key))
                "mosqueAddress" -> s = s.copy(mosqueAddress = json.getString(key))
                "mosqueTakmir" -> s = s.copy(mosqueTakmir = json.getString(key))
                "runningText" -> s = s.copy(runningText = json.getString(key))
                "runningTextSpeed" -> s = s.copy(runningTextSpeed = json.getInt(key))
                "runningTextFontSize" -> s = s.copy(runningTextFontSize = json.getInt(key))
                "animationsEnabled" -> s = s.copy(animationsEnabled = json.getBoolean(key))
                "showBirdsAnimation" -> s = s.copy(showBirdsAnimation = json.getBoolean(key))
                "keepScreenOn" -> s = s.copy(keepScreenOn = json.getBoolean(key))
                "tvAutoScaleEnabled" -> s = s.copy(tvAutoScaleEnabled = json.getBoolean(key))
                "tvSafeAreaPercent" -> s = s.copy(tvSafeAreaPercent = json.getDouble(key).toFloat())
                "tvLayoutPreset" -> s = s.copy(tvLayoutPreset = json.getString(key))
                "backgroundMode" -> {
                    val mode = runCatching { BackgroundMode.valueOf(json.getString(key)) }.getOrNull()
                    if (mode != null) s = s.copy(backgroundMode = mode)
                }
                "audioMode" -> {
                    val mode = runCatching { AudioMode.valueOf(json.getString(key)) }.getOrNull()
                    if (mode != null) s = s.copy(audioMode = mode)
                }
                "beepVolume" -> s = s.copy(beepVolume = json.getInt(key))
                "beepCount" -> s = s.copy(beepCount = json.getInt(key))
                "beepDurationMs" -> s = s.copy(beepDurationMs = json.getInt(key))
                "beepIntervalMs" -> s = s.copy(beepIntervalMs = json.getInt(key))
                "adzanFile" -> s = s.copy(adzanFile = json.getString(key))
                "adzanVolume" -> s = s.copy(adzanVolume = json.getInt(key))
                "photoSlideshowEnabled" -> s = s.copy(photoSlideshowEnabled = json.getBoolean(key))
                "photoSlideshowIntervalSeconds" -> s = s.copy(photoSlideshowIntervalSeconds = json.getInt(key))
                "imamSubuh" -> s = s.copy(officers = s.officers.copy(imamSubuh = json.getString(key)))
                "muadzinSubuh" -> s = s.copy(officers = s.officers.copy(muadzinSubuh = json.getString(key)))
                "imamDzuhur" -> s = s.copy(officers = s.officers.copy(imamDzuhur = json.getString(key)))
                "muadzinDzuhur" -> s = s.copy(officers = s.officers.copy(muadzinDzuhur = json.getString(key)))
                "imamAshar" -> s = s.copy(officers = s.officers.copy(imamAshar = json.getString(key)))
                "muadzinAshar" -> s = s.copy(officers = s.officers.copy(muadzinAshar = json.getString(key)))
                "imamMaghrib" -> s = s.copy(officers = s.officers.copy(imamMaghrib = json.getString(key)))
                "muadzinMaghrib" -> s = s.copy(officers = s.officers.copy(muadzinMaghrib = json.getString(key)))
                "imamIsya" -> s = s.copy(officers = s.officers.copy(imamIsya = json.getString(key)))
                "muadzinIsya" -> s = s.copy(officers = s.officers.copy(muadzinIsya = json.getString(key)))
                "khatibJumat" -> s = s.copy(officers = s.officers.copy(khatibJumat = json.getString(key)))
                "temaJumat" -> s = s.copy(officers = s.officers.copy(temaJumat = json.getString(key)))
                "ustadzKajian" -> s = s.copy(officers = s.officers.copy(ustadzKajian = json.getString(key)))
                "jadwalKajian" -> s = s.copy(officers = s.officers.copy(jadwalKajian = json.getString(key)))
                "temaKajian" -> s = s.copy(officers = s.officers.copy(temaKajian = json.getString(key)))
                "iqamahWaitMinutes" -> s = s.copy(iqamahWaitMinutes = json.getInt(key))
                "qobliyahWaitMinutes" -> s = s.copy(qobliyahWaitMinutes = json.getInt(key))
                "adzanWaitMinutes" -> s = s.copy(adzanWaitMinutes = json.getInt(key))
                "prayerFocusDurationMinutes" -> s = s.copy(prayerFocusDurationMinutes = json.getInt(key))
                "focusModeDurationMinutes" -> s = s.copy(focusModeDurationMinutes = json.getInt(key))
                "bankName" -> s = s.copy(bankName = json.getString(key))
                "bankAccountNumber" -> s = s.copy(bankAccountNumber = json.getString(key))
                "bankAccountHolder" -> s = s.copy(bankAccountHolder = json.getString(key))
                "qrisIntervalMinutes" -> s = s.copy(qrisIntervalMinutes = json.getInt(key))
                "qrisDisplayDurationSeconds" -> s = s.copy(qrisDisplayDurationSeconds = json.getInt(key))
                "slideEnabled" -> s = s.copy(slideEnabled = json.getBoolean(key))
                "slideIntervalSeconds" -> s = s.copy(slideIntervalSeconds = json.getInt(key))
                "qrisSlideEnabled" -> s = s.copy(qrisSlideEnabled = json.getBoolean(key))
                "laporanSlideEnabled" -> s = s.copy(laporanSlideEnabled = json.getBoolean(key))
                "kajianSlideEnabled" -> s = s.copy(kajianSlideEnabled = json.getBoolean(key))
                "contentRotationEnabled" -> s = s.copy(contentRotationEnabled = json.getBoolean(key))
                "contentRotationShowAyat" -> s = s.copy(contentRotationShowAyat = json.getBoolean(key))
                "contentRotationShowHadits" -> s = s.copy(contentRotationShowHadits = json.getBoolean(key))
                "contentRotationShowAsmaulHusna" -> s = s.copy(contentRotationShowAsmaulHusna = json.getBoolean(key))
                "contentRotationIntervalSeconds" -> s = s.copy(contentRotationIntervalSeconds = json.getInt(key))
                "cctvEnabled" -> s = s.copy(cctvEnabled = json.getBoolean(key))
                "cctvUrl" -> s = s.copy(cctvUrl = json.getString(key))
                "cctvSizePercent" -> s = s.copy(cctvSizePercent = json.getInt(key))
                "cctvPosition" -> {
                    val pos = runCatching { CctvPosition.valueOf(json.getString(key)) }.getOrNull()
                    if (pos != null) s = s.copy(cctvPosition = pos)
                }
                "ramadhanModeEnabled" -> s = s.copy(ramadhanModeEnabled = json.getBoolean(key))
                "showImsakIftarCountdown" -> s = s.copy(showImsakIftarCountdown = json.getBoolean(key))
                "showTarawihSchedule" -> s = s.copy(showTarawihSchedule = json.getBoolean(key))
                "showKultumSchedule" -> s = s.copy(showKultumSchedule = json.getBoolean(key))
                "ramadhanImsakOffsetMinutes" -> s = s.copy(ramadhanImsakOffsetMinutes = json.getInt(key))
                "tarawihTime" -> s = s.copy(tarawihTime = json.getString(key))
                "tarawihImam" -> s = s.copy(tarawihImam = json.getString(key))
                "kultumTitle" -> s = s.copy(kultumTitle = json.getString(key))
                "kultumUstadz" -> s = s.copy(kultumUstadz = json.getString(key))
                "kultumTime" -> s = s.copy(kultumTime = json.getString(key))
                "menuSahurText" -> s = s.copy(menuSahurText = json.getString(key))
                "menuIftarText" -> s = s.copy(menuIftarText = json.getString(key))
                "qrisPhotoUri" -> s = s.copy(qrisPhotoUri = json.getString(key))
                "officerPhotoUri" -> s = s.copy(officerPhotoUri = json.getString(key))
                "customBackgroundUri" -> s = s.copy(customBackgroundUri = json.getString(key))
                "laporanSaldoSebelumnya" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(saldoSebelumnya = json.getLong(key)))
                "laporanPemasukanJumat" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(pemasukanJumat = json.getLong(key)))
                "laporanPemasukanUmum" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(pemasukanUmum = json.getLong(key)))
                "laporanPengeluaranDakwah" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(pengeluaranDakwah = json.getLong(key)))
                "laporanPengeluaranSosial" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(pengeluaranSosial = json.getLong(key)))
                "laporanPengeluaranOperasional" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(pengeluaranOperasional = json.getLong(key)))
                "laporanPeriodeMulai" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(periodeMulai = json.getString(key)))
                "laporanPeriodeSelesai" -> s = s.copy(laporanKeuangan = s.laporanKeuangan.copy(periodeSelesai = json.getString(key)))
                "weeklyOfficers" -> {
                    val arr = json.optJSONArray(key)
                    if (arr != null) s = s.copy(weeklyOfficers = parseWeeklyOfficers(arr))
                }
                "kioskModeEnabled" -> s = s.copy(kioskModeEnabled = json.getBoolean(key))
                "autoStartOnBoot" -> s = s.copy(autoStartOnBoot = json.getBoolean(key))
                "whatsappReportEnabled" -> s = s.copy(whatsappReportEnabled = json.getBoolean(key))
                "fonnteToken" -> s = s.copy(fonnteToken = json.getString(key))
                "fonnteGroupId" -> s = s.copy(fonnteGroupId = json.getString(key))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Field $key error: ${e.message}")
        }
    }
    return s
}

// ============================================================
// PARSE WEEKLY OFFICERS
// ============================================================
private fun parseWeeklyOfficers(arr: JSONArray): List<DailyOfficerItem> {
    val result = mutableListOf<DailyOfficerItem>()
    for (i in 0 until arr.length()) {
        val o = arr.optJSONObject(i) ?: continue
        result.add(DailyOfficerItem(
            dayName = o.optString("dayName", "Senin"),
            imamSubuh = o.optString("imamSubuh", ""),
            muadzinSubuh = o.optString("muadzinSubuh", ""),
            imamDzuhur = o.optString("imamDzuhur", ""),
            muadzinDzuhur = o.optString("muadzinDzuhur", ""),
            imamAshar = o.optString("imamAshar", ""),
            muadzinAshar = o.optString("muadzinAshar", ""),
            imamMaghrib = o.optString("imamMaghrib", ""),
            muadzinMaghrib = o.optString("muadzinMaghrib", ""),
            imamIsya = o.optString("imamIsya", ""),
            muadzinIsya = o.optString("muadzinIsya", ""),
            khatibJumat = o.optString("khatibJumat", ""),
            temaJumat = o.optString("temaJumat", ""),
            ustadzKajian = o.optString("ustadzKajian", ""),
            temaKajian = o.optString("temaKajian", "")
        ))
    }
    return result.ifEmpty { settingsRepository.settingsFlow.value.weeklyOfficers }
}

// ============================================================
// SAVE FILES
// ============================================================
private fun saveUploadedFile(type: String, fileName: String, base64Data: String): String? {
    return try {
        val bytes = Base64.decode(base64Data, Base64.NO_WRAP)
        val dirName = when (type) {
            "qris" -> "qris"; "logo" -> "logo"
            "officer" -> "officer"; "background" -> "background"
            else -> "upload"
        }
        val dir = File(context.filesDir, "masjid_io/$dirName")
        if (!dir.exists()) dir.mkdirs()
        val safeName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val file = File(dir, "upload_${System.currentTimeMillis()}_$safeName")
        FileOutputStream(file).use { it.write(bytes) }
        file.absolutePath
    } catch (e: Exception) {
        Log.e(TAG, "Save file error: ${e.message}")
        null
    }
}

private fun saveMediaFile(
    context: Context,
    folder: String,
    fileName: String,
    bytes: ByteArray
): String? {
    return try {
        val dir = File(context.filesDir, "masjid_io/$folder")
        if (!dir.exists()) dir.mkdirs()
        val safeName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val file = File(dir, "${System.currentTimeMillis()}_$safeName")
        FileOutputStream(file).use { it.write(bytes) }
        Log.d(TAG, "saveMediaFile OK: ${file.absolutePath} (${bytes.size} bytes)")
        file.absolutePath
    } catch (e: Exception) {
        Log.e(TAG, "saveMediaFile error: ${e.message}")
        null
    }
}

// ============================================================
// V1.30.6 BARU — UPDATE SETTINGS SETELAH MEDIA MASUK
// Ganti path lama (dari HP) dengan path lokal (di TV)
// ============================================================
private fun applyMediaPathToSettings(fieldKey: String, localPath: String) {
    try {
        val current = settingsRepository.settingsFlow.value
        var updated = current

        when {
            fieldKey == "qrisPhotoUri" -> {
                updated = current.copy(qrisPhotoUri = localPath)
                Log.d(TAG, "✅ qrisPhotoUri updated: $localPath")
            }
            fieldKey == "officerPhotoUri" -> {
                updated = current.copy(officerPhotoUri = localPath)
                Log.d(TAG, "✅ officerPhotoUri updated: $localPath")
            }
            fieldKey == "customBackgroundUri" -> {
                updated = current.copy(customBackgroundUri = localPath)
                Log.d(TAG, "✅ customBackgroundUri updated: $localPath")
            }
            fieldKey == "prayerCardPhotoUri" -> {
                updated = current.copy(prayerCardPhotoUri = localPath)
                Log.d(TAG, "✅ prayerCardPhotoUri updated: $localPath")
            }
            fieldKey == "videoUri" -> {
                updated = current.copy(videoUri = localPath)
                Log.d(TAG, "✅ videoUri updated: $localPath")
            }
            fieldKey.startsWith("photoSlideshowUris[") -> {
                val indexStr = fieldKey.substringAfter("[").substringBefore("]")
                val index = indexStr.toIntOrNull()
                if (index != null && index >= 0) {
                    val list = current.photoSlideshowUris.toMutableList()
                    while (list.size <= index) list.add("")
                    list[index] = localPath
                    updated = current.copy(photoSlideshowUris = list)
                    Log.d(TAG, "✅ photoSlideshowUris[$index] updated: $localPath")
                }
            }
            else -> {
                Log.w(TAG, "⚠️ fieldKey tidak dikenal: $fieldKey")
                return
            }
        }

        settingsRepository.updateSettings(updated)
        Log.d(TAG, "🎉 Settings TV berhasil diupdate dengan path lokal")
    } catch (e: Exception) {
        Log.e(TAG, "applyMediaPathToSettings error: ${e.message}")
    }
}

// ============================================================
// JSON STATUS
// ============================================================
private fun getStatusJson(): String {
    val s = settingsRepository.settingsFlow.value
    return JSONObject().apply {
        put("app", "MASJID.IO")
        put("mosque", s.mosqueName)
        put("city", s.city)
        put("kioskMode", s.kioskModeEnabled)
        put("waReport", s.whatsappReportEnabled)
        put("port", actualPort)
        put("running", isRunning)
        put("lastError", lastError ?: "")
        put("activeMediaTransfers", mediaTransfers.size)
    }.toString()
}

private fun getFullSettingsJson(): String {
    val s = settingsRepository.settingsFlow.value
    return JSONObject().apply {
        put("mosqueName", s.mosqueName)
        put("mosqueAddress", s.mosqueAddress)
        put("mosqueTakmir", s.mosqueTakmir)
        put("runningText", s.runningText)
        put("runningTextSpeed", s.runningTextSpeed)
        put("runningTextFontSize", s.runningTextFontSize)
        put("animationsEnabled", s.animationsEnabled)
        put("showBirdsAnimation", s.showBirdsAnimation)
        put("keepScreenOn", s.keepScreenOn)
        put("tvAutoScaleEnabled", s.tvAutoScaleEnabled)
        put("tvSafeAreaPercent", s.tvSafeAreaPercent)
        put("tvLayoutPreset", s.tvLayoutPreset)
        put("backgroundMode", s.backgroundMode.name)
        put("audioMode", s.audioMode.name)
        put("beepVolume", s.beepVolume)
        put("beepCount", s.beepCount)
        put("beepDurationMs", s.beepDurationMs)
        put("beepIntervalMs", s.beepIntervalMs)
        put("adzanFile", s.adzanFile)
        put("adzanVolume", s.adzanVolume)
        put("photoSlideshowEnabled", s.photoSlideshowEnabled)
        put("photoSlideshowIntervalSeconds", s.photoSlideshowIntervalSeconds)
        put("imamSubuh", s.officers.imamSubuh)
        put("muadzinSubuh", s.officers.muadzinSubuh)
        put("imamDzuhur", s.officers.imamDzuhur)
        put("muadzinDzuhur", s.officers.muadzinDzuhur)
        put("imamAshar", s.officers.imamAshar)
        put("muadzinAshar", s.officers.muadzinAshar)
        put("imamMaghrib", s.officers.imamMaghrib)
        put("muadzinMaghrib", s.officers.muadzinMaghrib)
        put("imamIsya", s.officers.imamIsya)
        put("muadzinIsya", s.officers.muadzinIsya)
        put("khatibJumat", s.officers.khatibJumat)
        put("temaJumat", s.officers.temaJumat)
        put("ustadzKajian", s.officers.ustadzKajian)
        put("jadwalKajian", s.officers.jadwalKajian)
        put("temaKajian", s.officers.temaKajian)
        put("iqamahWaitMinutes", s.iqamahWaitMinutes)
        put("qobliyahWaitMinutes", s.qobliyahWaitMinutes)
        put("adzanWaitMinutes", s.adzanWaitMinutes)
        put("prayerFocusDurationMinutes", s.prayerFocusDurationMinutes)
        put("focusModeDurationMinutes", s.focusModeDurationMinutes)
        put("bankName", s.bankName)
        put("bankAccountNumber", s.bankAccountNumber)
        put("bankAccountHolder", s.bankAccountHolder)
        put("qrisIntervalMinutes", s.qrisIntervalMinutes)
        put("qrisDisplayDurationSeconds", s.qrisDisplayDurationSeconds)
        put("qrisPhotoUri", s.qrisPhotoUri ?: "")
        put("slideEnabled", s.slideEnabled)
        put("slideIntervalSeconds", s.slideIntervalSeconds)
        put("qrisSlideEnabled", s.qrisSlideEnabled)
        put("laporanSlideEnabled", s.laporanSlideEnabled)
        put("kajianSlideEnabled", s.kajianSlideEnabled)
        put("contentRotationEnabled", s.contentRotationEnabled)
        put("contentRotationShowAyat", s.contentRotationShowAyat)
        put("contentRotationShowHadits", s.contentRotationShowHadits)
        put("contentRotationShowAsmaulHusna", s.contentRotationShowAsmaulHusna)
        put("contentRotationIntervalSeconds", s.contentRotationIntervalSeconds)
        put("cctvEnabled", s.cctvEnabled)
        put("cctvUrl", s.cctvUrl)
        put("cctvSizePercent", s.cctvSizePercent)
        put("cctvPosition", s.cctvPosition.name)
        put("ramadhanModeEnabled", s.ramadhanModeEnabled)
        put("showImsakIftarCountdown", s.showImsakIftarCountdown)
        put("showTarawihSchedule", s.showTarawihSchedule)
        put("showKultumSchedule", s.showKultumSchedule)
        put("ramadhanImsakOffsetMinutes", s.ramadhanImsakOffsetMinutes)
        put("tarawihTime", s.tarawihTime)
        put("tarawihImam", s.tarawihImam)
        put("kultumTitle", s.kultumTitle)
        put("kultumUstadz", s.kultumUstadz)
        put("kultumTime", s.kultumTime)
        put("menuSahurText", s.menuSahurText)
        put("menuIftarText", s.menuIftarText)
        put("laporanSaldoSebelumnya", s.laporanKeuangan.saldoSebelumnya)
        put("laporanPemasukanJumat", s.laporanKeuangan.pemasukanJumat)
        put("laporanPemasukanUmum", s.laporanKeuangan.pemasukanUmum)
        put("laporanPengeluaranDakwah", s.laporanKeuangan.pengeluaranDakwah)
        put("laporanPengeluaranSosial", s.laporanKeuangan.pengeluaranSosial)
        put("laporanPengeluaranOperasional", s.laporanKeuangan.pengeluaranOperasional)
        put("laporanPeriodeMulai", s.laporanKeuangan.periodeMulai)
        put("laporanPeriodeSelesai", s.laporanKeuangan.periodeSelesai)
        put("kioskModeEnabled", s.kioskModeEnabled)
        put("autoStartOnBoot", s.autoStartOnBoot)
        put("whatsappReportEnabled", s.whatsappReportEnabled)
        put("fonnteToken", s.fonnteToken)
        put("fonnteGroupId", s.fonnteGroupId)
        val arr = JSONArray()
        s.weeklyOfficers.forEach { o ->
            arr.put(JSONObject().apply {
                put("dayName", o.dayName)
                put("imamSubuh", o.imamSubuh); put("muadzinSubuh", o.muadzinSubuh)
                put("imamDzuhur", o.imamDzuhur); put("muadzinDzuhur", o.muadzinDzuhur)
                put("imamAshar", o.imamAshar); put("muadzinAshar", o.muadzinAshar)
                put("imamMaghrib", o.imamMaghrib); put("muadzinMaghrib", o.muadzinMaghrib)
                put("imamIsya", o.imamIsya); put("muadzinIsya", o.muadzinIsya)
                put("khatibJumat", o.khatibJumat); put("temaJumat", o.temaJumat)
                put("ustadzKajian", o.ustadzKajian); put("temaKajian", o.temaKajian)
            })
        }
        put("weeklyOfficers", arr)
    }.toString()
}

    // ============================================================
    // LOGIN HTML
    // ============================================================
    private fun getLoginHtml(): String = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Login — MASJID.IO</title>
<style>
* { box-sizing: border-box; }
body { font-family: Arial, sans-serif; background: #0A1929; color: #fff;
margin: 0; min-height: 100vh; display: flex; align-items: center;
justify-content: center; padding: 20px; }
.card { background: #132F4C; padding: 32px; border-radius: 16px;
width: 100%; max-width: 400px; border: 2px solid #FFD700; text-align: center; }
h1 { color: #FFD700; margin: 0 0 8px; }
p { color: #90A4AE; margin: 0 0 24px; font-size: 14px; }
input { width: 100%; padding: 14px; border: 2px solid #2196F3;
border-radius: 10px; background: #0F2636; color: #fff;
font-size: 18px; text-align: center; letter-spacing: 8px; }
input:focus { outline: none; border-color: #FFD700; }
button { width: 100%; margin-top: 16px; padding: 14px;
background: #FFD700; color: #09141D; border: none;
border-radius: 10px; font-weight: bold; font-size: 16px; cursor: pointer; }
button:hover { background: #FFC107; }
.err { color: #FF5252; font-size: 13px; margin-top: 12px; min-height: 20px; }
</style>
</head>
<body>
<div class="card">
<h1>🕌 MASJID.IO</h1>
<p>Masukkan PIN Pengaturan</p>
<input id="pin" type="password" inputmode="numeric" maxlength="8" placeholder="••••" autofocus>
<button id="btn" onclick="doLogin()">MASUK</button>
<div class="err" id="err"></div>
</div>
<script>
async function doLogin() {
const btn = document.getElementById('btn');
const err = document.getElementById('err');
const pin = document.getElementById('pin').value;
if (!pin) { err.textContent = 'PIN tidak boleh kosong'; return; }
btn.disabled = true; btn.textContent = 'MEMPROSES...'; err.textContent = '';
try {
const res = await fetch('/api/login', {
method: 'POST',
headers: {'Content-Type': 'application/x-www-form-urlencoded'},
body: 'password=' + encodeURIComponent(pin),
credentials: 'same-origin'
});
const data = await res.json();
if (data.success) {
window.location.href = '/';
} else {
err.textContent = data.error || 'PIN salah';
btn.disabled = false;
btn.textContent = 'MASUK';
document.getElementById('pin').value = '';
document.getElementById('pin').focus();
}
} catch (e) {
err.textContent = 'Error: ' + e.message;
btn.disabled = false;
btn.textContent = 'MASUK';
}
}
document.getElementById('pin').addEventListener('keypress', e => {
if (e.key === 'Enter') doLogin();
});
</script>
</body>
</html>
    """.trimIndent()
    
    // ============================================================
    // DASHBOARD HTML
    // ============================================================
    private fun getDashboardHtml(): String = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Dashboard — MASJID.IO</title>
<style>
* { box-sizing: border-box; }
body { font-family: Arial, sans-serif; background: #0A1929; color: #fff; margin: 0; padding: 0; }
.header { background: #071A2E; padding: 16px 20px; display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; z-index: 100; border-bottom: 2px solid #FFD700; }
.header h1 { color: #FFD700; margin: 0; font-size: 20px; }
.tabs { display: flex; gap: 4px; padding: 12px; overflow-x: auto; background: #0F2636; position: sticky; top: 60px; z-index: 99; }
.tab { padding: 10px 16px; background: #132F4C; color: #90A4AE; border: none; border-radius: 8px; cursor: pointer; white-space: nowrap; font-weight: bold; font-size: 13px; }
.tab.active { background: #FFD700; color: #09141D; }
.content { padding: 16px; max-width: 700px; margin: 0 auto; }
.panel { display: none; }
.panel.active { display: block; }
.card { background: #132F4C; padding: 16px; border-radius: 12px; margin-bottom: 16px; border: 1px solid #1E3A5F; }
.card h3 { color: #64B5F6; margin: 0 0 12px; font-size: 15px; }
label { display: block; color: #90A4AE; font-size: 12px; margin-top: 10px; }
input[type="text"], input[type="number"], input[type="password"], textarea, select { width: 100%; padding: 10px; background: #0F2636; color: #fff; border: 1px solid #2196F3; border-radius: 6px; font-size: 14px; margin-top: 4px; }
textarea { min-height: 80px; font-family: monospace; }
input[type="range"] { width: 100%; margin-top: 8px; }
.row { display: flex; gap: 8px; align-items: center; }
.row input[type="range"] { flex: 1; }
.row .val { min-width: 40px; text-align: right; color: #FFD700; font-weight: bold; font-size: 14px; }
.toggle-wrap { display: flex; align-items: center; justify-content: space-between; padding: 8px 0; }
.toggle-wrap span { font-size: 14px; }
.toggle { width: 52px; height: 28px; border-radius: 14px; background: #37474F; position: relative; cursor: pointer; transition: 0.2s; }
.toggle.on { background: #4CAF50; }
.toggle::after { content: ''; position: absolute; width: 22px; height: 22px; background: #fff; border-radius: 50%; top: 3px; left: 3px; transition: 0.2s; }
.toggle.on::after { left: 27px; }
button.save { padding: 12px; background: #FFD700; color: #09141D; border: none; border-radius: 10px; font-weight: bold; width: 100%; margin-top: 12px; cursor: pointer; font-size: 14px; }
button.save:hover { background: #FFC107; }
button.danger { background: #FF5252; color: #fff; }
.toast { position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background: #4CAF50; color: #fff; padding: 12px 24px; border-radius: 8px; font-size: 14px; opacity: 0; transition: 0.3s; pointer-events: none; z-index: 999; }
.toast.show { opacity: 1; }
.toast.error { background: #FF5252; }
.divider { border-top: 1px solid #1E3A5F; margin: 12px 0; }
</style>
</head>
<body>
<div class="header">
<h1>🕌 MASJID.IO</h1>
<button class="tab" onclick="doLogout()">Logout</button>
</div>
<div class="tabs">
<button class="tab active" onclick="showTab(event,'masjid')">🏛️ Masjid</button>
<button class="tab" onclick="showTab(event,'running')">📝 Running</button>
<button class="tab" onclick="showTab(event,'audio')">🔊 Audio</button>
<button class="tab" onclick="showTab(event,'tampilan')">🎨 Tampilan</button>
<button class="tab" onclick="showTab(event,'petugas')">👤 Petugas</button>
<button class="tab" onclick="showTab(event,'waktu')">⏱️ Waktu</button>
<button class="tab" onclick="showTab(event,'qris')">💳 QRIS</button>
<button class="tab" onclick="showTab(event,'fitur')">⚙️ Fitur</button>
<button class="tab" onclick="showTab(event,'media')">📷 Media</button>
<button class="tab" onclick="showTab(event,'sistem')">🔧 Sistem</button>
</div>
<div class="content">

<div class="panel active" id="panel-masjid">
<div class="card">
<h3>🏛️ Identitas Masjid</h3>
<label>Nama Masjid</label><input type="text" id="mosqueName">
<label>Alamat</label><input type="text" id="mosqueAddress">
<label>Takmir</label><input type="text" id="mosqueTakmir">
<button class="save" onclick="saveFields(['mosqueName','mosqueAddress','mosqueTakmir'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-running">
<div class="card">
<h3>📝 Running Text</h3>
<label>Teks Running</label><textarea id="runningText" rows="4"></textarea>
<label>Kecepatan Scroll (1-10)</label>
<div class="row"><input type="range" id="runningTextSpeed" min="1" max="10" oninput="document.getElementById('spd').textContent=this.value"><div class="val" id="spd">5</div></div>
<label>Ukuran Font (14-32)</label>
<div class="row"><input type="range" id="runningTextFontSize" min="14" max="32" oninput="document.getElementById('fsz').textContent=this.value+' sp'"><div class="val" id="fsz">18 sp</div></div>
<button class="save" onclick="saveFields(['runningText','runningTextSpeed','runningTextFontSize'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-audio">
<div class="card">
<h3>🔊 Audio & Adzan</h3>
<label>Mode Audio</label>
<select id="audioMode">
<option value="BEEP_ONLY">BEEP_ONLY (Bip saja)</option>
<option value="FULL_ADZAN">FULL_ADZAN (Adzan penuh)</option>
<option value="SILENT">SILENT (Tanpa suara)</option>
</select>
<label>Volume Beep (0-100)</label>
<div class="row"><input type="range" id="beepVolume" min="0" max="100" oninput="document.getElementById('bv').textContent=this.value+'%'"><div class="val" id="bv">100%</div></div>
<label>Jumlah Beep (1-20)</label><input type="number" id="beepCount" min="1" max="20">
<label>Durasi Beep (ms)</label><input type="number" id="beepDurationMs" min="100" max="5000">
<label>Jeda Beep (ms)</label><input type="number" id="beepIntervalMs" min="100" max="10000">
<label>File Adzan</label><input type="text" id="adzanFile">
<label>Volume Adzan (0-100)</label>
<div class="row"><input type="range" id="adzanVolume" min="0" max="100" oninput="document.getElementById('av').textContent=this.value+'%'"><div class="val" id="av">85%</div></div>
<button class="save" onclick="saveFields(['audioMode','beepVolume','beepCount','beepDurationMs','beepIntervalMs','adzanFile','adzanVolume'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-tampilan">
<div class="card">
<h3>🎨 Tampilan</h3>
<label>Background Mode</label>
<select id="backgroundMode">
<option value="MAKKAH_DYNAMIC">MAKKAH_DYNAMIC (default)</option>
<option value="NATURE">NATURE</option>
<option value="DEFAULT_NATURE">DEFAULT_NATURE</option>
<option value="KABAH">KABAH</option>
<option value="EMERALD_GEOMETRIC">EMERALD_GEOMETRIC</option>
<option value="CUSTOM">CUSTOM</option>
<option value="CUSTOM_GALLERY">CUSTOM_GALLERY</option>
</select>
<div class="divider"></div>
<div class="toggle-wrap"><span>Animasi</span><div class="toggle" id="animationsEnabled" onclick="this.classList.toggle('on')"></div></div>
<div class="toggle-wrap"><span>Burung Terbang</span><div class="toggle" id="showBirdsAnimation" onclick="this.classList.toggle('on')"></div></div>
<div class="toggle-wrap"><span>Keep Screen On</span><div class="toggle" id="keepScreenOn" onclick="this.classList.toggle('on')"></div></div>
<div class="toggle-wrap"><span>Auto-Scale TV</span><div class="toggle" id="tvAutoScaleEnabled" onclick="this.classList.toggle('on')"></div></div>
<label>Safe Area (%)</label><input type="number" id="tvSafeAreaPercent" min="0" max="10" step="0.5">
<label>Layout Preset</label>
<select id="tvLayoutPreset">
<option value="AUTO">AUTO</option>
<option value="STANDAR">STANDAR</option>
<option value="ULTRAWIDE">ULTRAWIDE</option>
<option value="4:3">4:3</option>
</select>
<button class="save" onclick="saveFields(['backgroundMode','animationsEnabled','showBirdsAnimation','keepScreenOn','tvAutoScaleEnabled','tvSafeAreaPercent','tvLayoutPreset'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-petugas">
<div class="card">
<h3>👤 Jadwal Petugas</h3>
<label>Imam Subuh</label><input type="text" id="imamSubuh">
<label>Muadzin Subuh</label><input type="text" id="muadzinSubuh">
<label>Imam Dzuhur</label><input type="text" id="imamDzuhur">
<label>Muadzin Dzuhur</label><input type="text" id="muadzinDzuhur">
<label>Imam Ashar</label><input type="text" id="imamAshar">
<label>Muadzin Ashar</label><input type="text" id="muadzinAshar">
<label>Imam Maghrib</label><input type="text" id="imamMaghrib">
<label>Muadzin Maghrib</label><input type="text" id="muadzinMaghrib">
<label>Imam Isya</label><input type="text" id="imamIsya">
<label>Muadzin Isya</label><input type="text" id="muadzinIsya">
<label>Khatib Jumat</label><input type="text" id="khatibJumat">
<label>Tema Jumat</label><input type="text" id="temaJumat">
<label>Ustadz Kajian</label><input type="text" id="ustadzKajian">
<label>Tema Kajian</label><input type="text" id="temaKajian">
<button class="save" onclick="saveFields(['imamSubuh','muadzinSubuh','imamDzuhur','muadzinDzuhur','imamAshar','muadzinAshar','imamMaghrib','muadzinMaghrib','imamIsya','muadzinIsya','khatibJumat','temaJumat','ustadzKajian','temaKajian'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-waktu">
<div class="card">
<h3>⏱️ Waktu Sholat</h3>
<label>Jeda Iqamah (menit)</label><input type="number" id="iqamahWaitMinutes" min="0" max="60">
<label>Countdown Qobliyah (menit)</label><input type="number" id="qobliyahWaitMinutes" min="0" max="30">
<label>Adzan Wait (menit)</label><input type="number" id="adzanWaitMinutes" min="0" max="30">
<label>Durasi Mode Fokus (menit)</label><input type="number" id="focusModeDurationMinutes" min="5" max="120">
<button class="save" onclick="saveFields(['iqamahWaitMinutes','qobliyahWaitMinutes','adzanWaitMinutes','focusModeDurationMinutes'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-qris">
<div class="card">
<h3>💳 QRIS & Rekening</h3>
<label>Nama Bank</label><input type="text" id="bankName">
<label>No Rekening</label><input type="text" id="bankAccountNumber">
<label>Atas Nama</label><input type="text" id="bankAccountHolder">
<label>Interval QRIS (menit)</label><input type="number" id="qrisIntervalMinutes" min="1" max="120">
<label>Durasi Tampil (detik)</label><input type="number" id="qrisDisplayDurationSeconds" min="5" max="120">
<button class="save" onclick="saveFields(['bankName','bankAccountNumber','bankAccountHolder','qrisIntervalMinutes','qrisDisplayDurationSeconds'])">SIMPAN</button>
</div>
<div class="card">
<h3>💰 Laporan Keuangan</h3>
<label>Saldo Sebelumnya</label><input type="number" id="laporanSaldoSebelumnya">
<label>Pemasukan Jumat</label><input type="number" id="laporanPemasukanJumat">
<label>Pemasukan Umum</label><input type="number" id="laporanPemasukanUmum">
<label>Pengeluaran Dakwah</label><input type="number" id="laporanPengeluaranDakwah">
<label>Pengeluaran Sosial</label><input type="number" id="laporanPengeluaranSosial">
<label>Pengeluaran Operasional</label><input type="number" id="laporanPengeluaranOperasional">
<button class="save" onclick="saveFields(['laporanSaldoSebelumnya','laporanPemasukanJumat','laporanPemasukanUmum','laporanPengeluaranDakwah','laporanPengeluaranSosial','laporanPengeluaranOperasional'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-fitur">
<div class="card">
<h3>⚙️ Fitur Tambahan</h3>
<div class="toggle-wrap"><span>Slide Fullscreen</span><div class="toggle" id="slideEnabled" onclick="this.classList.toggle('on')"></div></div>
<label>Interval Slide (detik)</label><input type="number" id="slideIntervalSeconds" min="5" max="120">
<div class="divider"></div>
<div class="toggle-wrap"><span>Konten Rotasi</span><div class="toggle" id="contentRotationEnabled" onclick="this.classList.toggle('on')"></div></div>
<label>Interval Rotasi (detik)</label><input type="number" id="contentRotationIntervalSeconds" min="5" max="120">
<div class="divider"></div>
<div class="toggle-wrap"><span>CCTV</span><div class="toggle" id="cctvEnabled" onclick="this.classList.toggle('on')"></div></div>
<label>URL CCTV</label><input type="text" id="cctvUrl" placeholder="rtsp://...">
<label>Ukuran CCTV (%)</label><input type="number" id="cctvSizePercent" min="10" max="50">
<div class="divider"></div>
<div class="toggle-wrap"><span>Mode Ramadhan</span><div class="toggle" id="ramadhanModeEnabled" onclick="this.classList.toggle('on')"></div></div>
<label>Jadwal Tarawih</label><input type="text" id="tarawihTime" placeholder="19:30">
<label>Imam Tarawih</label><input type="text" id="tarawihImam">
<label>Kultum</label><input type="text" id="kultumTitle">
<label>Ustadz Kultum</label><input type="text" id="kultumUstadz">
<button class="save" onclick="saveFields(['slideEnabled','slideIntervalSeconds','contentRotationEnabled','contentRotationIntervalSeconds','cctvEnabled','cctvUrl','cctvSizePercent','ramadhanModeEnabled','tarawihTime','tarawihImam','kultumTitle','kultumUstadz'])">SIMPAN</button>
</div>
</div>

<div class="panel" id="panel-media">
<div class="card">
<h3>📷 Upload Media</h3>
<label>Logo Masjid</label><input type="file" accept="image/*" onchange="uploadFile(this,'logo')">
<label>Foto QRIS</label><input type="file" accept="image/*" onchange="uploadFile(this,'qris')">
<label>Foto Petugas</label><input type="file" accept="image/*" onchange="uploadFile(this,'officer')">
<label>Background Custom</label><input type="file" accept="image/*" onchange="uploadFile(this,'background')">
</div>
</div>

<div class="panel" id="panel-sistem">
<div class="card">
<h3>🔧 Sistem</h3>
<div class="toggle-wrap"><span>Kiosk Mode</span><div class="toggle" id="kioskModeEnabled" onclick="this.classList.toggle('on')"></div></div>
<div class="toggle-wrap"><span>Auto Start on Boot</span><div class="toggle" id="autoStartOnBoot" onclick="this.classList.toggle('on')"></div></div>
<div class="toggle-wrap"><span>WhatsApp Report</span><div class="toggle" id="whatsappReportEnabled" onclick="this.classList.toggle('on')"></div></div>
<label>Fonnte Token</label><input type="text" id="fonnteToken">
<label>Fonnte Group ID</label><input type="text" id="fonnteGroupId">
<button class="save" onclick="saveFields(['kioskModeEnabled','autoStartOnBoot','whatsappReportEnabled','fonnteToken','fonnteGroupId'])">SIMPAN</button>
</div>
<div class="card">
<h3>🔐 Keamanan</h3>
<label>PIN Pengaturan (4 digit)</label>
<input type="password" id="newPin" maxlength="4" placeholder="••••">
<button class="save" onclick="changePin()">GANTI PIN</button>
</div>
<div class="card">
<h3>🔄 Restart</h3>
<p style="color: #90A4AE; font-size: 13px;">Restart aplikasi MASJID.IO.</p>
<button class="save danger" onclick="restartApp()">RESTART SEKARANG</button>
</div>
</div>
</div>
<div class="toast" id="toast"></div>

<script>
let currentSettings = {};

function showTab(e, name) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    e.target.classList.add('active');
    document.getElementById('panel-' + name).classList.add('active');
}

function showToast(msg, isError) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.className = 'toast show' + (isError ? ' error' : '');
    setTimeout(() => t.className = 'toast', 2000);
}

function getVal(id) {
    const el = document.getElementById(id);
    if (!el) return null;
    if (el.classList && el.classList.contains('toggle')) return el.classList.contains('on');
    if (el.type === 'number') {
        const v = parseFloat(el.value);
        return isNaN(v) ? null : v;
    }
    return el.value;
}

async function saveFields(keys) {
    const payload = {};
    keys.forEach(k => {
        const v = getVal(k);
        if (v !== null) payload[k] = v;
    });
    try {
        const res = await fetch('/api/settings', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload),
            credentials: 'same-origin'
        });
        const data = await res.json();
        if (data.success) showToast('✅ Tersimpan');
        else showToast('❌ ' + (data.error || 'Gagal'), true);
    } catch (e) {
        showToast('❌ ' + e.message, true);
    }
}

async function uploadFile(input, type) {
    const file = input.files[0];
    if (!file) return;
    if (file.size > 5 * 1024 * 1024) {
        showToast('File terlalu besar (max 5MB)', true);
        return;
    }
    showToast('Mengunggah...');
    const reader = new FileReader();
    reader.onload = async () => {
        const base64 = reader.result.split(',')[1];
        try {
            const res = await fetch('/api/upload?type=' + type, {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: 'data=' + encodeURIComponent(base64) + '&name=' + encodeURIComponent(file.name),
                credentials: 'same-origin'
            });
            const data = await res.json();
            if (data.success) showToast('✅ Berhasil upload');
            else showToast('❌ ' + (data.error || 'Gagal'), true);
        } catch (e) {
            showToast('❌ ' + e.message, true);
        }
    };
    reader.readAsDataURL(file);
}

async function changePin() {
    const pin = document.getElementById('newPin').value;
    if (pin.length !== 4 || !/^\d{4}$/.test(pin)) {
        showToast('PIN harus 4 digit angka', true);
        return;
    }
    try {
        const res = await fetch('/api/pin', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'pin=' + encodeURIComponent(pin),
            credentials: 'same-origin'
        });
        const data = await res.json();
        if (data.success) showToast('✅ PIN diganti');
        else showToast('❌ ' + (data.error || 'Gagal'), true);
    } catch (e) {
        showToast('❌ ' + e.message, true);
    }
}

async function restartApp() {
    if (!confirm('Yakin restart aplikasi?')) return;
    await fetch('/api/restart', {method: 'POST', credentials: 'same-origin'});
    showToast('Restart dijadwalkan...');
}

async function doLogout() {
    await fetch('/api/logout', {method: 'POST', credentials: 'same-origin'});
    window.location.href = '/login';
}

async function loadSettings() {
    try {
        const res = await fetch('/api/settings', {credentials: 'same-origin'});
        if (res.status === 401) {
            window.location.href = '/login';
            return;
        }
        currentSettings = await res.json();
        Object.keys(currentSettings).forEach(k => {
            const el = document.getElementById(k);
            if (!el) return;
            if (el.classList && el.classList.contains('toggle')) {
                if (currentSettings[k]) el.classList.add('on');
            } else if (el.type === 'range' || el.type === 'number') {
                el.value = currentSettings[k];
                el.dispatchEvent(new Event('input'));
            } else {
                el.value = currentSettings[k];
            }
        });
    } catch (e) {
        console.error('Load settings error:', e);
    }
}

loadSettings();
</script>
</body>
</html>
    """.trimIndent()
}
