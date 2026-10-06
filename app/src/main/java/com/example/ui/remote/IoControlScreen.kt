package dev.andikune.masjidio.ui.remote

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
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andikune.masjidio.data.local.SettingsRepository
import dev.andikune.masjidio.ui.components.NeonFocusBorder
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.util.MediaTransferHelper
import dev.andikune.masjidio.util.RemoteControlClient
import dev.andikune.masjidio.util.SettingsTransferHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================================================
// WARNA TEMA iO CONTROL
// ============================================================
private val IoBlue = Color(0xFF2196F3)
private val IoBlueLight = Color(0xFF64B5F6)
private val IoBlueDark = Color(0xFF0D47A1)
private val IoGreen = Color(0xFF4CAF50)
private val IoGreenLight = Color(0xFF81C784)
private val IoRed = Color(0xFFFF5252)
private val IoBg = Color(0xFF0A1929)
private val IoCard = Color(0xFF132F4C)
private val IoAmber = Color(0xFFFFA726)
private val IoPurple = Color(0xFFBB86FC)

// ============================================================
// STATE MACHINE
// ============================================================
enum class IoPhase {
    SCANNING,
    CONNECTING,
    CONNECTED,
    SENDING_SETTINGS,
    SENDING_MEDIA,
    VERIFYING,
    READY_TO_RESTART,
    RECEIVING,
    DONE,
    ERROR
}

