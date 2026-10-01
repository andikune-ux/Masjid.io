package com.example.ui.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket

/**
 * NetworkHelper — Utilitas deteksi IP WiFi & koneksi jaringan.
 *
 * V1.31.0 (iO Control Improvement):
 * - Auto-detect IP WiFi saat ini (bukan hardcoded)
 * - Cek WiFi connected status
 * - Fallback ke hostname jika IP tidak ditemukan
 * - Detail error logging
 */
object NetworkHelper {
    private const val TAG = "NetworkHelper"

    /**
     * Dapatkan IP WiFi device saat ini.
     * Return null jika WiFi tidak connected atau error.
     */
    suspend fun getWiFiIp(context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: run {
                Log.w(TAG, "Tidak ada active network")
                return@withContext null
            }
            val caps = cm.getNetworkCapabilities(network) ?: run {
                Log.w(TAG, "Tidak ada network capabilities")
                return@withContext null
            }

            if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.w(TAG, "Active network bukan WiFi")
                return@withContext null
            }

            // Cari IP di interface yang active
            val linkProps = cm.getLinkProperties(network)
            linkProps?.let {
                it.linkAddresses.forEach { addr ->
                    val ip = addr.address.hostAddress
                    if (ip != null && !ip.contains(":") && ip != "127.0.0.1") {
                        Log.d(TAG, "✅ WiFi IP found: $ip")
                        return@withContext ip
                    }
                }
            }

            // Fallback: scan semua interface
            NetworkInterface.getNetworkInterfaces().toList().forEach { ni ->
                ni.inetAddresses.toList().forEach { addr ->
                    val ip = addr.hostAddress
                    if (ip != null && !ip.contains(":") && ip != "127.0.0.1" && !ip.startsWith("169.")) {
                        Log.d(TAG, "✅ WiFi IP found (fallback): $ip")
                        return@withContext ip
                    }
                }
            }

            Log.w(TAG, "❌ Tidak bisa deteksi WiFi IP")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getWiFiIp: ${e.message}")
            null
        }
    }

    /**
     * Cek apakah device terhubung ke WiFi.
     */
    fun isWiFiConnected(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } catch (e: Exception) {
            Log.w(TAG, "Error isWiFiConnected: ${e.message}")
            false
        }
    }

    /**
     * Test ping ke IP:port tertentu (async).
     */
    suspend fun testConnectivity(ip: String, port: Int, timeoutMs: Int = 3000): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Socket().use { socket ->
                    socket.soTimeout = timeoutMs
                    socket.connect(java.net.InetSocketAddress(ip, port), timeoutMs)
                    true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Connectivity test failed ($ip:$port): ${e.message}")
                false
            }
        }
}
