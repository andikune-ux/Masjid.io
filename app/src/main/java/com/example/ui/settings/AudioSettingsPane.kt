package com.example.ui.settings

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.AppSettings
import com.example.data.model.AudioMode
import com.example.ui.components.TvSlider
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AudioSettingsPane(
    settings: AppSettings,
    soundManager: SoundManager,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isTesting by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "AUDIO & ADZAN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Atur suara beep saat waktu sholat tiba",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // MODE AUDIO
        Text(
            text = "MODE AUDIO",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        AudioModeOption(
            label = "Beep Only",
            description = "Hanya bunyi 'bip bip bip' — untuk masjid yang sudah punya muadzin",
            isSelected = settings.audioMode == AudioMode.BEEP_ONLY,
            onClick = { onUpdate(settings.copy(audioMode = AudioMode.BEEP_ONLY)) }
        )

        AudioModeOption(
            label = "Full Adzan",
            description = "Memutar audio adzan lengkap dari TV",
            isSelected = settings.audioMode == AudioMode.FULL_ADZAN,
            onClick = { onUpdate(settings.copy(audioMode = AudioMode.FULL_ADZAN)) }
        )

        AudioModeOption(
            label = "Silent",
            description = "Tanpa suara, hanya tampilan visual",
            isSelected = settings.audioMode == AudioMode.SILENT,
            onClick = { onUpdate(settings.copy(audioMode = AudioMode.SILENT)) }
        )

        Spacer(Modifier.height(8.dp))

        // PENGATURAN BEEP
        Text(
            text = "PENGATURAN SUARA BEEP",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvSlider(
            label = "Volume Beep",
            value = settings.beepVolume.toFloat(),
            onValueChange = { onUpdate(settings.copy(beepVolume = it.toInt())) },
            valueRange = 0f..100f,
            steps = 19,
            formatter = { "${it.toInt()}%" }
        )

        TvSlider(
            label = "Jumlah Beep",
            value = settings.beepCount.toFloat(),
            onValueChange = { onUpdate(settings.copy(beepCount = it.toInt())) },
            valueRange = 1f..10f,
            steps = 8,
            formatter = { "${it.toInt()}x" }
        )

        TvSlider(
            label = "Durasi Tiap Beep",
            value = settings.beepDurationMs.toFloat(),
            onValueChange = { onUpdate(settings.copy(beepDurationMs = it.toInt())) },
            valueRange = 500f..3000f,
            steps = 24,
            formatter = { String.format("%.1f detik", it / 1000f) }
        )

        TvSlider(
            label = "Jeda Antar Beep",
            value = settings.beepIntervalMs.toFloat(),
            onValueChange = { onUpdate(settings.copy(beepIntervalMs = it.toInt())) },
            valueRange = 500f..5000f,
            steps = 44,
            formatter = { String.format("%.1f detik", it / 1000f) }
        )

        Spacer(Modifier.height(4.dp))

        // TOMBOL TEST SUARA
        TestButton(
            isTesting = isTesting,
            onClick = {
                if (isTesting) return@TestButton
                isTesting = true
                scope.launch {
                    soundManager.testBeep(
                        count = settings.beepCount,
                        volumePercent = settings.beepVolume,
                        durationMs = settings.beepDurationMs,
                        intervalMs = settings.beepIntervalMs
                    )
                    val totalMs = settings.beepCount.toLong() *
                            (settings.beepDurationMs + settings.beepIntervalMs).toLong()
                    delay(totalMs + 500)
                    isTesting = false
                }
            }
        )

        Spacer(Modifier.height(4.dp))

        // INFO BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "ℹ️ Info",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Suara beep akan berbunyi saat masuk waktu sholat. " +
                            "Gunakan mode Beep Only jika masjid sudah punya muadzin " +
                            "yang adzan langsung.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================

@Composable
private fun AudioModeOption(
    label: String,
    description: String,
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
                width = if (isFocused || isSelected) 2.dp else 1.dp,
                color = when {
                    isSelected -> IslamicGreen
                    isFocused -> IslamicGoldLight
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
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) IslamicGreen else IslamicGoldLight
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TestButton(
    isTesting: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isTesting) IslamicGreen.copy(alpha = 0.5f) else IslamicGold)
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = if (isFocused) IslamicGoldLight else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable(enabled = !isTesting) { onClick() }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color(0xFF09141D),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isTesting) "MEMUTAR..." else "TEST SUARA BEEP",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF09141D)
        )
    }
}
