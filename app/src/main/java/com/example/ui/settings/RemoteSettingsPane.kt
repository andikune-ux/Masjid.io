package dev.andikune.masjidio.ui.settings

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.components.NeonFocusBorder
import dev.andikune.masjidio.ui.components.TvToggle
import dev.andikune.masjidio.ui.remote.WebRemoteQrDialog
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.util.QrCodeGenerator
import kotlinx.coroutines.delay
import java.net.Inet4Address

// Warna iO Control (Biru Teknologi)
private val IoBlue = Color(0xFF2196F3)
private val IoBlueLight = Color(0xFF64B5F6)
private val IoGreen = Color(0xFF4CAF50)

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

    // ============================================================
    // AUTO-REFRESH IP & STATUS JARINGAN SETIAP 5 DETIK
    // ============================================================
    var currentIp by remember { mutableStateOf(getLocalIpAddress(context)) }
    var currentNetworkOn by remember { mutableStateOf(isNetworkConnected(context)) }
    var showQrDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentIp = getLocalIpAddress(context)
            currentNetworkOn = isNetworkConnected(context)
            delay(5000)
        }
    }

    // ============================================================
    // ROOT COLUMN — focusGroup + verticalScroll
    // ============================================================
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .focusGroup()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
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

        // TOMBOL BESAR — BUKA iO CONTROL (FOCUSABLE)
        val ioInteraction = remember { MutableInteractionSource() }
        val ioFocused by ioInteraction.collectIsFocusedAsState()
        val ioPressed by ioInteraction.collectIsPressedAsState()

        NeonFocusBorder(
            focused = ioFocused,
            pressed = ioPressed,
            cornerRadius = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    .focusable(interactionSource = ioInteraction)
                    .clickable(
                        interactionSource = ioInteraction,
                        indication = null
                    ) { onOpenIoControl() }
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
        }

        Spacer(modifier = Modifier.height(8.dp))

        // STATUS SERVER
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
                                if (isServerRunning) IoGreen else Color(0xFFFF5252)
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isServerRunning) "AKTIF" else "NONAKTIF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isServerRunning) IoGreen else Color(0xFFFF5252)
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

        // AKTIFKAN REMOTE SERVER (toggle)
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
                    description = "Aktifkan server HTTP untuk kontrol jarak jauh dari HP",
                    isChecked = localServerEnabled,
                    onToggle = { enabled ->
                        localServerEnabled = enabled
                        onUpdate(settings.copy(remoteControlEnabled = enabled))
                    }
                )
            }
        }
        
        // AKSES DARI HP — SCAN BARCODE QR & TOMBOL SALIN OTOMATIS
        if (isServerRunning) {
            val fullUrl = "http://$currentIp:${settings.remoteServerPort}/?token=${settings.remoteAuthToken}"
            val qrBitmap = remember(fullUrl) {
                if (fullUrl.isNotBlank()) QrCodeGenerator.generateQrImageBitmap(fullUrl, 400) else null
            }

            RemoteSectionCard(title = "AKSES DARI HP — SCAN BARCODE QR") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Scan barcode dengan kamera HP untuk langsung membuka web remote pengaturan TV:",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )

                    // KARTU TAMPILAN BARCODE QR
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0C1F33))
                            .border(1.5.dp, IoBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // KOTAK QR CODE
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(2.dp, IslamicGold, RoundedCornerShape(12.dp))
                                    .clickable { showQrDialog = true }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (qrBitmap != null) {
                                    Image(
                                        bitmap = qrBitmap,
                                        contentDescription = "Barcode QR Web Remote",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    CircularProgressIndicator(color = IoBlue, modifier = Modifier.size(28.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // PENJELASAN & TOMBOL PERBESAR
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = IoBlueLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "KODE QR SIAP SCAN",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicGoldLight
                                    )
                                }

                                Text(
                                    text = "Buka Kamera di HP Anda, arahkan ke kode QR ini. Notifikasi web akan langsung muncul di HP.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )

                                val expandInteraction = remember { MutableInteractionSource() }
                                val expandFocused by expandInteraction.collectIsFocusedAsState()
                                val expandPressed by expandInteraction.collectIsPressedAsState()

                                NeonFocusBorder(
                                    focused = expandFocused,
                                    pressed = expandPressed,
                                    cornerRadius = 10.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF132F4C))
                                            .border(1.dp, IoBlueLight, RoundedCornerShape(10.dp))
                                            .focusable(interactionSource = expandInteraction)
                                            .clickable(
                                                interactionSource = expandInteraction,
                                                indication = null
                                            ) { showQrDialog = true }
                                            .padding(vertical = 10.dp, horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.OpenInFull,
                                                contentDescription = null,
                                                tint = IoBlueLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "PERBESAR BARCODE QR",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = IoBlueLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Atau gunakan alamat URL manual:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )

                    CopyUrlBox(
                        url = fullUrl,
                        ip = currentIp,
                        port = settings.remoteServerPort,
                        token = settings.remoteAuthToken,
                        networkOn = currentNetworkOn
                    )
                }
            }
        }

        // KONEKSI JARINGAN
        RemoteSectionCard(title = "KONEKSI JARINGAN") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = if (currentNetworkOn) IoGreen else Color(0xFFFF5252),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (currentNetworkOn) "JARINGAN AKTIF" else "JARINGAN NONAKTIF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentNetworkOn) IoGreen else Color(0xFFFF5252)
                    )
                    Text(
                        text = "IP: $currentIp",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showQrDialog) {
        val fullUrl = "http://$currentIp:${settings.remoteServerPort}/?token=${settings.remoteAuthToken}"
        WebRemoteQrDialog(
            url = fullUrl,
            ip = currentIp,
            port = settings.remoteServerPort,
            isWifiConnected = currentNetworkOn,
            onDismiss = { showQrDialog = false }
        )
    }
}

