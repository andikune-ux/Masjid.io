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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.components.AspectType
import dev.andikune.masjidio.ui.components.TvSlider
import dev.andikune.masjidio.ui.components.TvToggle
import dev.andikune.masjidio.ui.components.rememberScreenInfo
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary

/**
 * Panel pengaturan tampilan TV responsif.
 *
 * Menampilkan:
 *   - Info resolusi TV saat ini (width x height, aspect ratio)
 *   - Toggle auto-scale
 *   - Slider safe area (0-10%)
 *   - 4 preset cepat: AUTO / STANDAR / ULTRAWIDE / 4:3
 *   - Tombol "Test Safe Area" (tampilkan overlay kotak merah)
 */
@Composable
fun TvDisplaySettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    // Info layar real-time
    val screenInfo = rememberScreenInfo(
        autoScaleEnabled = settings.tvAutoScaleEnabled,
        safeAreaPercent = settings.tvSafeAreaPercent
    )

    // Overlay test safe area
    var showSafeAreaTest by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ============================================================
        // HEADER
        // ============================================================
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "TAMPILAN TV OTOMATIS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Auto-scale untuk semua ukuran & aspek rasio TV",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // ============================================================
        // INFO RESOLUSI TV
        // ============================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INFORMASI LAYAR TV ANDA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }

                InfoRow(
                    label = "Resolusi",
                    value = "${screenInfo.widthPx} × ${screenInfo.heightPx} px"
                )
                InfoRow(
                    label = "Ukuran (dp)",
                    value = "${screenInfo.widthDp.toInt()} × ${screenInfo.heightDp.toInt()} dp"
                )
                InfoRow(
                    label = "Aspek Rasio",
                    value = String.format("%.2f:1", screenInfo.aspectRatio)
                )
                InfoRow(
                    label = "Tipe Layar",
                    value = when (screenInfo.aspectType) {
                        AspectType.ULTRAWIDE -> "Ultrawide (21:9 ke atas)"
                        AspectType.STANDARD_WIDE -> "Standar Wide (16:9)"
                        AspectType.STANDARD -> "Standar (16:10)"
                        AspectType.CLASSIC_4_3 -> "Klasik (4:3)"
                        AspectType.UNKNOWN -> "Tidak diketahui"
                    },
                    valueColor = IslamicGreen
                )
                InfoRow(
                    label = "Scale Factor",
                    value = String.format("%.2f× (base FHD)", screenInfo.scaleFactor),
                    valueColor = IslamicGreen
                )
                InfoRow(
                    label = "Safe Padding",
                    value = "${screenInfo.safePaddingPx} px (${screenInfo.safePaddingDp.value.toInt()} dp)"
                )
            }
        }

        // ============================================================
        // AUTO SCALE TOGGLE
        // ============================================================
        Text(
            text = "PENGATURAN UTAMA",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Auto-Scale Otomatis",
            description = if (settings.tvAutoScaleEnabled)
                "Font & tombol menyesuaikan ukuran TV otomatis"
            else
                "Ukuran font & tombol fixed (tidak disarankan)",
            isChecked = settings.tvAutoScaleEnabled,
            onToggle = { onUpdate(settings.copy(tvAutoScaleEnabled = it)) }
        )

        // ============================================================
        // SAFE AREA SLIDER
        // ============================================================
        Text(
            text = "SAFE AREA (PADDING AMAN)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Padding di tepi layar untuk menghindari overscan bezel TV. " +
                    "Kalau ada tombol/teks terpotong di tepi, naikkan nilainya.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )

        TvSlider(
            label = "Safe Area",
            value = settings.tvSafeAreaPercent,
            onValueChange = {
                onUpdate(settings.copy(tvSafeAreaPercent = it.coerceIn(0f, 10f)))
            },
            valueRange = 0f..10f,
            steps = 19,
            formatter = { String.format("%.1f%%", it) }
        )

        // ============================================================
        // PRESET LAYOUT
        // ============================================================
        Text(
            text = "PRESET LAYOUT",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Pilih AUTO untuk deteksi otomatis, atau pilih manual kalau auto terasa kurang pas.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )

        val presets = listOf(
            "AUTO" to "Otomatis (Rekomendasi)",
            "STANDAR" to "Standar (16:9 / 4:3)",
            "ULTRAWIDE" to "Ultrawide (21:9)",
            "4:3" to "Klasik (4:3 lama)"
        )

        for ((key, label) in presets) {
            PresetItem(
                label = label,
                isSelected = settings.tvLayoutPreset == key,
                onClick = { onUpdate(settings.copy(tvLayoutPreset = key)) }
            )
        }

        // ============================================================
        // TOMBOL TEST SAFE AREA
        // ============================================================
        Text(
            text = "UJI TAMPILAN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TestSafeAreaButton(
            isActive = showSafeAreaTest,
            onClick = { showSafeAreaTest = !showSafeAreaTest }
        )

        if (showSafeAreaTest) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(IslamicGreen.copy(alpha = 0.15f))
                    .border(1.dp, IslamicGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "✅ Overlay test aktif. Buka HomeScreen (tombol back → Home) untuk " +
                            "melihat area yang AMAN dari potongan bezel TV.\n\n" +
                            "Area di dalam kotak hijau = tampil utuh.\n" +
                            "Area di luar kotak = berisiko terpotong bezel TV.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )
            }
        }

        // ============================================================
        // INFO BOX
        // ============================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "ℹ️ Cara Pakai",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Lihat info resolusi di atas — sudah otomatis terdeteksi\n" +
                            "2. Biarkan Auto-Scale ON (rekomendasi)\n" +
                            "3. Kalau ada elemen terpotong di tepi → naikkan Safe Area\n" +
                            "4. Kalau konten terasa aneh → coba ganti preset manual\n" +
                            "5. Tekan Test Safe Area untuk cek area aman",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ============================================================
// KOMPONEN: INFO ROW
// ============================================================
@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = IslamicGoldLight
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = FontFamily.Monospace
        )
    }
}

// ============================================================
// KOMPONEN: PRESET ITEM
// ============================================================
@Composable
private fun PresetItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> Color(0x33A5D6A7)
                    isFocused -> Color(0x33FFD700)
                    else -> Color(0x22000000)
                }
            )
            .border(
                width = when {
                    isFocused -> 4.dp
                    isSelected -> 2.dp
                    else -> 1.dp
                },
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isSelected -> IslamicGreen
                    else -> Color(0x33FFFFFF)
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (isSelected) IslamicGreen else Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) IslamicGreen else TextPrimary
        )
    }
}

// ============================================================
// KOMPONEN: TEST SAFE AREA BUTTON
// ============================================================
@Composable
private fun TestSafeAreaButton(
    isActive: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) IslamicGreen.copy(alpha = 0.3f)
                else Color(0xFF142735)
            )
            .border(
                width = if (isFocused) 4.dp else if (isActive) 2.dp else 1.dp,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isActive -> IslamicGreen
                    else -> IslamicGold.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Tv,
            contentDescription = null,
            tint = if (isActive) IslamicGreen else IslamicGold,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isActive) "TEST SAFE AREA: AKTIF" else "TEST SAFE AREA",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) IslamicGreen else IslamicGoldLight
            )
            Text(
                text = if (isActive)
                    "Tap untuk matikan overlay"
                else
                    "Tampilkan overlay kotak untuk cek tepi layar",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
