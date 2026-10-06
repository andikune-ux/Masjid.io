package dev.andikune.masjidio.ui.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import dev.andikune.masjidio.ui.theme.UrgentRed
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TimeSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCalendarModal by remember { mutableStateOf(false) }

    val effectiveNow = remember(settings.isManualTimeEnabled, settings.manualTimeOffsetSeconds) {
        if (settings.isManualTimeEnabled) {
            LocalDateTime.now().plusSeconds(settings.manualTimeOffsetSeconds)
        } else {
            LocalDateTime.now()
        }
    }

    var selectedDate by remember { mutableStateOf(effectiveNow.toLocalDate()) }
    var selectedHour by remember { mutableIntStateOf(effectiveNow.hour) }
    var selectedMinute by remember { mutableIntStateOf(effectiveNow.minute) }
    var selectedSecond by remember { mutableIntStateOf(effectiveNow.second) }

    // Pakai Locale.forLanguageTag (bukan Locale(String, String) yang deprecated)
    val localeId = remember { Locale.forLanguageTag("id-ID") }
    val fullDateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", localeId) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Waktu & Tanggal Manual (Khusus TV Offline)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // Master Switch
        TvToggle(
            label = "Aktifkan Mode Waktu Manual (Offline)",
            description = if (settings.isManualTimeEnabled)
                "Menggunakan waktu & tanggal yang diatur sendiri secara manual."
            else
                "Menggunakan waktu otomatis dari sistem TV / jaringan internet.",
            isChecked = settings.isManualTimeEnabled,
            onToggle = { isEnabled ->
                onUpdate(settings.copy(isManualTimeEnabled = isEnabled))
                val msg = if (isEnabled) "Mode waktu manual diaktifkan" else "Kembali ke waktu otomatis sistem"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        )

        // Section Tanggal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pengaturan Hari & Tanggal",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tanggal Saat Ini:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = selectedDate.format(fullDateFormatter),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = IslamicGoldLight
                    )
                }

                Button(
                    onClick = { showCalendarModal = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = Color(0xFF09141D)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PILIH DI KALENDER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section Jam
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pengaturan Jam, Menit & Detik",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            TvSlider(
                label = "Jam (0-23)",
                value = selectedHour.toFloat(),
                onValueChange = { selectedHour = it.toInt() },
                valueRange = 0f..23f,
                steps = 22,
                formatter = { String.format("%02d", it.toInt()) }
            )

            TvSlider(
                label = "Menit (0-59)",
                value = selectedMinute.toFloat(),
                onValueChange = { selectedMinute = it.toInt() },
                valueRange = 0f..59f,
                steps = 58,
                formatter = { String.format("%02d", it.toInt()) }
            )

            TvSlider(
                label = "Detik (0-59)",
                value = selectedSecond.toFloat(),
                onValueChange = { selectedSecond = it.toInt() },
                valueRange = 0f..59f,
                steps = 58,
                formatter = { String.format("%02d", it.toInt()) }
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val manualDateTime = LocalDateTime.of(
                        selectedDate,
                        LocalTime.of(selectedHour, selectedMinute, selectedSecond)
                    )
                    val sysEpoch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                    val manualEpoch = manualDateTime.toEpochSecond(ZoneOffset.UTC)
                    val offset = manualEpoch - sysEpoch

                    onUpdate(
                        settings.copy(
                            isManualTimeEnabled = true,
                            manualTimeOffsetSeconds = offset
                        )
                    )

                    Toast.makeText(
                        context,
                        "Waktu manual diterapkan: ${String.format("%02d:%02d:%02d", selectedHour, selectedMinute, selectedSecond)}",
                        Toast.LENGTH_LONG
                    ).show()
                },
                modifier = Modifier.weight(1.5f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IslamicGreen,
                    contentColor = Color(0xFF09141D)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TERAPKAN WAKTU MANUAL",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Pakai BorderStroke (bukan outlinedButtonBorder.copy yang deprecated)
            OutlinedButton(
                onClick = {
                    onUpdate(
                        settings.copy(
                            isManualTimeEnabled = false,
                            manualTimeOffsetSeconds = 0L
                        )
                    )
                    val now = LocalDateTime.now()
                    selectedDate = now.toLocalDate()
                    selectedHour = now.hour
                    selectedMinute = now.minute
                    selectedSecond = now.second
                    Toast.makeText(context, "Waktu direset ke sistem TV", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                border = BorderStroke(1.5.dp, UrgentRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "RESET", fontSize = 12.sp)
            }
        }
    }

    if (showCalendarModal) {
        MiniCalendarPickerModal(
            initialDate = selectedDate,
            onDateSelected = { newDate -> selectedDate = newDate },
            onDismiss = { showCalendarModal = false }
        )
    }
}
