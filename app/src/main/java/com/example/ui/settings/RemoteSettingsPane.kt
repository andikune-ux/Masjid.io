package com.example.ui.settings

import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// Warna iO Control (Biru Teknologi)
private val IoBlue = Color(0xFF2196F3)
private val IoBlueLight = Color(0xFF64B5F6)

@Composable
fun RemoteSettingsPane(
    settings: AppSettings,
    isServerRunning: Boolean,
    onUpdate: (AppSettings) -> Unit,
    onOpenIoControl: () -> Unit = {}
) {
    val context = LocalContext.current
    var localServerEnabled by remember(settings.remoteControlEnabled) {
        mutableStateOf(settings.remoteControlEnabled)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ============================================================
        // HEADER
        // ============================================================
        Text(
            text = "iO CONTROL",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Hubungkan TV dengan HP via WiFi/Hotspot yang sama. " +
                    "Transfer semua pengaturan antar perangkat Masjid.io.",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ============================================================
        // TOMBOL BESAR — BUKA iO CONTROL
        // ============================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(IoBlue.copy(alpha = 0.25f), IoBlueLight.copy(alpha = 0.15f))
                    )
                )
                .border(2.dp, IoBlue, RoundedCornerShape(16.dp))
                .clickable { onOpenIoControl() }
                .padding(vertical = 28.dp, horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = IoBlueLight,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "BUKA iO CONTROL",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IoBlueLight
                    )
                    Text(
                        text = "Cari perangkat & transfer pengaturan",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ============================================================
        // DIVIDER
        // ============================================================
        Text(
            text = "— atau kelola manual via browser —",
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ============================================================
        // STATUS SERVER (Fitur lama)
        // ============================================================
        RemoteSectionCard(title = "STATUS SERVER") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isServerRunning) Color(0xFF4CAF50)
                                else Color(0xFFFF5252)
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isServerRunning) "AKTIF" else "NONAKTIF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isServerRunning) Color(0xFF4CAF50) else Color(0xFFFF5252)
                    )
                }
                Text(
                    text = "Port: ${settings.remoteServerPort}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // ============================================================
        // AKTIFKAN REMOTE SERVER (toggle)
        // ============================================================
        RemoteSectionCard(title = "AKTIFKAN REMOTE SERVER") {
            Column {
                Text(
                    text = "Server HTTP mini untuk kontrol via browser HP. " +
                            "Khusus untuk mengubah running text, PIN, dan restart.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                TvToggle(
                    label = if (localServerEnabled) "Remote Server: ON" else "Remote Server: OFF",
                    checked = localServerEnabled,
                    onCheckedChange = { enabled ->
                        localServerEnabled = enabled
                        onUpdate(settings.copy(remoteControlEnabled = enabled))
                    }
                )
            }
        }

        // ============================================================
        // INFO AKSES (Fitur lama)
        // ============================================================
        if (isServerRunning) {
            RemoteSectionCard(title = "AKSES DARI HP") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val ip = getLocalIpAddress(context)
                    val port = settings.remoteServerPort
                    val token = settings.remoteAuthToken
                    val url = "http://$ip:$port/?token=$token"

                    RemoteStep(number = "1", title = "Sambungkan HP ke WiFi yang sama")
                    RemoteStep(number = "2", title = "Buka browser di HP")
                    RemoteStep(number = "3", title = "Masukkan URL berikut:")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F2636))
                            .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = url,
                            fontSize = 13.sp,
                            color = IslamicGoldLight,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Token: $token",
                        fontSize = 11.sp,
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // ============================================================
        // WIFI STATUS
        // ============================================================
        RemoteSectionCard(title = "KONEKSI WiFi") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = if (isWifiOn(context)) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isWifiOn(context)) "WiFi AKTIF" else "WiFi NONAKTIF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWifiOn(context)) Color(0xFF4CAF50) else Color(0xFFFF5252)
                    )
                    Text(
                        text = "IP: ${getLocalIpAddress(context)}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================
@Composable
private fun RemoteSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF132F4C).copy(alpha = 0.7f))
            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun RemoteStep(number: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(IslamicGold.copy(alpha = 0.2f))
                .border(1.dp, IslamicGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            color = TextPrimary
        )
    }
}

// ============================================================
// HELPERS
// ============================================================
private fun getLocalIpAddress(context: Context): String {
    return try {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        val ipInt = wifiManager.connectionInfo.ipAddress
        if (ipInt == 0) "0.0.0.0"
        else {
            "${ipInt and 0xff}.${(ipInt shr 8) and 0xff}.${(ipInt shr 16) and 0xff}.${(ipInt shr 24) and 0xff}"
        }
    } catch (e: Exception) {
        "0.0.0.0"
    }
}

private fun isWifiOn(context: Context): Boolean {
    return try {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        wifiManager.isWifiEnabled && wifiManager.connectionInfo.networkId != -1
    } catch (e: Exception) {
        false
    }
}