// ============================================================
// MAIN COMPOSABLE
// V1.04.423: Hapus tombol Scan Barcode dari iO Control
// (fokus ke web remote lewat RemoteSettingsPane saja)
// ============================================================
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

    // ===== STATE UTAMA =====
    var phase by remember { mutableStateOf(IoPhase.SCANNING) }
    var selectedDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var transferProgress by remember { mutableStateOf(0f) }
    var statusMessage by remember { mutableStateOf("Mencari perangkat...") }
    var errorMessage by remember { mutableStateOf("") }

    var mediaProgress by remember { mutableStateOf<MediaTransferHelper.TransferProgress?>(null) }

    var transferResult by remember { mutableStateOf<RemoteControlClient.MediaTransferResult?>(null) }
    var allMediaList by remember { mutableStateOf<List<MediaTransferHelper.MediaFileInfo>>(emptyList()) }
    var failedFieldKeys by remember { mutableStateOf<List<String>>(emptyList()) }
    var showFailureDialog by remember { mutableStateOf(false) }
    var isRetrying by remember { mutableStateOf(false) }

    var showHelp by remember { mutableStateOf(false) }
    var currentIp by remember { mutableStateOf("...") }
    var isWifiOn by remember { mutableStateOf(false) }

    val scanButtonFocusRequester = remember { FocusRequester() }
    val firstDeviceFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        while (true) {
            currentIp = NetworkHelper.getWiFiIp(context) ?: "Tidak terdeteksi"
            isWifiOn = NetworkHelper.isWiFiConnected(context)
            delay(5000)
        }
    }

    LaunchedEffect(Unit) {
        val deviceUniqueId = try {
            android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            ) ?: "masjid-${java.util.UUID.randomUUID().toString().take(8)}"
        } catch (e: Exception) {
            "masjid-${java.util.UUID.randomUUID().toString().take(8)}"
        }

        DeviceDiscovery.configure(
            deviceId = deviceUniqueId,
            name = deviceName,
            role = deviceRole,
            version = appVersion,
            port = serverPort
        )
        DeviceDiscovery.startScan(context, scope)
        delay(300)
        runCatching { scanButtonFocusRequester.requestFocus() }
    }

    LaunchedEffect(devices, phase) {
        if (phase == IoPhase.SCANNING && devices.isNotEmpty()) {
            delay(150)
            runCatching { firstDeviceFocusRequester.requestFocus() }
        }
    }

    DisposableEffect(Unit) {
        onDispose { DeviceDiscovery.stopScan() }
    }

    LaunchedEffect(selectedDevice, phase) {
        if (phase == IoPhase.CONNECTING && selectedDevice != null) {
            val target = selectedDevice!!
            statusMessage = "Menghubungi ${target.name}..."

            val reachable = NetworkHelper.testConnectivity(target.ip, target.port, 3000)
            if (!reachable) {
                phase = IoPhase.ERROR
                errorMessage = "Tidak bisa menjangkau ${target.name} (${target.ip}:${target.port}).\n\n" +
                        "Kemungkinan:\n" +
                        "• Remote Server belum aktif di perangkat tujuan\n" +
                        "• WiFi berbeda jaringan\n" +
                        "• Perangkat tujuan sedang offline"
                return@LaunchedEffect
            }

            val ok = RemoteControlClient.handshake(target.ip, target.port)
            if (ok) {
                phase = IoPhase.CONNECTED
                statusMessage = "Terhubung dengan ${target.name}"
            } else {
                phase = IoPhase.ERROR
                errorMessage = "Gagal handshake dengan ${target.name}.\n\n" +
                        "Kemungkinan:\n" +
                        "• Remote Server tidak merespon\n" +
                        "• Port berbeda (perangkat tujuan pakai port lain)\n" +
                        "• Server sibuk"
            }
        }
    }
        // ============================================================
    // LAUNCHER: Kirim Settings + Media
    // ============================================================
    LaunchedEffect(phase) {
        when (phase) {
            IoPhase.SENDING_SETTINGS -> {
                val target = selectedDevice ?: return@LaunchedEffect
                transferProgress = 0f
                statusMessage = "Mengirim pengaturan..."

                val result = RemoteControlClient.sendSettings(
                    targetIp = target.ip,
                    targetPort = target.port,
                    settings = currentSettings,
                    onProgress = { prog ->
                        transferProgress = prog
                        statusMessage = when {
                            prog < 0.15f -> "Menyiapkan pengaturan..."
                            prog < 0.30f -> "Menghubungi ${target.name}..."
                            prog < 0.95f -> "Mengirim pengaturan..."
                            else -> "Menunggu konfirmasi..."
                        }
                    }
                )

                if (result.success) {
                    allMediaList = MediaTransferHelper.collectMediaFiles(currentSettings)
                    phase = IoPhase.SENDING_MEDIA
                } else {
                    phase = IoPhase.ERROR
                    errorMessage = result.message
                }
            }

            IoPhase.SENDING_MEDIA -> {
                val target = selectedDevice ?: return@LaunchedEffect
                statusMessage = "Mengirim media..."

                if (allMediaList.isEmpty()) {
                    transferResult = RemoteControlClient.MediaTransferResult(
                        success = true,
                        message = "Tidak ada media untuk dikirim",
                        filesTransferred = 0,
                        filesFailed = 0
                    )
                    failedFieldKeys = emptyList()
                    phase = IoPhase.VERIFYING
                    return@LaunchedEffect
                }

                val mediaResult = RemoteControlClient.sendMediaFilesChunked(
                    context = context,
                    targetIp = target.ip,
                    targetPort = target.port,
                    mediaList = allMediaList,
                    onProgress = { progress ->
                        mediaProgress = progress
                        statusMessage = progress.message
                    }
                )

                transferResult = mediaResult
                failedFieldKeys = mediaResult.failures.map { it.fieldKey }

                phase = IoPhase.VERIFYING
            }

            IoPhase.VERIFYING -> {
                val result = transferResult
                if (result == null) {
                    phase = IoPhase.ERROR
                    errorMessage = "Hasil transfer tidak diketahui"
                    return@LaunchedEffect
                }

                statusMessage = if (result.filesFailed == 0) {
                    "Semua ${result.filesTransferred} file terkirim dengan baik"
                } else {
                    "${result.filesTransferred} sukses, ${result.filesFailed} gagal"
                }

                delay(500)
                phase = IoPhase.READY_TO_RESTART
            }

            IoPhase.READY_TO_RESTART -> {
                // Tidak ada aksi otomatis.
            }

            else -> { /* no-op */ }
        }
    }

    if (showHelp) {
        IoControlHelpSheet(onClose = { showHelp = false })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(IoBg, Color(0xFF041020))))
    ) {
        IoTopBar(
            title = "iO Control",
            subtitle = when (phase) {
                IoPhase.SCANNING -> "Mencari perangkat..."
                IoPhase.CONNECTING -> "Menghubungkan..."
                IoPhase.CONNECTED -> "Terhubung"
                IoPhase.SENDING_SETTINGS -> "Mengirim pengaturan..."
                IoPhase.SENDING_MEDIA -> "Mengirim media..."
                IoPhase.VERIFYING -> "Memeriksa hasil transfer..."
                IoPhase.READY_TO_RESTART -> "Menunggu konfirmasi restart"
                IoPhase.RECEIVING -> "Menunggu pengirim..."
                IoPhase.DONE -> "Selesai"
                IoPhase.ERROR -> "Error"
            },
            onBack = onBack,
            onHelp = { showHelp = true }
        )

        NetworkInfoBar(
            ip = currentIp,
            wifiOn = isWifiOn,
            deviceName = deviceName
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
                    },
                    scanButtonFocusRequester = scanButtonFocusRequester,
                    firstDeviceFocusRequester = firstDeviceFocusRequester
                )
            }
            IoPhase.CONNECTED -> {
                ConnectedView(
                    device = selectedDevice,
                    onSend = {
                        transferResult = null
                        failedFieldKeys = emptyList()
                        allMediaList = emptyList()
                        phase = IoPhase.SENDING_SETTINGS
                    },
                    onReceive = { phase = IoPhase.RECEIVING },
                    onDisconnect = {
                        selectedDevice = null
                        phase = IoPhase.SCANNING
                        scope.launch { DeviceDiscovery.startScan(context, scope) }
                    }
                )
            }
            IoPhase.SENDING_SETTINGS -> {
                TransferProgressView(
                    isSending = true,
                    progress = transferProgress,
                    targetName = selectedDevice?.name ?: "Perangkat",
                    message = statusMessage
                )
            }
            IoPhase.SENDING_MEDIA -> {
                MediaProgressView(
                    progress = mediaProgress,
                    targetName = selectedDevice?.name ?: "Perangkat"
                )
            }
            IoPhase.VERIFYING -> {
                VerifyingView(
                    message = statusMessage,
                    targetName = selectedDevice?.name ?: "Perangkat"
                )
            }
            IoPhase.READY_TO_RESTART -> {
                ReadyToRestartView(
                    result = transferResult,
                    targetName = selectedDevice?.name ?: "Perangkat",
                    allMediaList = allMediaList,
                    onConfirmRestart = {
                        scope.launch {
                            statusMessage = "Mengirim sinyal restart..."
                            val target = selectedDevice
                            if (target != null) {
                                val finalResult = RemoteControlClient.sendFinalizeSignal(
                                    targetIp = target.ip,
                                    targetPort = target.port
                                )
                                if (finalResult.success) {
                                    phase = IoPhase.DONE
                                    statusMessage = "Restart sedang dilakukan di ${target.name}"
                                } else {
                                    phase = IoPhase.ERROR
                                    errorMessage = "Gagal kirim sinyal restart:\n${finalResult.message}"
                                }
                            }
                        }
                    },
                    onShowFailures = { showFailureDialog = true },
                    onRetryFailed = {
                        val target = selectedDevice ?: return@ReadyToRestartView
                        val failedKeys = failedFieldKeys
                        if (failedKeys.isEmpty()) return@ReadyToRestartView

                        isRetrying = true
                        scope.launch {
                            statusMessage = "Mengirim ulang ${failedKeys.size} file gagal..."

                            val retryResult = RemoteControlClient.retryFailedMedia(
                                context = context,
                                targetIp = target.ip,
                                targetPort = target.port,
                                allMediaList = allMediaList,
                                failedFieldKeys = failedKeys,
                                onProgress = { progress ->
                                    mediaProgress = progress
                                    statusMessage = progress.message
                                }
                            )

                            isRetrying = false

                            val prevSuccess = (transferResult?.filesTransferred ?: 0)
                            val newSuccess = retryResult.filesTransferred
                            val newFailed = retryResult.filesFailed

                            transferResult = RemoteControlClient.MediaTransferResult(
                                success = newFailed == 0,
                                message = if (newFailed == 0)
                                    "Retry berhasil — semua file sudah masuk"
                                else
                                    "${newSuccess} sukses, ${newFailed} masih gagal",
                                filesTransferred = prevSuccess + newSuccess,
                                filesFailed = newFailed,
                                totalBytes = (transferResult?.totalBytes ?: 0L) + retryResult.totalBytes,
                                failures = retryResult.failures,
                                allFailuresLog = retryResult.allFailuresLog
                            )
                            failedFieldKeys = retryResult.failures.map { it.fieldKey }
                        }
                    },
                    onSkipRestart = {
                        phase = IoPhase.DONE
                        statusMessage = "Selesai. Template akan aktif otomatis saat app dibuka ulang."
                    }
                )
            }
            IoPhase.RECEIVING -> {
                WaitingReceiveView(
                    deviceName = selectedDevice?.name ?: "Pengirim",
                    onCancel = { phase = IoPhase.CONNECTED }
                )
            }
            IoPhase.DONE -> DoneView(message = statusMessage)
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
                        scope.launch { DeviceDiscovery.startScan(context, scope) }
                    }
                )
            }
        }

        // ===== DIALOG LOG KEGAGALAN =====
        if (showFailureDialog) {
            FailureLogDialog(
                log = transferResult?.allFailuresLog ?: "(tidak ada log)",
                onDismiss = { showFailureDialog = false }
            )
        }
    }
}
// ============================================================
// TOP BAR — V1.04.423: Hapus tombol Scan Barcode
// ============================================================
@Composable
private fun IoTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onHelp: () -> Unit
) {
    val interactionSourceBack = remember { MutableInteractionSource() }
    val isFocusedBack by interactionSourceBack.collectIsFocusedAsState()
    val isPressedBack by interactionSourceBack.collectIsPressedAsState()

    val interactionSourceHelp = remember { MutableInteractionSource() }
    val isFocusedHelp by interactionSourceHelp.collectIsFocusedAsState()
    val isPressedHelp by interactionSourceHelp.collectIsPressedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF071A2E))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NeonFocusBorder(
                focused = isFocusedBack,
                pressed = isPressedBack,
                borderWidth = 5.dp,
                cornerRadius = 12.dp,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(IoCard)
                        .focusable(interactionSource = interactionSourceBack)
                        .clickable(
                            interactionSource = interactionSourceBack,
                            indication = null
                        ) { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = IoBlueLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = IoBlueLight)
                Text(text = subtitle, fontSize = 13.sp, color = TextSecondary)
            }
        }

        NeonFocusBorder(
            focused = isFocusedHelp,
            pressed = isPressedHelp,
            borderWidth = 5.dp,
            cornerRadius = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(IoBlue.copy(alpha = 0.2f))
                    .focusable(interactionSource = interactionSourceHelp)
                    .clickable(
                        interactionSource = interactionSourceHelp,
                        indication = null
                    ) { onHelp() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Panduan",
                    tint = IoBlueLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Panduan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IoBlueLight)
            }
        }
    }
}

