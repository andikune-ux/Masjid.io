package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.data.model.DailyOfficerItem
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed

@Composable
fun WeeklyOfficersSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDayIndex by remember { mutableStateOf(0) }
    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Ahad")

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdate(settings.copy(officerPhotoUri = uri.toString()))
        }
    }

    val currentOfficersList = remember(settings.weeklyOfficers) {
        if (settings.weeklyOfficers.size == 7) settings.weeklyOfficers
        else AppSettings.createDefaultWeeklySchedule()
    }

    val currentDaySchedule = currentOfficersList.getOrElse(selectedDayIndex) {
        DailyOfficerItem(dayName = days[selectedDayIndex])
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Jadwal Petugas Sholat 1 Minggu & Foto Ustadz",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // --- SECTION 1: FOTO USTADZ / IMAM ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Foto Profil Ustadz / Imam (Tampil di Layar Beranda)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo Preview
                if (!settings.officerPhotoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = settings.officerPhotoUri,
                        contentDescription = "Foto Ustadz",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, IslamicGold, CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(IslamicGold.copy(alpha = 0.2f))
                            .border(2.dp, IslamicGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Pick Photo Button & Reset
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGold,
                            contentColor = Color(0xFF09141D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "PILIH FOTO DARI GALERI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (!settings.officerPhotoUri.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { onUpdate(settings.copy(officerPhotoUri = null)) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(UrgentRed)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "HAPUS FOTO", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- SECTION 2: 7-DAY SELECTOR TABS ---
        Text(
            text = "Pilih Hari untuk Mengatur Jadwal Imam & Muadzin:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = IslamicGoldLight
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = index == selectedDayIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) IslamicGold else Color(0x33000000))
                        .border(1.dp, if (isSelected) IslamicGold else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                        .clickable { selectedDayIndex = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF09141D) else TextPrimary
                    )
                }
            }
        }

        // Helper function to update current day's officers
        fun updateDay(updatedDay: DailyOfficerItem) {
            val newList = currentOfficersList.toMutableList()
            newList[selectedDayIndex] = updatedDay
            onUpdate(settings.copy(weeklyOfficers = newList))
        }

        // --- SECTION 3: EDIT PETUGAS FOR SELECTED DAY ---
        Text(
            text = "Jadwal Petugas Hari ${days[selectedDayIndex]}:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGreen
        )

        // Subuh
        PrayerOfficerRow(
            prayerName = "Subuh",
            imamValue = currentDaySchedule.imamSubuh,
            muadzinValue = currentDaySchedule.muadzinSubuh,
            onImamChange = { updateDay(currentDaySchedule.copy(imamSubuh = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinSubuh = it)) }
        )

        // Dzuhur
        PrayerOfficerRow(
            prayerName = "Dzuhur",
            imamValue = currentDaySchedule.imamDzuhur,
            muadzinValue = currentDaySchedule.muadzinDzuhur,
            onImamChange = { updateDay(currentDaySchedule.copy(imamDzuhur = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinDzuhur = it)) }
        )

        // Ashar
        PrayerOfficerRow(
            prayerName = "Ashar",
            imamValue = currentDaySchedule.imamAshar,
            muadzinValue = currentDaySchedule.muadzinAshar,
            onImamChange = { updateDay(currentDaySchedule.copy(imamAshar = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinAshar = it)) }
        )

        // Maghrib
        PrayerOfficerRow(
            prayerName = "Maghrib",
            imamValue = currentDaySchedule.imamMaghrib,
            muadzinValue = currentDaySchedule.muadzinMaghrib,
            onImamChange = { updateDay(currentDaySchedule.copy(imamMaghrib = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinMaghrib = it)) }
        )

        // Isya
        PrayerOfficerRow(
            prayerName = "Isya",
            imamValue = currentDaySchedule.imamIsya,
            muadzinValue = currentDaySchedule.muadzinIsya,
            onImamChange = { updateDay(currentDaySchedule.copy(imamIsya = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinIsya = it)) }
        )

        // Special: Khatib & Kajian
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0C1D29))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Khatib Jum'at & Kajian Rutin (${days[selectedDayIndex]}):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentDaySchedule.khatibJumat,
                    onValueChange = { updateDay(currentDaySchedule.copy(khatibJumat = it)) },
                    label = { Text("Khatib Jum'at") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = currentDaySchedule.temaJumat,
                    onValueChange = { updateDay(currentDaySchedule.copy(temaJumat = it)) },
                    label = { Text("Tema Khutbah") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentDaySchedule.ustadzKajian,
                    onValueChange = { updateDay(currentDaySchedule.copy(ustadzKajian = it)) },
                    label = { Text("Ustadz Pemateri Kajian") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = currentDaySchedule.temaKajian,
                    onValueChange = { updateDay(currentDaySchedule.copy(temaKajian = it)) },
                    label = { Text("Tema Kajian Rutin") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )
            }
        }
    }
}

@Composable
private fun PrayerOfficerRow(
    prayerName: String,
    imamValue: String,
    muadzinValue: String,
    onImamChange: (String) -> Unit,
    onMuadzinChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF091620))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prayerName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGold,
            modifier = Modifier.width(68.dp)
        )

        OutlinedTextField(
            value = imamValue,
            onValueChange = onImamChange,
            label = { Text("Imam $prayerName") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IslamicGold,
                unfocusedBorderColor = Color(0x44FFFFFF)
            )
        )

        OutlinedTextField(
            value = muadzinValue,
            onValueChange = onMuadzinChange,
            label = { Text("Muadzin $prayerName") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IslamicGreen,
                unfocusedBorderColor = Color(0x44FFFFFF)
            )
        )
    }
}
