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
import androidx.compose.material.icons.filled.Battery
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.graphics.vector.ImageVector
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
fun PowerSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    // State lokal untuk yang belum ada di AppSettings
    var autoOnOff by remember { mutableStateOf(false) }
    var autoOnTime by remember { mutableStateOf("04:00") }
    var autoOffTime by remember { mutableStateOf("22:30") }
    var idleScreenOff by remember { mutableStateOf(true) }
    var idleTimeoutMinutes by remember { mutableStateOf("30") }
    var autoBrightness by remember { mutableStateOf(true) }
    var saveBatteryMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Power,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "DAYA & BOOTING",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Atur daya, booting, dan layar TV",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // LAYAR
        Text(
            text = "PENGATURAN LAYAR",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        ToggleRow(
            label = "Layar Selalu Menyala",
            description = "Cegah TV masuk mode sleep saat aplikasi aktif",
            isChecked = settings.keepScreenOn,
            onToggle = { onUpdate(settings.copy(keepScreenOn = it)) }
        )

        ToggleRow(
            label = "Auto Brightness",
            description = "Sesuaikan kecerahan otomatis sesuai waktu sholat",
            isChecked = autoBrightness,
            onToggle = { autoBrightness = it }
        )

        ToggleRow(
            label = "Mode Hemat Daya",
            description = "Kurangi animasi saat malam (Isya-Subuh)",
            isChecked = saveBatteryMode,
            onToggle = { saveBatteryMode = it }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // IDLE SCREEN
        Text(
            text = "IDLE SCREEN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        ToggleRow(
            label = "Matikan Layar saat Idle",
            description = "Layar redup otomatis kalau tidak ada aktivitas",
            isChecked = idleScreenOff,
            onToggle = { idleScreenOff = it }
        )

        if (idleScreenOff) {
            SettingsInput(
                label = "Timeout Idle (menit)",
                value = idleTimeoutMinutes,
                placeholder = "30",
                onValueChange = { idleTimeoutMinutes = it }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // AUTO ON/OFF
        Text(
            text = "JADWAL ON / OFF TV",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        ToggleRow(
            label = "Aktifkan Jadwal On/Off",
            description = "TV otomatis on & off sesuai jadwal",
            isChecked = autoOnOff,
            onToggle = { autoOnOff = it }
        )

        if (autoOnOff) {
            SettingsInput(
                label = "Jam Auto ON",
                value = autoOnTime,
                placeholder = "04:00",
                onValueChange = { autoOnTime = it }
            )
            SettingsInput(
                label = "Jam Auto OFF",
                value = autoOffTime,
                placeholder = "22:30",
                onValueChange = { autoOffTime = it }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                    .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "⚠️ Catatan: Fitur Auto ON/OFF memerlukan dukungan TV. Beberapa TV mungkin tidak support fitur ini.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // INFO BOOT
        Text(
            text = "BOOTING",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        InfoBox(
            icon = Icons.Default.Timer,
            title = "Auto-Start saat Boot",
            value = "Aktif",
            description = "Aplikasi otomatis terbuka saat TV dinyalakan (atur di menu Keamanan)"
        )

        InfoBox(
            icon = Icons.Default.Battery,
            title = "Wake Lock",
            value = if (settings.keepScreenOn) "AKTIF" else "NONAKTIF",
            description = "Cegah TV sleep saat aplikasi berjalan"
        )

        InfoBox(
            icon = Icons.Default.Brightness6,
            title = "Kecerahan Saat Ini",
            value = "Auto",
            description = "Kecerahan menyesuaikan waktu sholat"
        )
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

@Composable
private fun InfoBox(
    icon: ImageVector,
    title: String,
    value: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33000000), RoundedCornerShape(10.dp))
            .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = IslamicGold,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, color = TextSecondary)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
            Text(description, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}