// ============================================================
// INFO BAR JARINGAN — V1.04.423: Hapus tombol QR
// ============================================================
@Composable
private fun NetworkInfoBar(
    ip: String,
    wifiOn: Boolean,
    deviceName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1F30))
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (wifiOn) IoGreen else IoRed)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (wifiOn) "WiFi Aktif" else "WiFi Nonaktif",
                fontSize = 11.sp,
                color = if (wifiOn) IoGreen else IoRed,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "IP: $ip",
                fontSize = 11.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace
            )
        }

        Text(text = deviceName, fontSize = 11.sp, color = TextSecondary.copy(alpha = 0.7f))
    }
}

// ============================================================
// SCANNING VIEW — V1.04.423: Hapus tombol Web Remote QR
// ============================================================
@Composable
private fun ScanningView(
    devices: List<DiscoveredDevice>,
    isScanning: Boolean,
    myRole: String,
    onDeviceClick: (DiscoveredDevice) -> Unit,
    onRescan: () -> Unit,
    scanButtonFocusRequester: FocusRequester,
    firstDeviceFocusRequester: FocusRequester
) {
    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(modifier = Modifier.weight(0.5f).fillMaxHeight(), contentAlignment = Alignment.Center) {
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CircularProgressIndicator(color = IoBlueLight, modifier = Modifier.size(48.dp))
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
                        val isFirst = devices.firstOrNull()?.ip == device.ip
                        DeviceCard(
                            device = device,
                            onClick = { onDeviceClick(device) },
                            focusRequester = if (isFirst) firstDeviceFocusRequester else null
                        )
                    }
                }
            }

            ScanButton(onClick = onRescan, focusRequester = scanButtonFocusRequester)
        }
    }
}

