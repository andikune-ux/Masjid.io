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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightsStay
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
import com.example.data.local.PrayerTimesCalculator
import com.example.data.model.AppSettings
import com.example.ui.components.TvSlider
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun PowerSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    // Hitung jadwal ON/OFF dari jadwal sholat hari ini
    val schedule = PrayerTimesCalculator.calculate(
        date = LocalDate.now(),
        latitude = settings.latitude,
        longitude = settings.longitude
    )

    val autoOnTime = calculateAutoOnTime(
        subuhTime = schedule.subuh,
        minutesBefore = settings.autoOnMinutesBeforeSubuh
    )
    val autoOffTime = calculateAutoOffTime(
        isyaTime = schedule.isya,
        minutesAfter = settings.autoOffMinutesAfterIsya
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ============================================================
        // HEADER
        // ============================================================
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

        // ============================================================
        // PENGATURAN LAYAR
        // ============================================================
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

        // ============================================================
        // JADWAL ON/OFF OTOMATIS — V1.04.423
        // ============================================================
        Text(
            text = "JADWAL ON / OFF LAYAR OTOMATIS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Layar otomatis nyala & redup mengikuti jadwal sholat. " +
                    "Tidak perlu atur jam manual.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )

        TvToggle(
            label = "Aktifkan Jadwal On/Off Otomatis",
            description = "Layar redup total (brightness 0) setelah Isya, dan nyala lagi sebelum Subuh",
            isChecked = settings.autoOnOff,
            onToggle = { onUpdate(settings.copy(autoOnOff = it)) }
        )

        if (settings.autoOnOff) {
            // ============================================================
            // KARTU JADWAL HARI INI (read-only info)
            // ============================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22FFD700), RoundedCornerShape(12.dp))
                    .border(1.5.dp, IslamicGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "JADWAL HARI INI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // ON
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = IslamicGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🌅 NYALA (ON)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGreen
                                )
                                Text(
                                    text = "Subuh (${schedule.subuh}) - ${settings.autoOnMinutesBeforeSubuh} menit",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Text(
                            text = autoOnTime,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGreen
                        )
                    }

                    // OFF
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = null,
                                tint = UrgentRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🌙 REDUP (OFF)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UrgentRed
                                )
                                Text(
                                    text = "Isya (${schedule.isya}) + ${settings.autoOffMinutesAfterIsya} menit",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Text(
                            text = autoOffTime,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UrgentRed
                        )
                    }
                }
            }

            // ============================================================
            // SLIDER: Berapa menit setelah Isya (untuk OFF)
            // ============================================================
            TvSlider(
                label = "Redup Setelah Isya",
                value = settings.autoOffMinutesAfterIsya.toFloat(),
                onValueChange = {
                    onUpdate(settings.copy(autoOffMinutesAfterIsya = it.toInt()))
                },
                valueRange = 5f..120f,
                steps = 22,
                formatter = { "${it.toInt()} menit setelah Isya" }
            )

            // ============================================================
            // SLIDER: Berapa menit sebelum Subuh (untuk ON)
            // ============================================================
            TvSlider(
                label = "Nyala Sebelum Subuh",
                value = settings.autoOnMinutesBeforeSubuh.toFloat(),
                onValueChange = {
                    onUpdate(settings.copy(autoOnMinutesBeforeSubuh = it.toInt()))
                },
                valueRange = 5f..120f,
                steps = 22,
                formatter = { "${it.toInt()} menit sebelum Subuh" }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ============================================================
            // POPUP KONFIRMASI
            // ============================================================
            Text(
                text = "POPUP KONFIRMASI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            TvToggle(
                label = "Aktifkan Popup Konfirmasi",
                description = "Saat user tekan remote di jam OFF, muncul popup YA/TIDAK untuk mematikan jadwal",
                isChecked = settings.autoOffDialogEnabled,
                onToggle = { onUpdate(settings.copy(autoOffDialogEnabled = it)) }
            )

            if (settings.autoOffDialogEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33A5D6A7), RoundedCornerShape(10.dp))
                        .border(1.dp, IslamicGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "💡 Cara Kerja Popup",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGreen
                        )
                        Text(
                            text = "• User tekan remote saat layar redup → Popup muncul\n" +
                                    "• Pilih YA → Jadwal OFF dimatikan, layar nyala terus\n" +
                                    "• Pilih TIDAK / tidak pilih → Popup hilang, layar redup lagi\n" +
                                    "• Auto-dismiss 2 menit → dianggap TIDAK",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ============================================================
            // INFO TAMBAHAN
            // ============================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33FF5252), RoundedCornerShape(10.dp))
                    .border(1.dp, UrgentRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "⚠️ Catatan: Saat brightness 0, layar benar-benar hitam. " +
                            "TV tetap menyala (tidak sleep). Hemat daya ~10-30%. " +
                            "Untuk hemat listrik maksimal, gunakan Timer bawaan TV.",
                    fontSize = 11.sp,
                    color = Color(0xFFFF8A80),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ============================================================
        // IDLE SCREEN
        // ============================================================
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

        // ============================================================
        // INFO SISTEM
        // ============================================================
        Text(
            text = "INFORMASI SISTEM",
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
            value = when {
                settings.autoOnOff -> "JADWAL OTOMATIS AKTIF"
                settings.autoBrightness -> "AUTO"
                else -> "MANUAL"
            },
            description = "Kecerahan menyesuaikan jadwal sholat & waktu otomatis"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================================
// HITUNG JAM AUTO ON
// ON = Subuh - X menit
// ============================================================
private fun calculateAutoOnTime(subuhTime: String, minutesBefore: Int): String {
    return try {
        val parts = subuhTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 4
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 20

        val totalMinutes = hour * 60 + minute - minutesBefore
        val newHour = ((totalMinutes % (24 * 60)) + (24 * 60)) % (24 * 60) / 60
        val newMinute = ((totalMinutes % 60) + 60) % 60

        String.format("%02d:%02d", newHour, newMinute)
    } catch (e: Exception) {
        "04:00"
    }
}

// ============================================================
// HITUNG JAM AUTO OFF
// OFF = Isya + X menit
// ============================================================
private fun calculateAutoOffTime(isyaTime: String, minutesAfter: Int): String {
    return try {
        val parts = isyaTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 19
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val totalMinutes = hour * 60 + minute + minutesAfter
        val newHour = (totalMinutes / 60) % 24
        val newMinute = totalMinutes % 60

        String.format("%02d:%02d", newHour, newMinute)
    } catch (e: Exception) {
        "22:30"
    }
}

// ============================================================
// INFO BOX
// ============================================================
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
