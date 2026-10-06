package dev.andikune.masjidio.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.data.model.CctvPosition
import dev.andikune.masjidio.ui.components.TvSlider
import dev.andikune.masjidio.ui.components.TvToggle
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary

@Composable
fun CctvSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var urlInput by remember { mutableStateOf(settings.cctvUrl) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== HEADER =====
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "CCTV MASJID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Tampilkan live view kamera di sudut layar",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // ===== MASTER TOGGLE =====
        TvToggle(
            label = "Aktifkan Widget CCTV",
            description = "Widget CCTV (PiP) akan tampil di sudut HomeScreen",
            isChecked = settings.cctvEnabled,
            onToggle = { onUpdate(settings.copy(cctvEnabled = it)) }
        )

        // ===== URL CCTV =====
        if (settings.cctvEnabled) {
            Text(
                text = "URL CCTV",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Text(
                text = "Masukkan URL sesuai tipe DVR/Camera Anda:",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            CctvUrlInput(
                value = urlInput,
                placeholder = "rtsp://user:pass@192.168.1.100:554/stream",
                onValueChange = {
                    urlInput = it
                    onUpdate(settings.copy(cctvUrl = it))
                }
            )

            // ===== CONTOH URL =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                    .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "📋 Contoh URL CCTV",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CctvExampleRow("Hikvision", "rtsp://admin:pass@ip:554/Streaming/Channels/101")
                    CctvExampleRow("Dahua", "rtsp://admin:pass@ip:554/cam/realmonitor?channel=1&subtype=0")
                    CctvExampleRow("Tapo TP-Link", "rtsp://user:pass@ip:554/stream1")
                    CctvExampleRow("Xiaomi Yi", "rtsp://ip:554/ch0_0.h264")
                    CctvExampleRow("Snapshot JPG", "http://user:pass@ip/cgi-bin/snapshot.cgi")
                    CctvExampleRow("MJPEG Stream", "http://ip:8080/video")
                }
            }

            // ===== POSISI WIDGET =====
            Text(
                text = "POSISI WIDGET",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PositionButton(
                    label = "Kiri Atas",
                    isSelected = settings.cctvPosition == CctvPosition.TOP_LEFT,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdate(settings.copy(cctvPosition = CctvPosition.TOP_LEFT)) }
                )
                PositionButton(
                    label = "Kanan Atas",
                    isSelected = settings.cctvPosition == CctvPosition.TOP_RIGHT,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdate(settings.copy(cctvPosition = CctvPosition.TOP_RIGHT)) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PositionButton(
                    label = "Kiri Bawah",
                    isSelected = settings.cctvPosition == CctvPosition.BOTTOM_LEFT,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdate(settings.copy(cctvPosition = CctvPosition.BOTTOM_LEFT)) }
                )
                PositionButton(
                    label = "Kanan Bawah",
                    isSelected = settings.cctvPosition == CctvPosition.BOTTOM_RIGHT,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdate(settings.copy(cctvPosition = CctvPosition.BOTTOM_RIGHT)) }
                )
            }

            // ===== UKURAN WIDGET =====
            TvSlider(
                label = "Ukuran Widget",
                value = settings.cctvSizePercent.toFloat(),
                onValueChange = { onUpdate(settings.copy(cctvSizePercent = it.toInt())) },
                valueRange = 10f..40f,
                steps = 29,
                formatter = { "${it.toInt()}% dari layar" }
            )
        }

        // ===== INFO =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Info",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• RTSP: Butuh URL dari DVR (contoh Hikvision/Dahua)\n" +
                            "• HTTP Snapshot: Kamera auto-refresh gambar\n" +
                            "• MJPEG: Stream video HTTP\n" +
                            "• Pastikan TV & DVR dalam 1 jaringan WiFi/LAN\n" +
                            "• Cek menu Network di DVR untuk URL yang benar",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: INPUT URL CCTV
// ============================================================

@Composable
private fun CctvUrlInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x22000000))
            .border(
                width = if (isFocused) 4.dp else 1.dp,
                color = if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                color = TextSecondary.copy(alpha = 0.5f)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 13.sp,
                color = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
        )
    }
}

// ============================================================
// KOMPONEN: CONTOH URL
// ============================================================

@Composable
private fun CctvExampleRow(
    brand: String,
    example: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "• $brand",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGold
        )
        Text(
            text = example,
            fontSize = 10.sp,
            color = TextSecondary,
            lineHeight = 14.sp
        )
    }
}

// ============================================================
// KOMPONEN: POSITION BUTTON
// ============================================================

@Composable
private fun PositionButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> Color(0x44FFD700)
                    isFocused -> Color(0x33FFD700)
                    else -> Color(0x22000000)
                }
            )
            .border(
                width = if (isFocused) 4.dp else if (isSelected) 2.dp else 1.dp,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isSelected -> IslamicGold
                    else -> Color(0x33FFFFFF)
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) IslamicGoldLight else TextPrimary
        )
    }
}
