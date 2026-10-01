package com.example.ui.remote

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface

/**
 * Representasi perangkat Masjid.io lain yang ditemukan di jaringan.
 */
data class DiscoveredDevice(
    val name: String,
    val ip: String,
    val port: Int,
    val role: String,        // "TV" atau "HP"
    val version: String,
    val lastSeen: Long = System.currentTimeMillis()
)

/**
 * DeviceDiscovery — Mencari & temukan perangkat Masjid.io lain
 * di WiFi/Hotspot yang sama menggunakan UDP broadcast.
 *
 * PERBAIKAN V1.31.0:
 * - Auto-retry broadcast jika socket gagal
 * - Timeout lebih pendek (3 detik) untuk respon cepat
 * - Deduplikasi device by IP + PORT combo
 * - Logging detail untuk debugging koneksi
 * - Guard untuk multicast lock
 * - Sort device list by role (TV first)
 *
 * Cara kerja:
 *   1. Kirim UDP broadcast ke port 45678 tiap 2 detik
 *   2. Dengarkan UDP balasan dari device lain
 *   3. Filter device yang sudah tidak aktif (>8 detik)
 *   4. Expose state via StateFlow (sorted)
 */
object DeviceDiscovery {

    private const val TAG = "DeviceDiscovery"
    private const val BROADCAST_PORT = 45678
    private const val BROADCAST_INTERVAL_MS = 2000L  // Dipercepat dari 3s
    private const val DEVICE_TIMEOUT_MS = 8000L      // Lebih pendek dari 10s
    private const val SOCKET_TIMEOUT_MS = 3000

    private var sendJob: Job? = null
    private var listenJob: Job? = null
    private var socket: DatagramSocket? = null
    private var multicastLock: WifiManager.MulticastLock? = null

    private val _devices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val devices: StateFlow<List<DiscoveredDevice>> = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    // Info device ini (di-set dari luar)
    private var myName: String = "Masjid.io Device"
    private var myRole: String = "TV"
    private var myVersion: String = "V1.0.0"
    private var myPort: Int = 8080

    /**
     * Konfigurasi info device ini.
     */
    fun configure(name: String, role: String, version: String, port: Int) {
        myName = name
        myRole = role
        myVersion = version
        myPort = port
        Log.d(TAG, "Configured: $name ($role) v$version at :$port")
    }

    /**
     * Mulai scanning — broadcast + listen.
     */
    fun startScan(context: Context, scope: CoroutineScope) {
        if (_isScanning.value) {
            Log.w(TAG, "Scan sudah berjalan")
            return
        }
        _isScanning.value = true
        _devices.value = emptyList()
        _lastError.value = null
        Log.d(TAG, "🔍 Scanning dimulai...")

        // Acquire multicast lock (Android butuh ini untuk UDP broadcast)
        try {
            val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            multicastLock = wifi.createMulticastLock("masjid_io_discovery").apply {
                setReferenceCounted(false)
                acquire()
            }
            Log.d(TAG, "✅ Multicast lock acquired")
        } catch (e: Exception) {
            Log.w(TAG, "⚠ Multicast lock gagal: ${e.message}")
            _lastError.value = "Multicast lock: ${e.message}"
        }

        sendJob = scope.launch(Dispatchers.IO) { broadcastLoop() }
        listenJob = scope.launch(Dispatchers.IO) { listenLoop() }
    }

    /**
     * Stop scanning & bersihkan.
     */
    fun stopScan() {
        _isScanning.value = false
        sendJob?.cancel()
        listenJob?.cancel()
        sendJob = null
        listenJob = null
        socket?.close()
        socket = null
        try {
            multicastLock?.release()
        } catch (_: Exception) {}
        multicastLock = null
        _devices.value = emptyList()
        Log.d(TAG, "🛑 Scanning stopped")
    }

