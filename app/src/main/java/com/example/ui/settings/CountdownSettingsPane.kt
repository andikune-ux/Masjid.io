package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.math.roundToInt

@Composable
fun CountdownSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Durasi & Hitungan Mundur (Slider 0 - 30 Menit)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Atur seluruh durasi jeda hitungan mundur waktu sholat, fase iqamah, dan tampilan fokus.",
            fontSize = 13.sp,
            color = TextSecondary
        )

        // 1. Jeda Iqomah (0 - 30 Menit)
        DurationSliderCard(
            title = "Jeda Waktu Iqamah",
            description = "Durasi hitungan mundur dari kumandang adzan hingga iqamah ditegakkan.",
            valueMinutes = settings.iqamahWaitMinutes,
            icon = Icons.Default.HourglassBottom,
            valueRange = 1f..30f,
            steps = 29,
            onValueChange = { onUpdate(settings.copy(iqamahWaitMinutes = it)) }
        )

        // 2. Jeda Sholat Sunnah Qobliyah (0 - 30 Menit)
        DurationSliderCard(
            title = "Jeda Sholat Sunnah Qobliyah",
            description = "Waktu jamaah menunaikan sholat sunnah sebelum iqamah / merapatkan shaf.",
            valueMinutes = settings.qobliyahWaitMinutes,
            icon = Icons.Default.AvTimer,
            valueRange = 0f..30f,
            steps = 30,
            onValueChange = { onUpdate(settings.copy(qobliyahWaitMinutes = it)) }
        )

        // 3. Durasi Adzan Berlangsung (0 - 30 Menit)
        DurationSliderCard(
            title = "Durasi Peringatan Adzan Masuk",
            description = "Lama tampilan status adzan berkumandang di layar utama.",
            valueMinutes = settings.adzanWaitMinutes,
            icon = Icons.Default.Timer,
            valueRange = 1f..30f,
            steps = 29,
            onValueChange = { onUpdate(settings.copy(adzanWaitMinutes = it)) }
        )

        // 4. Durasi Total Mode Fokus Sholat (1 - 60 Menit)
        DurationSliderCard(
            title = "Durasi Mode Fokus Sholat Berjamaah",
            description = "Layar hening khusyuk & instruksi rapatkan shaf selama sholat berlangsung (otomatis kembali ke beranda).",
            valueMinutes = settings.prayerFocusDurationMinutes,
            icon = Icons.Default.HourglassBottom,
            valueRange = 5f..60f,
            steps = 55,
            onValueChange = { onUpdate(settings.copy(prayerFocusDurationMinutes = it)) }
        )

        // 5. Interval Tampilan Donasi QRIS (0 - 30 Menit)
        DurationSliderCard(
            title = "Interval Periode Tampil QRIS Donasi",
            description = "Seberapa sering QRIS infaq masjid muncul ke layar fokus (0 = nonaktifkan tayangan otomatis).",
            valueMinutes = settings.qrisIntervalMinutes,
            icon = Icons.Default.QrCode2,
            valueRange = 0f..30f,
            steps = 30,
            unitLabel = if (settings.qrisIntervalMinutes == 0) "Nonaktif" else "Menit",
            onValueChange = { onUpdate(settings.copy(qrisIntervalMinutes = it)) }
        )

        // 6. Durasi Tampil QRIS Setiap Muncul (5 - 60 Detik)
        DurationSliderCard(
            title = "Lama QRIS Tampil Setiap Sesi",
            description = "Berapa detik QRIS donasi tertampil di layar sebelum otomatis kembali.",
            valueMinutes = settings.qrisDisplayDurationSeconds,
            icon = Icons.Default.Timer,
            valueRange = 5f..60f,
            steps = 55,
            unitLabel = "Detik",
            onValueChange = { onUpdate(settings.copy(qrisDisplayDurationSeconds = it)) }
        )
    }
}

@Composable
fun DurationSliderCard(
    title: String,
    description: String,
    valueMinutes: Int,
    icon: ImageVector,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    unitLabel: String = "Menit",
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF091620))
            .border(1.dp, Color(0x22FFD700), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IslamicGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Value Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(IslamicGreen.copy(alpha = 0.2f))
                    .border(1.dp, IslamicGreen, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (unitLabel == "Nonaktif") "Nonaktif" else "$valueMinutes $unitLabel",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Slider component
        Slider(
            value = valueMinutes.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = IslamicGold,
                activeTrackColor = IslamicGold,
                inactiveTrackColor = Color(0x33FFFFFF)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
