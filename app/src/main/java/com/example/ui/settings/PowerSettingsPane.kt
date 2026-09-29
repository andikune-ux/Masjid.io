package com.example.ui.settings

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.components.TvSlider
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PowerSettingsPane(
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

        Spacer(modifier = Modifier.height(4.dp))

        // PENGATURAN LAYAR
        Text(
            text = "PENGATURAN LAYAR",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Layar Selalu Menyala",
            description = "Cegah TV masuk mode sleep saat aplikasi aktif",
            isChecked = settings.keepScreenOn,
            onToggle = { onUpdate(settings.copy(keepScreenOn = it)) }
        )

        TvToggle(
            label = "Auto Brightness",
            description = "Sesuaikan kecerahan otomatis sesuai waktu sholat",
            isChecked = settings.autoBrightness,
            onToggle = { onUpdate(settings.copy(autoBrightness = it)) }
        )

        TvToggle(
            label = "Mode Hemat Daya",
            description = "Kurangi animasi saat malam (Isya-Subuh)",
            isChecked = settings.saveBatteryMode,
            onToggle = { onUpdate(settings.copy(saveBatteryMode = it)) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // IDLE SCREEN
        Text(
            text = "IDLE SCREEN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Matikan Layar saat Idle",
            description = "Layar redup otomatis kalau tidak ada aktivitas",
            isChecked = settings.idleScreenOff,
            onToggle = { onUpdate(settings.copy(idleScreenOff = it)) }
        )

        if (settings.idleScreenOff) {
            TvSlider(
                label = "Timeout Idle",
                value = settings.idleTimeoutMinutes.toFloat(),
                onValueChange = {
                    onUpdate(settings.copy(idleTimeoutMinutes = it.toInt()))
                },
                valueRange = 5f..120f,
                steps = 22,
                unit = " menit"
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // JADWAL ON/OFF TV
        Text(
            text = "JADWAL ON / OFF TV",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Aktifkan Jadwal On/Off",
            description = "TV otomatis on & off sesuai jadwal",
            isChecked = settings.autoOnOff,
            onToggle = { onUpdate(settings.copy(autoOnOff = it)) }
        )

        if (settings.autoOnOff) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                    .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "⏰ Jam Auto ON",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = settings.autoOnTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⏰ Jam Auto OFF",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = settings.autoOffTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                    .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "⚠️ Catatan: Fitur Auto ON/OFF memerlukan dukungan TV. Beberapa TV mungkin tidak support fitur ini. Pengaturan jam dapat disesuaikan di update berikutnya.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // BOOTING INFO
        Text(
            text = "BOOTING",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        InfoBox(
            icon = Icons.Default.Timer,
            title = "Auto-Start saat Boot",
            value = if (settings.autoStartOnBoot) "AKTIF" else "NONAKTIF",
            description = "Aplikasi otomatis terbuka saat TV dinyalakan (atur di menu Keamanan)"
        )

        InfoBox(
            icon = Icons.Default.BatteryFull,
            title = "Wake Lock",
            value = if (settings.keepScreenOn) "AKTIF" else "NONAKTIF",
            description = "Cegah TV sleep saat aplikasi berjalan"
        )

        InfoBox(
            icon = Icons.Default.Brightness6,
            title = "Kecerahan",
            value = if (settings.autoBrightness) "AUTO" else "MANUAL",
            description = "Kecerahan menyesuaikan waktu sholat"
        )
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
            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(
                description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
