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
 *
 * V2 (FIX): Tambah `deviceId` unik (Android ID) sebagai identitas utama.
 *   → IP boleh berubah, device tetap dikenali
 *   → Dedup by deviceId, bukan IP
 */
data class DiscoveredDevice(
    val deviceId: String,
    val name: String,
    val ip: String,
    val port: Int,
    val role: String,
    val version: String,
    val lastSeen: Long = System.currentTimeMillis()
)

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

    private var myDeviceId: String = ""
    private var myName: String = "Masjid.io Device"
    private var myRole: String = "TV"
    private var myVersion: String = "V1.0.0"
    private var myPort: Int = 14039

    fun configure(
        deviceId: String,
        name: String,
        role: String,
        version: String,
        port: Int
    ) {
        myDeviceId = deviceId
        myName = name
        myRole = role
        myVersion = version
        myPort = port
        Log.d(TAG, "Configured: id=$deviceId, name=$name, role=$role, port=$port")
    }

    fun startScan(context: Context, scope: CoroutineScope) {
        if (_isScanning.value) return
        _isScanning.value = true
        _devices.value = emptyList()

        try {
            val wifi = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
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

    fun stopScan() {
        _isScanning.value = false
        sendJob?.cancel()
        listenJob?.cancel()
        sendJob = null
        listenJob = null
        socket?.close()
        socket = null
        try { multicastLock?.release() } catch (_: Exception) {}
        multicastLock = null
        _devices.value = emptyList()
    }

    fun findById(deviceId: String): DiscoveredDevice? {
        return _devices.value.firstOrNull { it.deviceId == deviceId }
    }

    private suspend fun broadcastLoop() {
        val payload = JSONObject().apply {
            put("type", "masjid_io_discover")
            put("deviceId", myDeviceId)
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
                    if (isMyIp(senderIp)) continue
                    handleIncoming(raw, senderIp)
                } catch (e: java.net.SocketTimeoutException) {
                    // normal
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
                deviceId = json.optString("deviceId", ""),
                name = json.optString("name", "Unknown"),
                ip = senderIp,
                port = json.optInt("port", 14039),
                role = json.optString("role", "TV"),
                version = json.optString("version", "V1.0.0"),
                lastSeen = System.currentTimeMillis()
            )

            val current = _devices.value.toMutableList()
            val idx = if (device.deviceId.isNotBlank()) {
                current.indexOfFirst { it.deviceId == device.deviceId }
            } else {
                current.indexOfFirst { it.ip == device.ip && it.port == device.port }
            }

            if (idx >= 0) {
                current[idx] = device
            } else {
                current.add(device)
            }
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
        } catch (e: Exception) { false }
    }
}