// ============================================================
// SCAN BUTTON
// ============================================================
@Composable
private fun ScanButton(onClick: () -> Unit, focusRequester: FocusRequester) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(IoBlue.copy(alpha = 0.15f))
                .focusRequester(focusRequester)
                .focusable(interactionSource = interactionSource)
                .clickable(interactionSource = interactionSource, indication = null) { onClick() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔄 SCAN ULANG", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IoBlueLight)
        }
    }
}
// ============================================================
// RADAR VIEW
// ============================================================
@Composable
private fun RadarView(devices: List<DiscoveredDevice>, isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    val sweepRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "radar_sweep"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "radar_pulse"
    )

    Box(modifier = Modifier.fillMaxSize().aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().scale(pulseScale)) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.minDimension / 2 - 16.dp.toPx()

            drawCircle(color = IoBlue.copy(alpha = 0.4f), radius = maxRadius, center = center, style = Stroke(width = 2.dp.toPx()))
            for (i in 1..3) {
                drawCircle(color = IoBlue.copy(alpha = 0.15f), radius = maxRadius * (i / 3f), center = center, style = Stroke(width = 1.dp.toPx()))
            }
            drawLine(color = IoBlue.copy(alpha = 0.15f), start = Offset(center.x - maxRadius, center.y), end = Offset(center.x + maxRadius, center.y), strokeWidth = 1.dp.toPx())
            drawLine(color = IoBlue.copy(alpha = 0.15f), start = Offset(center.x, center.y - maxRadius), end = Offset(center.x, center.y + maxRadius), strokeWidth = 1.dp.toPx())

            if (isScanning) {
                rotate(sweepRotation, pivot = center) {
                    val sweepSweep = 60f
                    for (i in 0 until 30) {
                        val angle = Math.toRadians((i * sweepSweep / 30).toDouble()).toFloat()
                        val alpha = (i / 30f) * 0.6f
                        drawLine(
                            color = IoBlueLight.copy(alpha = alpha),
                            start = center,
                            end = Offset(center.x + maxRadius * kotlin.math.cos(angle), center.y + maxRadius * kotlin.math.sin(angle)),
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
                drawCircle(color = IoGreen, radius = 8.dp.toPx(), center = Offset(dotX, dotY))
                drawCircle(color = IoGreenLight.copy(alpha = 0.4f), radius = 14.dp.toPx(), center = Offset(dotX, dotY))
            }

            drawCircle(color = IslamicGold, radius = 10.dp.toPx(), center = center)
            drawCircle(color = IslamicGoldLight.copy(alpha = 0.5f), radius = 18.dp.toPx(), center = center, style = Stroke(width = 2.dp.toPx()))
        }

        Text(
            text = "ANDA",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGold,
            modifier = Modifier.align(Alignment.Center).padding(top = 44.dp)
        )
    }
}

