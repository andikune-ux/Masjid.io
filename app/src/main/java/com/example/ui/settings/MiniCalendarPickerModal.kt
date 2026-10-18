package dev.andikune.masjidio.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.andikune.masjidio.ui.components.NeonFocusBorder
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MiniCalendarPickerModal(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val localeId = Locale.forLanguageTag("id-ID")
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", localeId)
    val displayDateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", localeId)

    var isFocusedPrev by remember { mutableStateOf(false) }
    var isFocusedNext by remember { mutableStateOf(false) }
    var isFocusedClose by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier.fillMaxWidth(0.94f).widthIn(max = 420.dp).fillMaxHeight(0.92f).clip(RoundedCornerShape(20.dp)).background(Color(0xFF091620)).border(2.dp, IslamicGold, RoundedCornerShape(20.dp)).padding(20.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, tint = IslamicGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PILIH TANGGAL", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                }
                NeonFocusBorder(focused = isFocusedClose, cornerRadius = 8.dp, borderWidth = 3.dp) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0x33FF5252)).focusable().onFocusChanged { isFocusedClose = it.isFocused }.clickable { onDismiss() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, "Tutup", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Month & Navigation
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0x33000000)).padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                NeonFocusBorder(focused = isFocusedPrev, cornerRadius = 8.dp, borderWidth = 3.dp) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).focusable().onFocusChanged { isFocusedPrev = it.isFocused }.clickable { currentYearMonth = currentYearMonth.minusMonths(1) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Prev", tint = IslamicGold, modifier = Modifier.size(18.dp))
                    }
                }
                Text(currentYearMonth.format(monthFormatter).uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                NeonFocusBorder(focused = isFocusedNext, cornerRadius = 8.dp, borderWidth = 3.dp) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).focusable().onFocusChanged { isFocusedNext = it.isFocused }.clickable { currentYearMonth = currentYearMonth.plusMonths(1) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next", tint = IslamicGold, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Days of Week Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (dayName in daysOfWeek) {
                    Text(dayName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (dayName == "Min" || dayName == "Jum") IslamicGold else TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Days Grid
            val firstDayOfMonth = currentYearMonth.atDay(1)
            val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.value - 1)
            val daysInMonth = currentYearMonth.lengthOfMonth()
            val totalCells = ((dayOfWeekOffset + daysInMonth + 6) / 7) * 7

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (row in 0 until (totalCells / 7)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - dayOfWeekOffset + 1
                            if (dayNumber in 1..daysInMonth) {
                                val cellDate = currentYearMonth.atDay(dayNumber)
                                val isSelected = cellDate == selectedDate
                                val isToday = cellDate == LocalDate.now()
                                var isFocusedDay by remember(cellDate) { mutableStateOf(false) }

                                NeonFocusBorder(focused = isFocusedDay, cornerRadius = 20.dp, borderWidth = 2.dp, modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp)) {
                                    Box(
                                        modifier = Modifier.fillMaxSize().clip(CircleShape).background(if (isSelected) IslamicGold else if (isToday) Color(0x33FFD700) else Color.Transparent).border(if (isToday && !isSelected) 1.dp else 0.dp, if (isToday && !isSelected) IslamicGold else Color.Transparent, CircleShape).focusable().onFocusChanged { isFocusedDay = it.isFocused }.clickable {
                                            selectedDate = cellDate
                                            Toast.makeText(context, "Tanggal: ${cellDate.format(displayDateFormatter)}", Toast.LENGTH_SHORT).show()
                                        },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("$dayNumber", fontSize = 13.sp, fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFF09141D) else TextPrimary)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Date Display
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0x33000000)).padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text("Terpilih: ${selectedDate.format(displayDateFormatter)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = IslamicGoldLight)
            }

            Spacer(modifier = Modifier.height(14.dp))

            var isFocusedHariIni by remember { mutableStateOf(false) }
            var isFocusedGunakan by remember { mutableStateOf(false) }

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NeonFocusBorder(focused = isFocusedHariIni, cornerRadius = 10.dp, borderWidth = 3.dp, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(10.dp)).background(Color(0x22000000)).focusable().onFocusChanged { isFocusedHariIni = it.isFocused }.clickable {
                        val today = LocalDate.now(); selectedDate = today; currentYearMonth = YearMonth.from(today)
                        Toast.makeText(context, "Direset ke hari ini", Toast.LENGTH_SHORT).show()
                    }, contentAlignment = Alignment.Center) {
                        Text("Hari Ini", fontSize = 12.sp, color = TextPrimary)
                    }
                }
                NeonFocusBorder(focused = isFocusedGunakan, cornerRadius = 10.dp, borderWidth = 3.5.dp, modifier = Modifier.weight(1.4f)) {
                    Box(modifier = Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(10.dp)).background(IslamicGreen).focusable().onFocusChanged { isFocusedGunakan = it.isFocused }.clickable {
                        onDateSelected(selectedDate)
                        Toast.makeText(context, "Tersimpan: ${selectedDate.format(displayDateFormatter)}", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }, contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, null, tint = Color(0xFF09141D), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("GUNAKAN TANGGAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF09141D))
                        }
                    }
                }
            }
        }
    }
}
