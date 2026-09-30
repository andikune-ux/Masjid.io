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
 * DeviceDiscovery — Mencari & ditemukan perangkat Masjid.io lain
 * di WiFi/Hotspot yang sama menggunakan UDP broadcast.
 *
 * Cara kerja:
 *   1. Kirim UDP broadcast ke port 45678 tiap 3 detik
 *   2. Dengarkan UDP balasan dari device lain
 *   3. Filter device yang sudah tidak aktif (>10 detik)
 *   4. Expose state via StateFlow
 */
object DeviceDiscovery {

    private const val TAG = "DeviceDiscovery"
    private const val BROADCAST_PORT = 45678
    private const val BROADCAST_INTERVAL_MS = 3000L
    private const val DEVICE_TIMEOUT_MS = 10000L

    private var sendJob: Job? = null
    private var listenJob: Job? = null
    private var socket: DatagramSocket? = null
    private var multicastLock: WifiManager.MulticastLock? = null

    private val _devices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val devices: StateFlow<List<DiscoveredDevice>> = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

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
    }

    /**
     * Mulai scanning — broadcast + listen.
     */
    fun startScan(context: Context, scope: CoroutineScope) {
        if (_isScanning.value) return
        _isScanning.value = true
        _devices.value = emptyList()

        // Acquire multicast lock (Android butuh ini untuk UDP broadcast)
        try {
            val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            multicastLock = wifi.createMulticastLock("masjid_io_discovery").apply {
                setReferenceCounted(false)
                acquire()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Multicast lock gagal: ${e.message}")
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

        while (CoroutineScope(Dispatchers.IO).isActive) {
            try {
                val s = DatagramSocket()
                s.broadcast = true
                val bytes = payload.toByteArray(Charsets.UTF_8)
                val address = InetAddress.getByName("255.255.255.255")
                val packet = DatagramPacket(bytes, bytes.size, address, BROADCAST_PORT)
                s.send(packet)
                s.close()
            } catch (e: Exception) {
                Log.w(TAG, "Broadcast gagal: ${e.message}")
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
            s.soTimeout = 5000
            socket = s

            val buffer = ByteArray(2048)
            while (_isScanning.value) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    s.receive(packet)

                    val raw = String(packet.data, 0, packet.length, Charsets.UTF_8)
                    val senderIp = packet.address.hostAddress ?: continue

                    // Skip kalau dari diri sendiri
                    if (isMyIp(senderIp)) continue

                    handleIncoming(raw, senderIp)
                } catch (e: java.net.SocketTimeoutException) {
                    // Timeout normal, lanjut loop
                } catch (e: Exception) {
                    if (_isScanning.value) Log.w(TAG, "Listen error: ${e.message}")
                }

                pruneInactiveDevices()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Listen loop error: ${e.message}")
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

            // Update atau tambah ke list
            val current = _devices.value.toMutableList()
            val idx = current.indexOfFirst { it.ip == device.ip }
            if (idx >= 0) current[idx] = device else current.add(device)
            _devices.value = current
        } catch (e: Exception) {
            Log.w(TAG, "Parse incoming gagal: ${e.message}")
        }
    }

    private fun pruneInactiveDevices() {
        val now = System.currentTimeMillis()
        val filtered = _devices.value.filter { now - it.lastSeen < DEVICE_TIMEOUT_MS }
        if (filtered.size != _devices.value.size) {
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
