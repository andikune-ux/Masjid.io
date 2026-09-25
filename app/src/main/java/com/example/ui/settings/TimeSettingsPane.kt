package com.example.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
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

    // Derive current effective time from settings
    val effectiveNow = remember(settings.isManualTimeEnabled, settings.manualTimeOffsetSeconds) {
        if (settings.isManualTimeEnabled) {
            LocalDateTime.now().plusSeconds(settings.manualTimeOffsetSeconds)
        } else {
            LocalDateTime.now()
        }
    }

    var selectedDate by remember { mutableStateOf(effectiveNow.toLocalDate()) }
    var selectedHour by remember { mutableStateOf(effectiveNow.hour) }
    var selectedMinute by remember { mutableStateOf(effectiveNow.minute) }
    var selectedSecond by remember { mutableStateOf(effectiveNow.second) }

    val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Waktu & Tanggal Manual (Khusus TV Offline / Tanpa Internet)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // Master Switch: Mode Manual vs Otomatis
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Aktifkan Mode Waktu Manual (Offline)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (settings.isManualTimeEnabled)
                        "Menggunakan waktu & tanggal yang diatur sendiri secara manual. Detik tetap berdetik normal setiap detik."
                    else
                        "Menggunakan waktu otomatis dari sistem TV / jaringan internet.",
                    fontSize = 12.sp,
                    color = if (settings.isManualTimeEnabled) IslamicGoldLight else TextSecondary
                )
            }

            Switch(
                checked = settings.isManualTimeEnabled,
                onCheckedChange = { isEnabled ->
                    onUpdate(settings.copy(isManualTimeEnabled = isEnabled))
                    val msg = if (isEnabled) "Mode waktu manual diaktifkan" else "Kembali ke waktu otomatis sistem"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = IslamicGold,
                    checkedTrackColor = IslamicGreen
                )
            )
        }

        // Section 1: Tanggal Manual + Tombol Kalender Mini
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
                    text = "Pengaturan Hari & Tanggal:",
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
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "PILIH DI KALENDER MINI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 2: Jam, Menit, Detik (TV Remote friendly Stepper)
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
                    text = "Pengaturan Jam, Menit & Detik:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Jam
                TimeStepperItem(
                    label = "Jam (0-23)",
                    value = selectedHour,
                    range = 0..23,
                    onValueChange = { selectedHour = it },
                    modifier = Modifier.weight(1f)
                )

                // Menit
                TimeStepperItem(
                    label = "Menit (0-59)",
                    value = selectedMinute,
                    range = 0..59,
                    onValueChange = { selectedMinute = it },
                    modifier = Modifier.weight(1f)
                )

                // Detik
                TimeStepperItem(
                    label = "Detik (0-59)",
                    value = selectedSecond,
                    range = 0..59,
                    onValueChange = { selectedSecond = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Buttons: Terapkan & Reset
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
                        "Waktu manual berhasil diterapkan: ${selectedDate.format(fullDateFormatter)} • ${String.format("%02d:%02d:%02d", selectedHour, selectedMinute, selectedSecond)}",
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
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "TERAPKAN WAKTU MANUAL", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

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

                    Toast.makeText(context, "Waktu direset ke waktu sistem TV", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(UrgentRed)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "RESET OTOMATIS", fontSize = 12.sp)
            }
        }
    }

    // Mini Calendar Popup Dialog
    if (showCalendarModal) {
        MiniCalendarPickerModal(
            initialDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
            },
            onDismiss = { showCalendarModal = false }
        )
    }
}

@Composable
private fun TimeStepperItem(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0C1B26))
            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    val nextVal = if (value - 1 < range.first) range.last else value - 1
                    onValueChange(nextVal)
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x33FFFFFF))
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang", tint = IslamicGold, modifier = Modifier.size(16.dp))
            }

            Text(
                text = String.format("%02d", value),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            IconButton(
                onClick = {
                    val nextVal = if (value + 1 > range.last) range.first else value + 1
                    onValueChange(nextVal)
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x33FFFFFF))
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = IslamicGold, modifier = Modifier.size(16.dp))
            }
        }
    }
}
