package com.example.ui.remote

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SettingsRepository
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.MosqueDeepBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.RemoteControlClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================================================
// WARNA TEMA iO CONTROL — BIRU TEKNOLOGI
// ============================================================
private val IoBlue = Color(0xFF2196F3)
private val IoBlueLight = Color(0xFF64B5F6)
private val IoBlueDark = Color(0xFF0D47A1)
private val IoGreen = Color(0xFF4CAF50)
private val IoGreenLight = Color(0xFF81C784)
private val IoRed = Color(0xFFFF5252)
private val IoBg = Color(0xFF0A1929)
private val IoCard = Color(0xFF132F4C)

// ============================================================
// STATE MACHINE
// ============================================================
enum class IoPhase {
    SCANNING,
    CONNECTING,
    CONNECTED,
    SENDING,
    RECEIVING,
    DONE,
    ERROR
}

@Composable
fun IoControlScreen(
    settingsRepository: SettingsRepository,
    deviceName: String,
    deviceRole: String,
    appVersion: String,
    serverPort: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val devices by DeviceDiscovery.devices.collectAsState()
    val isScanning by DeviceDiscovery.isScanning.collectAsState()
    val currentSettings by settingsRepository.settingsFlow.collectAsState()

    var phase by remember { mutableStateOf(IoPhase.SCANNING) }
    var selectedDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var transferProgress by remember { mutableStateOf(0f) }
    var statusMessage by remember { mutableStateOf("Mencari perangkat...") }
    var errorMessage by remember { mutableStateOf("") }

    // ============================================================
    // MULAI SCAN SAAT DIBUKA
    // ============================================================
    LaunchedEffect(Unit) {
        DeviceDiscovery.configure(
            name = deviceName,
            role = deviceRole,
            version = appVersion,
            port = serverPort
        )
        DeviceDiscovery.startScan(context, scope)
    }

    // ============================================================
    // CLEANUP SAAT DITUTUP
    // ============================================================
    DisposableEffect(Unit) {
        onDispose {
            DeviceDiscovery.stopScan()
        }
    }

    // ============================================================
    // AUTO-CONNECT SAAT DEVICE DIPILIH
    // ============================================================
    LaunchedEffect(selectedDevice, phase) {
        if (phase == IoPhase.CONNECTING && selectedDevice != null) {
            statusMessage = "Menghubungkan ke ${selectedDevice!!.name}..."
            val ok = RemoteControlClient.handshake(
                selectedDevice!!.ip,
                selectedDevice!!.port
            )
            if (ok) {
                phase = IoPhase.CONNECTED
                statusMessage = "Terhubung dengan ${selectedDevice!!.name}"
            } else {
                phase = IoPhase.ERROR
                errorMessage = "Gagal terhubung ke ${selectedDevice!!.name}. Pastikan Remote Server aktif di perangkat lain."
            }
        }
    }

    // ============================================================
    // REAL SEND — pakai RemoteControlClient
    // ============================================================
    LaunchedEffect(phase) {
        if (phase == IoPhase.SENDING) {
            val target = selectedDevice ?: return@LaunchedEffect
            transferProgress = 0f
            statusMessage = "Menyiapkan data..."

            val result = RemoteControlClient.sendSettings(
                targetIp = target.ip,
                targetPort = target.port,
                settings = currentSettings,
                onProgress = { prog ->
                    transferProgress = prog
                    statusMessage = when {
                        prog < 0.15f -> "Menyiapkan data..."
                        prog < 0.30f -> "Menghubungi ${target.name}..."
                        prog < 0.95f -> "Mengirim pengaturan..."
                        else -> "Menunggu konfirmasi..."
                    }
                }
            )

            if (result.success) {
                transferProgress = 1f
                statusMessage = "Transfer selesai! Perangkat ${target.name} akan restart."
                delay(3000)
                phase = IoPhase.DONE
            } else {
                phase = IoPhase.ERROR
                errorMessage = result.message
            }
        }
    }
    
    // ============================================================
    // UI UTAMA
    // ============================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(IoBg, Color(0xFF041020))
                )
            )
    ) {
        IoTopBar(
            title = "iO Control",
            subtitle = when (phase) {
                IoPhase.SCANNING -> "Mencari perangkat..."
                IoPhase.CONNECTING -> "Menghubungkan..."
                IoPhase.CONNECTED -> "Terhubung"
                IoPhase.SENDING -> "Mengirim..."
                IoPhase.RECEIVING -> "Menunggu pengirim..."
                IoPhase.DONE -> "Selesai"
                IoPhase.ERROR -> "Error"
            },
            onBack = onBack
        )

        when (phase) {
            IoPhase.SCANNING, IoPhase.CONNECTING -> {
                ScanningView(
                    devices = devices,
                    isScanning = isScanning,
                    myRole = deviceRole,
                    onDeviceClick = { device ->
                        selectedDevice = device
                        phase = IoPhase.CONNECTING
                    },
                    onRescan = {
                        DeviceDiscovery.stopScan()
                        scope.launch {
                            delay(300)
                            DeviceDiscovery.startScan(context, scope)
                        }
                    }
                )
            }
            IoPhase.CONNECTED -> {
                ConnectedView(
                    device = selectedDevice,
                    onSend = { phase = IoPhase.SENDING },
                    onReceive = { phase = IoPhase.RECEIVING },
                    onDisconnect = {
                        selectedDevice = null
                        phase = IoPhase.SCANNING
                        scope.launch {
                            DeviceDiscovery.startScan(context, scope)
                        }
                    }
                )
            }
            IoPhase.SENDING -> {
                TransferProgressView(
                    isSending = true,
                    progress = transferProgress,
                    targetName = selectedDevice?.name ?: "Perangkat",
                    message = statusMessage
                )
            }
            IoPhase.RECEIVING -> {
                WaitingReceiveView(
                    deviceName = selectedDevice?.name ?: "Pengirim",
                    onCancel = {
                        phase = IoPhase.CONNECTED
                    }
                )
            }
            IoPhase.DONE -> {
                DoneView(message = statusMessage)
            }
            IoPhase.ERROR -> {
                ErrorView(
                    message = errorMessage,
                    onRetry = {
                        errorMessage = ""
                        phase = IoPhase.CONNECTED
                    },
                    onBackToScan = {
                        selectedDevice = null
                        errorMessage = ""
                        phase = IoPhase.SCANNING
                        scope.launch {
                            DeviceDiscovery.startScan(context, scope)
                        }
                    }
                )
            }
        }
    }
}