// ============================================================
// DEVICE CARD
// ============================================================
@Composable
private fun DeviceCard(
    device: DiscoveredDevice,
    onClick: () -> Unit,
    focusRequester: FocusRequester? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(IoCard)
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .focusable(interactionSource = interactionSource)
                .clickable(interactionSource = interactionSource, indication = null) { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(IoBlue.copy(alpha = 0.2f)),
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
                Text(text = device.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${device.role} • ${device.version}", fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = "IP: ${device.ip}:${device.port}",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace
                )
            }
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(IoGreen))
        }
    }
}
// ============================================================
// CONNECTED VIEW
// ============================================================
@Composable
private fun ConnectedView(
    device: DiscoveredDevice?,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "✓ TERHUBUNG", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = IoGreen)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = device?.name ?: "Perangkat", fontSize = 16.sp, color = TextPrimary)
        Text(text = "${device?.role} • ${device?.ip}:${device?.port}", fontSize = 13.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Pilih aksi:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BigActionButton(
                icon = Icons.Default.Send,
                title = "KIRIM",
                subtitle = "Kirim pengaturan +\nmedia dari HP ini",
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
        DisconnectButton(onClick = onDisconnect)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================================
// DISCONNECT BUTTON
// ============================================================
@Composable
private fun DisconnectButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 12.dp
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FF5252))
                .focusable(interactionSource = interactionSource)
                .clickable(interactionSource = interactionSource, indication = null) { onClick() }
                .padding(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Text(text = "PUTUSKAN KONEKSI", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
        }
    }
}

// ============================================================
// BIG ACTION BUTTON
// ============================================================
@Composable
private fun BigActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 20.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(color.copy(alpha = 0.15f))
                .focusable(interactionSource = interactionSource)
                .clickable(interactionSource = interactionSource, indication = null) { onClick() }
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

