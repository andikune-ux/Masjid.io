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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
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
import com.example.data.model.AppSettings
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RamadhanSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.NightsStay,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "MODE RAMADHAN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Atur fitur khusus bulan Ramadhan",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // TOGGLE AKTIF
        TvToggle(
            label = "Aktifkan Mode Ramadhan",
            description = "Aktifkan fitur khusus bulan Ramadhan",
            isChecked = settings.ramadhanModeEnabled,
            onToggle = { onUpdate(settings.copy(ramadhanModeEnabled = it)) }
        )

        // SECTION COUNTDOWN
        Text(
            text = "COUNTDOWN & JADWAL",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Countdown Imsak & Iftar",
            description = "Tampilkan hitungan mundur imsak & berbuka",
            isChecked = settings.showImsakIftarCountdown,
            onToggle = { onUpdate(settings.copy(showImsakIftarCountdown = it)) },
            enabled = settings.ramadhanModeEnabled
        )

        TvToggle(
            label = "Jadwal Tarawih",
            description = "Tampilkan jadwal sholat tarawih",
            isChecked = settings.showTarawihSchedule,
            onToggle = { onUpdate(settings.copy(showTarawihSchedule = it)) },
            enabled = settings.ramadhanModeEnabled
        )

        if (settings.showTarawihSchedule && settings.ramadhanModeEnabled) {
            SettingsInput(
                label = "Jam Tarawih",
                value = settings.tarawihTime,
                placeholder = "19:30",
                onValueChange = { onUpdate(settings.copy(tarawihTime = it)) }
            )
            SettingsInput(
                label = "Imam Tarawih",
                value = settings.tarawihImam,
                placeholder = "Nama imam tarawih",
                onValueChange = { onUpdate(settings.copy(tarawihImam = it)) }
            )
        }

        TvToggle(
            label = "Jadwal Kultum",
            description = "Tampilkan jadwal kultum Ramadhan",
            isChecked = settings.showKultumSchedule,
            onToggle = { onUpdate(settings.copy(showKultumSchedule = it)) },
            enabled = settings.ramadhanModeEnabled
        )

        if (settings.showKultumSchedule && settings.ramadhanModeEnabled) {
            SettingsInput(
                label = "Judul Kultum",
                value = settings.kultumTitle,
                placeholder = "Judul ceramah",
                onValueChange = { onUpdate(settings.copy(kultumTitle = it)) }
            )
            SettingsInput(
                label = "Ustadz Kultum",
                value = settings.kultumUstadz,
                placeholder = "Nama ustadz",
                onValueChange = { onUpdate(settings.copy(kultumUstadz = it)) }
            )
            SettingsInput(
                label = "Jam Kultum",
                value = settings.kultumTime,
                placeholder = "17:30",
                onValueChange = { onUpdate(settings.copy(kultumTime = it)) }
            )
        }

        TvToggle(
            label = "Menu Sahur & Iftar",
            description = "Tampilkan menu sahur & iftar masjid",
            isChecked = settings.showMenuSahurIftar,
            onToggle = { onUpdate(settings.copy(showMenuSahurIftar = it)) },
            enabled = settings.ramadhanModeEnabled
        )

        // INFO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "ℹ️ Catatan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pengaturan di sini akan otomatis tersimpan setelah Anda tekan SIMPAN PENGATURAN di atas.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsInput(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x22000000))
                .border(
                    if (isFocused) 3.dp else 1.dp,
                    if (isFocused) IslamicGoldLight else Color(0x44FFFFFF),
                    RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
            )
        }
    }
}