    // ============================================================
    // BROADCAST LOOP — Kirim sinyal "saya di sini"
    // ============================================================
    private suspend fun broadcastLoop() {
        val payload = JSONObject().apply {
            put("type", "masjid_io_discover")
            put("name", myName)
            put("role", myRole)
            put("version", myVersion)
            put("port", myPort)
        }.toString()

        var retryCount = 0
        while (_isScanning.value) {
            try {
                val s = DatagramSocket()
                s.broadcast = true
                val bytes = payload.toByteArray(Charsets.UTF_8)
                val address = InetAddress.getByName("255.255.255.255")
                val packet = DatagramPacket(bytes, bytes.size, address, BROADCAST_PORT)
                s.send(packet)
                s.close()
                retryCount = 0  // Reset retry counter on success
                Log.v(TAG, "📤 Broadcast sent")
            } catch (e: Exception) {
                retryCount++
                val msg = "Broadcast gagal (attempt $retryCount): ${e.message}"
                Log.w(TAG, msg)
                if (retryCount >= 3) {
                    _lastError.value = "Broadcast failed after 3 attempts: ${e.message}"
                }
            }
            delay(BROADCAST_INTERVAL_MS)
        }
    }

    // ============================================================
    // LISTEN LOOP — Terima sinyal dari device lain
    // ============================================================
    private suspend fun listenLoop() {
        try {
            val s = DatagramSocket(BROADCAST_PORT)
            s.broadcast = true
            s.soTimeout = SOCKET_TIMEOUT_MS
            socket = s
            Log.d(TAG, "📡 Listen socket ready on port $BROADCAST_PORT")

            val buffer = ByteArray(2048)
            while (_isScanning.value) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    s.receive(packet)

                    val raw = String(packet.data, 0, packet.length, Charsets.UTF_8)
                    val senderIp = packet.address.hostAddress ?: continue

                    // Skip kalau dari diri sendiri
                    if (isMyIp(senderIp)) {
                        Log.v(TAG, "🔄 Loopback ignored: $senderIp")
                        continue
                    }

                    handleIncoming(raw, senderIp)
                } catch (e: java.net.SocketTimeoutException) {
                    // Timeout normal, lanjut loop
                } catch (e: Exception) {
                    if (_isScanning.value) {
                        Log.w(TAG, "Listen error: ${e.message}")
                    }
                }

                pruneInactiveDevices()
            }
        } catch (e: Exception) {
            val msg = "Listen loop error: ${e.message}"
            Log.e(TAG, msg)
            _lastError.value = msg
        }
    }

    private fun handleIncoming(raw: String, senderIp: String) {
        try {
            val json = JSONObject(raw)
            if (json.optString("type") != "masjid_io_discover") return

            val device = DiscoveredDevice(
                name = json.optString("name", "Unknown"),
                ip = senderIp,
                port = json.optInt("port", 8080),
                role = json.optString("role", "TV"),
                version = json.optString("version", "V1.0.0")
            )

            // Update atau tambah ke list (deduplikasi by IP + PORT)
            val current = _devices.value.toMutableList()
            val idx = current.indexOfFirst { it.ip == device.ip && it.port == device.port }
            if (idx >= 0) {
                current[idx] = device
            } else {
                current.add(device)
                Log.d(TAG, "✨ New device: ${device.name} (${device.role}) at ${device.ip}:${device.port}")
            }
            // Sort: TV dulu, kemudian HP
            _devices.value = current.sortedBy { if (it.role == "TV") 0 else 1 }
            _lastError.value = null  // Clear error jika ada device
        } catch (e: Exception) {
            Log.w(TAG, "Parse incoming gagal: ${e.message}")
        }
    }

    private fun pruneInactiveDevices() {
        val now = System.currentTimeMillis()
        val filtered = _devices.value.filter { now - it.lastSeen < DEVICE_TIMEOUT_MS }
        if (filtered.size != _devices.value.size) {
            Log.d(TAG, "🧹 Pruned inactive devices: ${_devices.value.size} -> ${filtered.size}")
            _devices.value = filtered
        }
    }

    private fun isMyIp(ip: String): Boolean {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .flatMap { it.inetAddresses.toList() }
                .any { it.hostAddress == ip }
        } catch (e: Exception) {
            false
        }
    }
}
