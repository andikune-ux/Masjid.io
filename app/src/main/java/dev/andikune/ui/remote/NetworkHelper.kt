package dev.andikune.masjidio.ui.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket

object NetworkHelper {
    private const val TAG = "NetworkHelper"

    suspend fun getWiFiIp(context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return@withContext null
            val linkProps = cm.getLinkProperties(network)

            linkProps?.let {
                it.linkAddresses.forEach { addr ->
                    val ip = addr.address.hostAddress
                    if (ip != null && !ip.contains(":") && ip != "127.0.0.1") {
                        return@withContext ip
                    }
                }
            }

            NetworkInterface.getNetworkInterfaces().toList().forEach { ni ->
                ni.inetAddresses.toList().forEach { addr ->
                    val ip = addr.hostAddress
                    if (ip != null && !ip.contains(":") && ip != "127.0.0.1" && !ip.startsWith("169.")) {
                        return@withContext ip
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getWiFiIp: ${e.message}")
            null
        }
    }

    fun isWiFiConnected(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun testConnectivity(ip: String, port: Int, timeoutMs: Int = 3000): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Socket().use { socket ->
                    socket.soTimeout = timeoutMs
                    socket.connect(InetSocketAddress(ip, port), timeoutMs)
                    true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Connectivity test failed ($ip:$port): ${e.message}")
                false
            }
        }
}