// ============================================================
// TRANSFER PROGRESS VIEW (Settings)
// ============================================================
@Composable
private fun TransferProgressView(
    isSending: Boolean,
    progress: Float,
    targetName: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = if (isSending) "📤 MENGIRIM..." else "📥 MENERIMA...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSending) IoBlue else IoGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "ke $targetName", fontSize = 14.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier.fillMaxWidth(0.8f).height(16.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1A2E44))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
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
        Text(text = message, fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ============================================================
// MEDIA PROGRESS VIEW
// ============================================================
@Composable
private fun MediaProgressView(
    progress: MediaTransferHelper.TransferProgress?,
    targetName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "📤 MENGIRIM MEDIA...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IoBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "ke $targetName", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(32.dp))

        if (progress == null) {
            CircularProgressIndicator(color = IoBlueLight)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Menyiapkan data media...", fontSize = 13.sp, color = TextSecondary)
        } else {
            Text(
                text = "File ${progress.currentFileIndex} dari ${progress.totalFiles}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IoBlueLight
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = progress.currentFileName,
                fontSize = 13.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "File ini:", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier.fillMaxWidth(0.85f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFF1A2E44))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.currentFileProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.horizontalGradient(listOf(IoBlue, IoBlueLight)))
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress.currentFileProgress * 100).toInt()}%",
                fontSize = 12.sp,
                color = IoBlueLight,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Total keseluruhan:", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier.fillMaxWidth(0.85f).height(16.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1A2E44))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.overallProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.horizontalGradient(listOf(IoGreen, IoGreenLight)))
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress.overallProgress * 100).toInt()}%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IoGreenLight,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(20.dp))

            val phaseColor = when (progress.phase) {
                "error" -> IoRed
                "done" -> IoGreen
                "transferring" -> IoBlueLight
                "compressing" -> IoAmber
                else -> TextSecondary
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(phaseColor.copy(alpha = 0.12f))
                    .border(1.dp, phaseColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = progress.message,
                    fontSize = 13.sp,
                    color = phaseColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ============================================================
// VERIFYING VIEW
// ============================================================
@Composable
private fun VerifyingView(
    message: String,
    targetName: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "verify")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "verify_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(IoPurple.copy(alpha = pulseAlpha * 0.2f))
                .border(3.dp, IoPurple.copy(alpha = pulseAlpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = IoPurple,
                modifier = Modifier.size(80.dp),
                strokeWidth = 5.dp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "🔍 MEMERIKSA HASIL TRANSFER",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = IoPurple,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ke $targetName",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(12.dp))
                .background(IoPurple.copy(alpha = 0.12f))
                .border(1.dp, IoPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mohon tunggu, sedang memverifikasi semua file...",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
// ============================================================
// READY TO RESTART VIEW
// ============================================================
@Composable
private fun ReadyToRestartView(
    result: RemoteControlClient.MediaTransferResult?,
    targetName: String,
    allMediaList: List<MediaTransferHelper.MediaFileInfo>,
    onConfirmRestart: () -> Unit,
    onShowFailures: () -> Unit,
    onRetryFailed: () -> Unit,
    onSkipRestart: () -> Unit
) {
    val totalSuccess = result?.filesTransferred ?: 0
    val totalFailed = result?.filesFailed ?: 0
    val failures = result?.failures ?: emptyList()
    val isAllSuccess = totalFailed == 0

    val photoCount = allMediaList.count { it.fileType == "photo" }
    val videoCount = allMediaList.count { it.fileType == "video" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    if (isAllSuccess) IoGreen.copy(alpha = 0.2f)
                    else IoAmber.copy(alpha = 0.2f)
                )
                .border(
                    3.dp,
                    if (isAllSuccess) IoGreen else IoAmber,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isAllSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isAllSuccess) IoGreen else IoAmber,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isAllSuccess) "✅ SEMUA FILE TERKIRIM" else "⚠️ SEBAGIAN FILE GAGAL",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAllSuccess) IoGreen else IoAmber,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ke $targetName",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(14.dp))
                .background(IoCard.copy(alpha = 0.7f))
                .border(
                    1.5.dp,
                    if (isAllSuccess) IoGreen.copy(alpha = 0.5f) else IoAmber.copy(alpha = 0.5f),
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryRow(label = "Total file", value = "${totalSuccess + totalFailed}")
                SummaryRow(label = "Sukses", value = "$totalSuccess", valueColor = IoGreen)
                SummaryRow(
                    label = "Gagal",
                    value = "$totalFailed",
                    valueColor = if (totalFailed > 0) IoRed else TextSecondary
                )
                SummaryRow(label = "📷 Foto", value = "$photoCount")
                SummaryRow(label = "🎬 Video", value = "$videoCount")
            }
        }

        if (isAllSuccess) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IoGreen.copy(alpha = 0.1f))
                    .border(1.dp, IoGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "💡 Semua file sudah diterima dengan baik.\n" +
                            "Klik tombol KONFIRMASI RESTART untuk mengaktifkan template di TV.\n" +
                            "Atau klik LEWATI — template akan aktif otomatis saat app dibuka ulang.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IoAmber.copy(alpha = 0.1f))
                    .border(1.dp, IoAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "📋 File yang gagal:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IoAmber
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    failures.take(5).forEach { f ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "• ",
                                fontSize = 11.sp,
                                color = IoRed,
                                fontWeight = FontWeight.Bold
                            )
                            Column {
                                Text(
                                    text = f.displayName,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "  Gagal di: ${f.failedAt}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                    if (failures.size > 5) {
                        Text(
                            text = "... dan ${failures.size - 5} file lagi",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "💡 Klik \"COBA LAGI\" untuk kirim ulang file yang gagal saja,\n" +
                        "atau klik \"LIHAT LOG\" untuk melihat detail error Kotlin.",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isAllSuccess) {
            ReadyActionButton(
                icon = Icons.Default.Refresh,
                title = "KONFIRMASI RESTART",
                subtitle = "Aktifkan template & restart TV sekarang",
                backgroundColor = IoGreen,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = onConfirmRestart
            )

            Spacer(modifier = Modifier.height(12.dp))

            SmallOutlineButton(
                icon = Icons.Default.Close,
                label = "LEWATI (aktif otomatis saat app dibuka ulang)",
                color = TextSecondary,
                onClick = onSkipRestart
            )
        } else {
            ReadyActionButton(
                icon = Icons.Default.Refresh,
                title = "COBA LAGI (${totalFailed} FILE)",
                subtitle = "Kirim ulang hanya file yang gagal",
                backgroundColor = IoBlue,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = onRetryFailed
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReadyActionButton(
                icon = Icons.Default.Info,
                title = "LIHAT LOG ERROR",
                subtitle = "Tampilkan stack trace Kotlin lengkap",
                backgroundColor = IoPurple.copy(alpha = 0.7f),
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = onShowFailures
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReadyActionButton(
                icon = Icons.Default.Refresh,
                title = "RESTART SAJA",
                subtitle = "Lewati file gagal & terapkan yang sudah masuk",
                backgroundColor = IoAmber.copy(alpha = 0.8f),
                textColor = Color.Black,
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = onConfirmRestart
            )

            Spacer(modifier = Modifier.height(12.dp))

            SmallOutlineButton(
                icon = Icons.Default.Close,
                label = "TIDAK USAH RESTART DULU",
                color = TextSecondary,
                onClick = onSkipRestart
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
// ============================================================
// ROW RINGKASAN
// ============================================================
@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = FontFamily.Monospace
        )
    }
}

// ============================================================
// READY ACTION BUTTON (tombol besar)
// ============================================================
@Composable
private fun ReadyActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 5.dp,
        cornerRadius = 16.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor.copy(alpha = 0.9f))
                .focusable(interactionSource = interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                .padding(vertical = 18.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ============================================================
// SMALL OUTLINE BUTTON (tombol kecil untuk lewati)
// ============================================================
@Composable
private fun SmallOutlineButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    NeonFocusBorder(
        focused = isFocused,
        pressed = isPressed,
        borderWidth = 4.dp,
        cornerRadius = 10.dp
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x22000000))
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .focusable(interactionSource = interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
// ============================================================
// FAILURE LOG DIALOG (popup log error Kotlin)
// ============================================================
@Composable
private fun FailureLogDialog(
    log: String,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(18.dp))
                .background(IoBg)
                .border(2.dp, IoRed.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== HEADER =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = IoRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LOG KEGAGALAN TRANSFER",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = IoRed
                        )
                        Text(
                            text = "Stack trace Kotlin lengkap",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IoCard)
                        .clickable { onDismiss() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "TUTUP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // ===== LOG CONTENT =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF000000))
                    .border(1.dp, IoRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = log.ifBlank { "(tidak ada log)" },
                    fontSize = 11.sp,
                    color = Color(0xFF80E080),
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }

            // ===== TOMBOL SALIN =====
            val copyInteraction = remember { MutableInteractionSource() }
            val copyFocused by copyInteraction.collectIsFocusedAsState()
            val copyPressed by copyInteraction.collectIsPressedAsState()

            NeonFocusBorder(
                focused = copyFocused,
                pressed = copyPressed,
                borderWidth = 5.dp,
                cornerRadius = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (copied) IoGreen.copy(alpha = 0.3f) else IoBlue.copy(alpha = 0.2f)
                        )
                        .border(
                            1.5.dp,
                            if (copied) IoGreen else IoBlue,
                            RoundedCornerShape(12.dp)
                        )
                        .focusable(interactionSource = copyInteraction)
                        .clickable(
                            interactionSource = copyInteraction,
                            indication = null
                        ) {
                            clipboard.setText(AnnotatedString(log))
                            copied = true
                        }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (copied) IoGreen else IoBlueLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (copied) "✓ TERSALIN KE CLIPBOARD" else "📋 SALIN LOG KE CLIPBOARD",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (copied) IoGreen else IoBlueLight
                    )
                }
            }
        }
    }
}
// ============================================================
// WAITING RECEIVE VIEW
// ============================================================
@Composable
private fun WaitingReceiveView(deviceName: String, onCancel: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "waiting")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "waiting_pulse"
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(IoGreen.copy(alpha = pulseAlpha * 0.3f))
                .border(3.dp, IoGreen.copy(alpha = pulseAlpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = IoGreen, modifier = Modifier.size(56.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "MENUNGGU PENGIRIM", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = IoGreen)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Minta $deviceName untuk menekan tombol KIRIM di perangkatnya.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Perangkat ini siap menerima pengaturan.\nSetelah semua data diterima, akan ada countdown 5 detik sebelum restart.",
            fontSize = 12.sp,
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(48.dp))

        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val isPressed by interactionSource.collectIsPressedAsState()

        NeonFocusBorder(
            focused = isFocused,
            pressed = isPressed,
            borderWidth = 5.dp,
            cornerRadius = 12.dp
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FF5252))
                    .focusable(interactionSource = interactionSource)
                    .clickable(interactionSource = interactionSource, indication = null) { onCancel() }
                    .padding(horizontal = 32.dp, vertical = 14.dp)
            ) {
                Text(text = "BATAL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ============================================================
// DONE VIEW
// ============================================================
@Composable
private fun DoneView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "✓", fontSize = 96.sp, fontWeight = FontWeight.Bold, color = IoGreen)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "SELESAI", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = IoGreen)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = message, fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(12.dp))
                .background(IoGreen.copy(alpha = 0.1f))
                .border(1.dp, IoGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "💡 Template yang sudah diterima akan otomatis aktif saat aplikasi dibuka ulang.",
                fontSize = 12.sp,
                color = TextPrimary,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ============================================================
// ERROR VIEW
// ============================================================
@Composable
private fun ErrorView(message: String, onRetry: () -> Unit, onBackToScan: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "⚠️", fontSize = 96.sp, fontWeight = FontWeight.Bold, color = IoRed)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "GAGAL", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = IoRed)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(IoRed.copy(alpha = 0.1f))
                .border(1.dp, IoRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(IoAmber.copy(alpha = 0.1f))
                .border(1.dp, IoAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(text = "💡 Tips troubleshooting:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IoAmber)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Cek Remote Server aktif di perangkat tujuan\n" +
                            "• Pastikan WiFi sama & port server sama\n" +
                            "• File besar butuh waktu (10+ menit)\n" +
                            "• Jangan tutup aplikasi saat transfer\n" +
                            "• Buka tombol Panduan untuk info lengkap",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val retryInteraction = remember { MutableInteractionSource() }
            val retryFocused by retryInteraction.collectIsFocusedAsState()
            val retryPressed by retryInteraction.collectIsPressedAsState()

            NeonFocusBorder(
                focused = retryFocused,
                pressed = retryPressed,
                borderWidth = 5.dp,
                cornerRadius = 12.dp
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(IoBlue.copy(alpha = 0.15f))
                        .focusable(interactionSource = retryInteraction)
                        .clickable(interactionSource = retryInteraction, indication = null) { onRetry() }
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(text = "COBA LAGI", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IoBlueLight)
                }
            }

            val scanInteraction = remember { MutableInteractionSource() }
            val scanFocused by scanInteraction.collectIsFocusedAsState()
            val scanPressed by scanInteraction.collectIsPressedAsState()

            NeonFocusBorder(
                focused = scanFocused,
                pressed = scanPressed,
                borderWidth = 5.dp,
                cornerRadius = 12.dp
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FF5252))
                        .focusable(interactionSource = scanInteraction)
                        .clickable(interactionSource = scanInteraction, indication = null) { onBackToScan() }
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(text = "SCAN ULANG", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