// ============================================================
// TOP BAR
// ============================================================
@Composable
private fun IoTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF071A2E))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(IoCard)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = IoBlueLight,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = IoBlueLight
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}

// ============================================================
// SCANNING VIEW — RADAR BIRU + DAFTAR DEVICE
// ============================================================
@Composable
private fun ScanningView(
    devices: List<DiscoveredDevice>,
    isScanning: Boolean,
    myRole: String,
    onDeviceClick: (DiscoveredDevice) -> Unit,
    onRescan: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            RadarView(devices = devices, isScanning = isScanning)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(0.5f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Perangkat Ditemukan: ${devices.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (devices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(IoCard.copy(alpha = 0.3f))
                        .border(1.dp, IoBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = IoBlueLight,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mencari perangkat Masjid.io lain...",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pastikan WiFi/Hotspot sama",
                            fontSize = 12.sp,
                            color = TextSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(devices) { device ->
                        DeviceCard(
                            device = device,
                            onClick = { onDeviceClick(device) }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(IoBlue.copy(alpha = 0.15f))
                    .border(1.5.dp, IoBlue, RoundedCornerShape(12.dp))
                    .clickable { onRescan() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔄 SCAN ULANG",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IoBlueLight
                )
            }
        }
    }
}

// ============================================================
// RADAR VIEW
// ============================================================
@Composable
private fun RadarView(
    devices: List<DiscoveredDevice>,
    isScanning: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    val sweepRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().scale(pulseScale)) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.minDimension / 2 - 16.dp.toPx()

            drawCircle(
                color = IoBlue.copy(alpha = 0.4f),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            for (i in 1..3) {
                drawCircle(
                    color = IoBlue.copy(alpha = 0.15f),
                    radius = maxRadius * (i / 3f),
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            drawLine(
                color = IoBlue.copy(alpha = 0.15f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = IoBlue.copy(alpha = 0.15f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.dp.toPx()
            )

            if (isScanning) {
                rotate(sweepRotation, pivot = center) {
                    val sweepSweep = 60f
                    for (i in 0 until 30) {
                        val angle = Math.toRadians((i * sweepSweep / 30).toDouble()).toFloat()
                        val alpha = (i / 30f) * 0.6f
                        drawLine(
                            color = IoBlueLight.copy(alpha = alpha),
                            start = center,
                            end = Offset(
                                center.x + maxRadius * kotlin.math.cos(angle),
                                center.y + maxRadius * kotlin.math.sin(angle)
                            ),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }
            }

            devices.forEachIndexed { index, _ ->
                val angle = (index * 137.5) % 360.0
                val radiusFactor = 0.4f + ((index * 0.2f) % 0.5f)
                val rad = Math.toRadians(angle)
                val dotX = center.x + maxRadius * radiusFactor * kotlin.math.cos(rad).toFloat()
                val dotY = center.y + maxRadius * radiusFactor * kotlin.math.sin(rad).toFloat()

                drawCircle(
                    color = IoGreen,
                    radius = 8.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
                drawCircle(
                    color = IoGreenLight.copy(alpha = 0.4f),
                    radius = 14.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }

            drawCircle(
                color = IslamicGold,
                radius = 10.dp.toPx(),
                center = center
            )
            drawCircle(
                color = IslamicGoldLight.copy(alpha = 0.5f),
                radius = 18.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Text(
            text = "ANDA",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 44.dp)
        )
    }
}

// ============================================================
// DEVICE CARD
// ============================================================
@Composable
private fun DeviceCard(
    device: DiscoveredDevice,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(IoCard)
            .border(1.5.dp, IoBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(IoBlue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (device.role == "TV") Icons.Default.Tv else Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = IoBlueLight,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${device.role} • ${device.version}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = "IP: ${device.ip}",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        }
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(IoGreen)
        )
    }
}

// ============================================================
// CONNECTED VIEW — 2 TOMBOL KIRIM & TERIMA
// ============================================================
@Composable
private fun ConnectedView(
    device: DiscoveredDevice?,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✓ TERHUBUNG",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = IoGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = device?.name ?: "Perangkat",
            fontSize = 16.sp,
            color = TextPrimary
        )
        Text(
            text = "${device?.role} • ${device?.ip}",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Pilih aksi:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BigActionButton(
                icon = Icons.Default.Send,
                title = "KIRIM",
                subtitle = "Kirim pengaturan\ndari perangkat ini",
                color = IoBlue,
                modifier = Modifier.weight(1f),
                onClick = onSend
            )
            BigActionButton(
                icon = Icons.Default.Download,
                title = "TERIMA",
                subtitle = "Terima pengaturan\ndari perangkat lain",
                color = IoGreen,
                modifier = Modifier.weight(1f),
                onClick = onReceive
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FF5252))
                .border(1.5.dp, IoRed, RoundedCornerShape(12.dp))
                .clickable { onDisconnect() }
                .padding(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Text(
                text = "PUTUSKAN KONEKSI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF8A80)
            )
        }
    }
}

@Composable
private fun BigActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(2.dp, color, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================
// TRANSFER PROGRESS VIEW (SENDING)
// ============================================================
@Composable
private fun TransferProgressView(
    isSending: Boolean,
    progress: Float,
    targetName: String,
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSending) "📤 MENGIRIM..." else "📥 MENERIMA...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSending) IoBlue else IoGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ke $targetName",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A2E44))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                if (isSending) IoBlue else IoGreen,
                                if (isSending) IoBlueLight else IoGreenLight
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSending) IoBlueLight else IoGreenLight,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = message,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================
// WAITING RECEIVE VIEW (RECEIVING)
// ============================================================
@Composable
private fun WaitingReceiveView(
    deviceName: String,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waiting")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waiting_pulse"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(IoGreen.copy(alpha = pulseAlpha * 0.3f))
                .border(3.dp, IoGreen.copy(alpha = pulseAlpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = IoGreen,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "MENUNGGU PENGIRIM",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = IoGreen
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Minta $deviceName untuk menekan tombol KIRIM di perangkatnya.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Perangkat ini siap menerima pengaturan.\n" +
                    "Setelah data diterima, aplikasi akan otomatis restart.",
            fontSize = 12.sp,
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FF5252))
                .border(1.5.dp, IoRed, RoundedCornerShape(12.dp))
                .clickable { onCancel() }
                .padding(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Text(
                text = "BATAL",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF8A80)
            )
        }
    }
}

// ============================================================
// DONE VIEW
// ============================================================
@Composable
private fun DoneView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✓",
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = IoGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SELESAI",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = IoGreen
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================
// ERROR VIEW
// ============================================================
@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    onBackToScan: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = IoRed
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "GAGAL",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = IoRed
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(IoBlue.copy(alpha = 0.15f))
                    .border(1.5.dp, IoBlue, RoundedCornerShape(12.dp))
                    .clickable { onRetry() }
                    .padding(horizontal = 32.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "COBA LAGI",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IoBlueLight
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FF5252))
                    .border(1.5.dp, IoRed, RoundedCornerShape(12.dp))
                    .clickable { onBackToScan() }
                    .padding(horizontal = 32.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "SCAN ULANG",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A80)
                )
            }
        }
    }
}
