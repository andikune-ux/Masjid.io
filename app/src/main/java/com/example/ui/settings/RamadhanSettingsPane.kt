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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
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
    // State lokal — nanti akan disimpan ke AppSettings setelah field ditambahkan
    var isRamadhanActive by remember { mutableStateOf(false) }
    var showCountdownImsakIftar by remember { mutableStateOf(true) }
    var showTarawihSchedule by remember { mutableStateOf(true) }
    var showKultumSchedule by remember { mutableStateOf(true) }
    var showMenuSahurIftar by remember { mutableStateOf(false) }
    var tarawihTime by remember { mutableStateOf("19:30") }
    var tarawihImam by remember { mutableStateOf("") }
    var kultumTitle by remember { mutableStateOf("") }
    var kultumUstadz by remember { mutableStateOf("") }
    var kultumTime by remember { mutableStateOf("17:30") }

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

        Spacer(modifier = Modifier.height(8.dp))

        // TOGGLE AKTIF
        ToggleRow(
            label = "Aktifkan Mode Ramadhan",
            description = "Aktifkan fitur khusus bulan Ramadhan",
            isChecked = isRamadhanActive,
            onToggle = { isRamadhanActive = it }
        )

        // SECTION COUNTDOWN
        Text(
            text = "COUNTDOWN & JADWAL",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        ToggleRow(
            label = "Countdown Imsak & Iftar",
            description = "Tampilkan hitungan mundur imsak & berbuka",
            isChecked = showCountdownImsakIftar,
            onToggle = { showCountdownImsakIftar = it },
            enabled = isRamadhanActive
        )

        ToggleRow(
            label = "Jadwal Tarawih",
            description = "Tampilkan jadwal sholat tarawih",
            isChecked = showTarawihSchedule,
            onToggle = { showTarawihSchedule = it },
            enabled = isRamadhanActive
        )

        if (showTarawihSchedule && isRamadhanActive) {
            SettingsInput(
                label = "Jam Tarawih",
                value = tarawihTime,
                placeholder = "19:30",
                onValueChange = { tarawihTime = it }
            )
            SettingsInput(
                label = "Imam Tarawih",
                value = tarawihImam,
                placeholder = "Nama imam tarawih",
                onValueChange = { tarawihImam = it }
            )
        }

        ToggleRow(
            label = "Jadwal Kultum",
            description = "Tampilkan jadwal kultum Ramadhan",
            isChecked = showKultumSchedule,
            onToggle = { showKultumSchedule = it },
            enabled = isRamadhanActive
        )

        if (showKultumSchedule && isRamadhanActive) {
            SettingsInput(
                label = "Judul Kultum",
                value = kultumTitle,
                placeholder = "Judul ceramah",
                onValueChange = { kultumTitle = it }
            )
            SettingsInput(
                label = "Ustadz Kultum",
                value = kultumUstadz,
                placeholder = "Nama ustadz",
                onValueChange = { kultumUstadz = it }
            )
            SettingsInput(
                label = "Jam Kultum",
                value = kultumTime,
                placeholder = "17:30",
                onValueChange = { kultumTime = it }
            )
        }

        ToggleRow(
            label = "Menu Sahur & Iftar",
            description = "Tampilkan menu sahur & iftar masjid",
            isChecked = showMenuSahurIftar,
            onToggle = { showMenuSahurIftar = it },
            enabled = isRamadhanActive
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
                    text = "Pengaturan ini masih bersifat sementara. Fitur akan tersimpan permanen setelah field Ramadhan ditambahkan di pengaturan.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isFocused && enabled) Color(0x33FFD700) else Color(0x22000000)
            )
            .border(
                if (isFocused && enabled) 3.dp else 1.5.dp,
                if (isFocused && enabled) IslamicGoldLight else IslamicGold.copy(alpha = if (enabled) 0.5f else 0.2f),
                RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled)
            .clickable(enabled = enabled) { onToggle(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) IslamicGoldLight else TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isChecked) IslamicGreen else Color(0x55FFFFFF))
                .padding(3.dp),
            contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color.White)
            )
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
                    if (isFocused) 2.dp else 1.dp,
                    if (isFocused) IslamicGold else Color(0x44FFFFFF),
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
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = androidx.compose.ui.text.TextStyle(
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
