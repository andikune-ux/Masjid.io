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
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.ui.components.TvSlider
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

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
            text = "Pengaturan Durasi & Hitungan Mundur",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "Atur seluruh durasi jeda hitungan mundur waktu sholat, fase iqamah, dan tampilan fokus. " +
                    "Gunakan tombol KIRI/KANAN setelah tekan OK untuk menggeser.",
            fontSize = 13.sp,
            color = TextSecondary
        )

        // ============================================================
        // V1.04.421 BARU — ALUR SHOLAT BARU (Adzan → Doa → Iqomah → Mode Fokus)
        // ============================================================
        Text(
            text = "📢 ALUR SHOLAT (V1.04.421)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Urutan: Beep → Overlay Adzan → Himbauan HP → Niat Qobliyah → Mode Fokus (Niat Fardhu → Dzikir)",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )

        // A. Durasi Overlay Adzan
        DurationSliderCard(
            title = "Durasi Overlay Adzan",
            description = "Lama tampil layar 'ADZAN [waktu]' setelah masuk waktu sholat.",
            icon = Icons.Default.Timer,
            value = settings.adzanDisplayDurationSeconds.toFloat(),
            valueRange = 3f..30f,
            steps = 27,
            unitLabel = "Detik",
            onValueChange = { onUpdate(settings.copy(adzanDisplayDurationSeconds = it.toInt())) }
        )

        // B. Durasi Himbauan HP
        DurationSliderCard(
            title = "Durasi Himbauan HP",
            description = "Lama tampil layar 'HENINGKAN HP ANDA' untuk ketenangan ibadah.",
            icon = Icons.Default.PhoneAndroid,
            value = settings.silentPhoneDisplayDurationSeconds.toFloat(),
            valueRange = 3f..30f,
            steps = 27,
            unitLabel = "Detik",
            onValueChange = { onUpdate(settings.copy(silentPhoneDisplayDurationSeconds = it.toInt())) }
        )

        // C. Durasi Niat Qobliyah
        DurationSliderCard(
            title = "Durasi Niat Qobliyah + Countdown Iqomah",
            description = "Lama tampil niat sholat sunnah qobliyah (dengan hitungan iqomah).",
            icon = Icons.Default.AvTimer,
            value = settings.qobliyahNiatDisplayDurationSeconds.toFloat(),
            valueRange = 5f..60f,
            steps = 55,
            unitLabel = "Detik",
            onValueChange = { onUpdate(settings.copy(qobliyahNiatDisplayDurationSeconds = it.toInt())) }
        )

        // D. Durasi Niat Fardhu (di Mode Fokus)
        DurationSliderCard(
            title = "Durasi Niat Fardhu (Mode Fokus)",
            description = "Lama tampil niat sholat fardhu (arab + latin + arti) di Mode Fokus.",
            icon = Icons.Default.HourglassBottom,
            value = settings.fardhuNiatDisplayDurationSeconds.toFloat(),
            valueRange = 5f..60f,
            steps = 55,
            unitLabel = "Detik",
            onValueChange = { onUpdate(settings.copy(fardhuNiatDisplayDurationSeconds = it.toInt())) }
        )

        // E. Durasi Dzikir (Mode Fokus)
        DurationSliderCard(
            title = "Durasi Dzikir (Mode Fokus)",
            description = "Lama tampil dzikir setelah sholat sebelum keluar dari Mode Fokus.",
            icon = Icons.Default.VolumeOff,
            value = settings.dzikirDisplayDurationSeconds.toFloat(),
            valueRange = 30f..300f,
            steps = 53,
            unitLabel = "Detik",
            onValueChange = { onUpdate(settings.copy(dzikirDisplayDurationSeconds = it.toInt())) }
        )

        // ============================================================
        // PENGATURAN LAMA (tetap dipertahankan)
        // ============================================================
        Text(
            text = "⏱️ PENGATURAN UMUM",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 1. Jeda Iqomah
        DurationSliderCard(
            title = "Jeda Waktu Iqamah",
            description = "Durasi hitungan mundur dari adzan hingga iqamah ditegakkan.",
            icon = Icons.Default.HourglassBottom,
            value = settings.iqamahWaitMinutes.toFloat(),
            valueRange = 1f..30f,
            steps = 28,
            unitLabel = "Menit",
            onValueChange = { onUpdate(settings.copy(iqamahWaitMinutes = it.toInt())) }
        )

        // 2. Jeda Sholat Sunnah Qobliyah
        DurationSliderCard(
            title = "Jeda Sholat Sunnah Qobliyah",
            description = "Waktu jamaah menunaikan sholat sunnah sebelum iqamah / merapatkan shaf.",
            icon = Icons.Default.AvTimer,
            value = settings.qobliyahWaitMinutes.toFloat(),
            valueRange = 0f..30f,
            steps = 29,
            unitLabel = "Menit",
            onValueChange = { onUpdate(settings.copy(qobliyahWaitMinutes = it.toInt())) }
        )

        // 3. Durasi Adzan Berlangsung
        DurationSliderCard(
            title = "Durasi Peringatan Adzan Masuk",
            description = "Lama tampilan status adzan berkumandang di layar utama.",
            icon = Icons.Default.Timer,
            value = settings.adzanWaitMinutes.toFloat(),
            valueRange = 1f..30f,
            steps = 28,
            unitLabel = "Menit",
            onValueChange = { onUpdate(settings.copy(adzanWaitMinutes = it.toInt())) }
        )

        // 4. Durasi Total Mode Fokus Sholat
        DurationSliderCard(
            title = "Durasi Mode Fokus Sholat Berjamaah",
            description = "Layar hening khusyuk & instruksi rapatkan shaf selama sholat berlangsung.",
            icon = Icons.Default.HourglassBottom,
            value = settings.prayerFocusDurationMinutes.toFloat(),
            valueRange = 5f..60f,
            steps = 54,
            unitLabel = "Menit",
            onValueChange = { onUpdate(settings.copy(prayerFocusDurationMinutes = it.toInt())) }
        )

        // 5. Interval QRIS
        DurationSliderCard(
            title = "Interval Periode Tampil QRIS Donasi",
            description = "Seberapa sering QRIS infaq masjid muncul (0 = nonaktifkan).",
            icon = Icons.Default.QrCode2,
            value = settings.qrisIntervalMinutes.toFloat(),
            valueRange = 0f..30f,
            steps = 29,
            unitLabel = if (settings.qrisIntervalMinutes == 0) "Nonaktif" else "Menit",
            onValueChange = { onUpdate(settings.copy(qrisIntervalMinutes = it.toInt())) }
        )

        // 6. Lama QRIS Tampil
        DurationSliderCard(
            title = "Lama QRIS Tampil Setiap Sesi",
            description = "Berapa detik QRIS donasi tertampil sebelum otomatis kembali.",
            icon = Icons.Default.Timer,
            value = settings.qrisDisplayDurationSeconds.toFloat(),
            valueRange = 5f..60f,
            steps = 54,
            unitLabel = "Detik",
            onValueChange = { onUpdate(settings.copy(qrisDisplayDurationSeconds = it.toInt())) }
        )
    }
}
// ============================================================
// KOMPONEN: DURATION SLIDER CARD
// ============================================================

@Composable
fun DurationSliderCard(
    title: String,
    description: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    unitLabel: String = "Menit",
    onValueChange: (Float) -> Unit
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(IslamicGreen.copy(alpha = 0.2f))
                    .border(1.dp, IslamicGreen, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (unitLabel == "Nonaktif") "Nonaktif"
                           else "${value.toInt()} $unitLabel",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TvSlider(
            label = "Nilai",
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            formatter = { v ->
                if (unitLabel == "Nonaktif" && v.toInt() == 0) "Nonaktif"
                else "${v.toInt()} $unitLabel"
            }
        )
    }
}
