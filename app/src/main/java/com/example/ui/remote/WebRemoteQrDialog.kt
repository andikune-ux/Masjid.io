package dev.andikune.masjidio.ui.remote

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.andikune.masjidio.ui.components.NeonFocusBorder
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.util.QrCodeGenerator
import kotlinx.coroutines.delay

private val IoBlue = Color(0xFF2196F3)
private val IoBlueLight = Color(0xFF64B5F6)
private val IoGreen = Color(0xFF4CAF50)
private val IoCardBg = Color(0xFF0C1F33)

@Composable
fun WebRemoteQrDialog(
    url: String,
    ip: String,
    port: Int,
    isWifiConnected: Boolean,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val closeFocusRequester = remember { FocusRequester() }
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2500)
            copied = false
        }
    }

    LaunchedEffect(Unit) {
        delay(150)
        runCatching { closeFocusRequester.requestFocus() }
    }

    // Generate QR ImageBitmap
    val qrBitmap = remember(url) {
        if (url.isNotBlank()) {
            QrCodeGenerator.generateQrImageBitmap(url, 600)
        } else null
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(IoCardBg)
                    .border(2.dp, IoBlueLight.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* prevent click through */ }
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(IoBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "QR Code",
                                    tint = IoBlueLight,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "SCAN BARCODE WEB REMOTE",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGoldLight
                                )
                                Text(
                                    text = "Buka Kamera HP & Scan untuk Mengatur TV",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Tombol Tutup (D-pad Focusable)
                        val closeInteraction = remember { MutableInteractionSource() }
                        val closeFocused by closeInteraction.collectIsFocusedAsState()
                        val closePressed by closeInteraction.collectIsPressedAsState()

                        NeonFocusBorder(
                            focused = closeFocused,
                            pressed = closePressed,
                            cornerRadius = 10.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1B3654))
                                    .focusRequester(closeFocusRequester)
                                    .focusable(interactionSource = closeInteraction)
                                    .clickable(
                                        interactionSource = closeInteraction,
                                        indication = null
                                    ) { onDismiss() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Main Content: Row on wide screen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // KOTAK KODE QR (High Contrast White Tile)
                        Box(
                            modifier = Modifier
                                .size(230.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(3.dp, IslamicGold, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap,
                                    contentDescription = "Kode QR Barcode Web Remote",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = IoBlue, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Membuat QR...",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // PETUNJUK SCAN KAMERA HP
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Status WiFi & IP Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF132A42))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = if (isWifiConnected) IoGreen else Color(0xFFFF5252),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isWifiConnected) "WiFi Aktif" else "WiFi Nonaktif",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWifiConnected) IoGreen else Color(0xFFFF5252)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "IP: $ip:$port",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Langkah Mudah Menghubungkan:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldLight
                            )

                            Text(
                                text = "① Pastikan HP dan TV terhubung ke WiFi/Hotspot yang sama.",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                            Text(
                                text = "② Buka aplikasi Kamera di HP Anda lalu arahkan ke barcode QR di samping.",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                            Text(
                                text = "③ Klik notifikasi / tautan web yang muncul di layar HP untuk membuka Web Remote Pengaturan TV.",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // KOTAK ALAMAT URL & TOMBOL SALIN
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF091624))
                            .border(1.dp, IoBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Atau buka browser di HP lalu ketik alamat ini:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = url,
                            fontSize = 12.sp,
                            color = IoBlueLight,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TOMBOL AKSI
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val copyInteraction = remember { MutableInteractionSource() }
                        val copyFocused by copyInteraction.collectIsFocusedAsState()
                        val copyPressed by copyInteraction.collectIsPressedAsState()

                        NeonFocusBorder(
                            focused = copyFocused,
                            pressed = copyPressed,
                            cornerRadius = 10.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (copied) IoGreen.copy(alpha = 0.25f) else Color(0xFF132F4C))
                                    .border(1.5.dp, if (copied) IoGreen else IoBlue, RoundedCornerShape(10.dp))
                                    .focusable(interactionSource = copyInteraction)
                                    .clickable(
                                        interactionSource = copyInteraction,
                                        indication = null
                                    ) {
                                        clipboardManager.setText(AnnotatedString(url))
                                        copied = true
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = if (copied) IoGreen else IoBlueLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (copied) "URL TERSALIN" else "SALIN URL WEB",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (copied) IoGreen else IoBlueLight
                                    )
                                }
                            }
                        }

                        val doneInteraction = remember { MutableInteractionSource() }
                        val doneFocused by doneInteraction.collectIsFocusedAsState()
                        val donePressed by doneInteraction.collectIsPressedAsState()

                        NeonFocusBorder(
                            focused = doneFocused,
                            pressed = donePressed,
                            cornerRadius = 10.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1B3B5C))
                                    .border(1.5.dp, IoBlueLight, RoundedCornerShape(10.dp))
                                    .focusable(interactionSource = doneInteraction)
                                    .clickable(
                                        interactionSource = doneInteraction,
                                        indication = null
                                    ) { onDismiss() }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SELESAI / TUTUP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
