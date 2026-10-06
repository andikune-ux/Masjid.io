package dev.andikune.masjidio.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.components.TvSlider
import dev.andikune.masjidio.ui.components.TvToggle
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary

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
        // ===== HEADER =====
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

        // ===== MASTER TOGGLE =====
        TvToggle(
            label = "Aktifkan Mode Ramadhan",
            description = "Fitur khusus Ramadhan akan tampil otomatis",
            isChecked = settings.ramadhanModeEnabled,
            onToggle = { onUpdate(settings.copy(ramadhanModeEnabled = it)) }
        )

        // ===== COUNTDOWN & JADWAL =====
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
            label = "Tampilkan Imsak Besar",
            description = "Waktu imsak tampil besar di overlay",
            isChecked = settings.showImsakBesar,
            onToggle = { onUpdate(settings.copy(showImsakBesar = it)) },
            enabled = settings.ramadhanModeEnabled
        )

        TvToggle(
            label = "Tampilkan Iftar Besar",
            description = "Waktu iftar tampil besar di overlay",
            isChecked = settings.showIftarBesar,
            onToggle = { onUpdate(settings.copy(showIftarBesar = it)) },
            enabled = settings.ramadhanModeEnabled
        )

        TvSlider(
            label = "Jeda Imsak (menit)",
            value = settings.ramadhanImsakOffsetMinutes.toFloat(),
            onValueChange = { onUpdate(settings.copy(ramadhanImsakOffsetMinutes = it.toInt())) },
            valueRange = 0f..30f,
            steps = 29,
            unit = " menit sebelum Subuh"
        )

        // ===== JADWAL TARAWIH =====
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

        // ===== JADWAL KULTUM =====
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

        // ===== MENU SAHUR & IFTAR =====
        TvToggle(
            label = "Menu Sahur & Iftar",
            description = "Tampilkan menu sahur & iftar masjid",
            isChecked = settings.showMenuSahurIftar,
            onToggle = { onUpdate(settings.copy(showMenuSahurIftar = it)) },
            enabled = settings.ramadhanModeEnabled
        )

        if (settings.showMenuSahurIftar && settings.ramadhanModeEnabled) {
            SettingsInput(
                label = "Menu Sahur",
                value = settings.menuSahurText,
                placeholder = "Contoh: Nasi uduk, telur balado, air mineral",
                onValueChange = { onUpdate(settings.copy(menuSahurText = it)) }
            )
            SettingsInput(
                label = "Menu Iftar",
                value = settings.menuIftarText,
                placeholder = "Contoh: Kolak, kurma, nasi kebuli",
                onValueChange = { onUpdate(settings.copy(menuIftarText = it)) }
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
                Text(
                    text = "ℹ️ Catatan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mode Ramadhan akan otomatis muncul 1 jam sebelum Imsak " +
                            "dan 1 jam sebelum Maghrib (Iftar). " +
                            "Setelah Imsak/Iftar terlewati, overlay akan tertutup otomatis.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: INPUT TEKS
// ============================================================

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
                    width = if (isFocused) 4.dp else 1.dp,
                    color = if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
            )
        }
    }
}