// ============================================================
// COPY URL BOX
// ============================================================
@Composable
private fun CopyUrlBox(
    url: String,
    ip: String,
    port: Int,
    token: String,
    networkOn: Boolean
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val copyInteraction = remember { MutableInteractionSource() }
    val copyFocused by copyInteraction.collectIsFocusedAsState()
    val copyPressed by copyInteraction.collectIsPressedAsState()

    LaunchedEffect(copied) {
        if (copied) {
            delay(3000)
            copied = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0F2636))
                .border(
                    1.5.dp,
                    if (networkOn) IoBlue.copy(alpha = 0.5f) else Color(0xFFFF5252).copy(alpha = 0.5f),
                    RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
            Text(
                text = url,
                fontSize = 13.sp,
                color = if (networkOn) IoBlueLight else Color(0xFFFF8A80),
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp
            )
        }

        // Tombol SALIN URL (FOCUSABLE)
        NeonFocusBorder(
            focused = copyFocused,
            pressed = copyPressed,
            cornerRadius = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (copied) IoGreen.copy(alpha = 0.25f) else IoBlue.copy(alpha = 0.2f)
                    )
                    .border(
                        1.5.dp,
                        if (copied) IoGreen else IoBlue,
                        RoundedCornerShape(10.dp)
                    )
                    .focusable(interactionSource = copyInteraction)
                    .clickable(
                        interactionSource = copyInteraction,
                        indication = null
                    ) {
                        clipboard.setText(AnnotatedString(url))
                        copied = true
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = if (copied) IoGreen else IoBlueLight,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (copied) "✓ TERSALIN" else "📋 SALIN URL",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (copied) IoGreen else IoBlueLight,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoChip(label = "IP", value = ip)
            InfoChip(label = "PORT", value = port.toString())
            InfoChip(label = "TOKEN", value = token)
        }

        if (!networkOn) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33FF5252))
                    .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "⚠️ Jaringan tidak aktif. Sambungkan TV ke jaringan " +
                            "supaya IP muncul otomatis.",
                    fontSize = 12.sp,
                    color = Color(0xFFFF8A80),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Text(
            text = "💡 Tap tombol di atas → buka Chrome → tempel (paste). " +
                    "URL sudah lengkap dengan IP, port, dan token.",
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.8f),
            lineHeight = 15.sp
        )
    }
}

// ============================================================
// INFO CHIP
// ============================================================
@Composable
private fun InfoChip(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F2636))
            .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                fontSize = 10.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                fontSize = 10.sp,
                color = IslamicGoldLight,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ============================================================
// SECTION CARD
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

// ============================================================
// HELPERS — PAKAI ConnectivityManager (Android 12+ friendly)
// ============================================================
private fun isNetworkConnected(context: Context): Boolean {
    return try {
        val cm = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false

        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    } catch (e: Exception) {
        false
    }
}

private fun getLocalIpAddress(context: Context): String {
    return try {
        val cm = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return "0.0.0.0"
        val linkProps: LinkProperties = cm.getLinkProperties(network) ?: return "0.0.0.0"

        val ipv4 = linkProps.linkAddresses.firstOrNull {
            it.address is Inet4Address && !it.address.isLoopbackAddress
        }?.address?.hostAddress

        ipv4 ?: "0.0.0.0"
    } catch (e: Exception) {
        "0.0.0.0"
    }
}
