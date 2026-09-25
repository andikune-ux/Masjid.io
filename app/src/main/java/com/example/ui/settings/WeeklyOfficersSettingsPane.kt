package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private const val TARGET_GLOBAL = "__global__"

@Composable
fun WeeklyOfficersSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDayIndex by remember { mutableStateOf(0) }
    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Ahad")

    var photoTarget by remember { mutableStateOf<String?>(null) }

    val currentOfficersList = remember(settings.weeklyOfficers) {
        if (settings.weeklyOfficers.size == 7) settings.weeklyOfficers
        else AppSettings.createDefaultWeeklySchedule()
    }
    val currentDaySchedule = currentOfficersList.getOrElse(selectedDayIndex) {
        DailyOfficerItem(dayName = days[selectedDayIndex])
    }

    fun updateDay(updatedDay: DailyOfficerItem) {
        val newList = currentOfficersList.toMutableList()
        newList[selectedDayIndex] = updatedDay
        onUpdate(settings.copy(weeklyOfficers = newList))
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val target = photoTarget
        photoTarget = null
        if (uri == null || target == null) return@rememberLauncherForActivityResult

        if (target == TARGET_GLOBAL) {
            onUpdate(settings.copy(officerPhotoUri = uri.toString()))
            return@rememberLauncherForActivityResult
        }

        // Re-read state agar tidak basi
        val freshList = if (settings.weeklyOfficers.size == 7) settings.weeklyOfficers
                        else AppSettings.createDefaultWeeklySchedule()
        val dayItem = freshList.getOrElse(selectedDayIndex) {
            DailyOfficerItem(dayName = days[selectedDayIndex])
        }

        val updated = when (target) {
            "imam_subuh"      -> dayItem.copy(fotoImamSubuh = uri.toString())
            "muadzin_subuh"   -> dayItem.copy(fotoMuadzinSubuh = uri.toString())
            "imam_dzuhur"     -> dayItem.copy(fotoImamDzuhur = uri.toString())
            "muadzin_dzuhur"  -> dayItem.copy(fotoMuadzinDzuhur = uri.toString())
            "imam_ashar"      -> dayItem.copy(fotoImamAshar = uri.toString())
            "muadzin_ashar"   -> dayItem.copy(fotoMuadzinAshar = uri.toString())
            "imam_maghrib"    -> dayItem.copy(fotoImamMaghrib = uri.toString())
            "muadzin_maghrib" -> dayItem.copy(fotoMuadzinMaghrib = uri.toString())
            "imam_isya"       -> dayItem.copy(fotoImamIsya = uri.toString())
            "muadzin_isya"    -> dayItem.copy(fotoMuadzinIsya = uri.toString())
            "khatib_jumat"    -> dayItem.copy(fotoKhatibJumat = uri.toString())
            "ustadz_kajian"   -> dayItem.copy(fotoUstadzKajian = uri.toString())
            else -> dayItem
        }
        val newList = freshList.toMutableList()
        newList[selectedDayIndex] = updated
        onUpdate(settings.copy(weeklyOfficers = newList))
    }

    fun pickPhoto(target: String) {
        photoTarget = target
        photoPickerLauncher.launch("image/*")
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

        // ---------- FOTO DEFAULT (FALLBACK) ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Foto Profil Default Ustadz / Imam",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Dipakai sebagai fallback jika foto per sesi belum di-upload.",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { pickPhoto(TARGET_GLOBAL) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGold,
                            contentColor = Color(0xFF09141D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "PILIH FOTO", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (!settings.officerPhotoUri.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { onUpdate(settings.copy(officerPhotoUri = null)) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(UrgentRed)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "HAPUS", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // ---------- PILIH HARI ----------
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
                        .border(
                            1.dp,
                            if (isSelected) IslamicGold else Color(0x33FFFFFF),
                            RoundedCornerShape(8.dp)
                        )
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

        Text(
            text = "Tap foto untuk upload / ganti. Tekan lama untuk hapus.",
            fontSize = 11.sp,
            color = TextSecondary
        )

        // ---------- JADWAL PETUGAS PER HARI ----------
        Text(
            text = "Jadwal Petugas Hari ${days[selectedDayIndex]}:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGreen
        )

        PrayerOfficerRow(
            prayerName = "Subuh",
            imamValue = currentDaySchedule.imamSubuh,
            muadzinValue = currentDaySchedule.muadzinSubuh,
            imamPhotoUri = currentDaySchedule.fotoImamSubuh,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinSubuh,
            onImamChange = { updateDay(currentDaySchedule.copy(imamSubuh = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinSubuh = it)) },
            onImamPhotoClick = { pickPhoto("imam_subuh") },
            onMuadzinPhotoClick = { pickPhoto("muadzin_subuh") },
            onImamPhotoDelete = { updateDay(currentDaySchedule.copy(fotoImamSubuh = null)) },
            onMuadzinPhotoDelete = { updateDay(currentDaySchedule.copy(fotoMuadzinSubuh = null)) }
        )

        PrayerOfficerRow(
            prayerName = "Dzuhur",
            imamValue = currentDaySchedule.imamDzuhur,
            muadzinValue = currentDaySchedule.muadzinDzuhur,
            imamPhotoUri = currentDaySchedule.fotoImamDzuhur,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinDzuhur,
            onImamChange = { updateDay(currentDaySchedule.copy(imamDzuhur = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinDzuhur = it)) },
            onImamPhotoClick = { pickPhoto("imam_dzuhur") },
            onMuadzinPhotoClick = { pickPhoto("muadzin_dzuhur") },
            onImamPhotoDelete = { updateDay(currentDaySchedule.copy(fotoImamDzuhur = null)) },
            onMuadzinPhotoDelete = { updateDay(currentDaySchedule.copy(fotoMuadzinDzuhur = null)) }
        )

        PrayerOfficerRow(
            prayerName = "Ashar",
            imamValue = currentDaySchedule.imamAshar,
            muadzinValue = currentDaySchedule.muadzinAshar,
            imamPhotoUri = currentDaySchedule.fotoImamAshar,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinAshar,
            onImamChange = { updateDay(currentDaySchedule.copy(imamAshar = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinAshar = it)) },
            onImamPhotoClick = { pickPhoto("imam_ashar") },
            onMuadzinPhotoClick = { pickPhoto("muadzin_ashar") },
            onImamPhotoDelete = { updateDay(currentDaySchedule.copy(fotoImamAshar = null)) },
            onMuadzinPhotoDelete = { updateDay(currentDaySchedule.copy(fotoMuadzinAshar = null)) }
        )

        PrayerOfficerRow(
            prayerName = "Maghrib",
            imamValue = currentDaySchedule.imamMaghrib,
            muadzinValue = currentDaySchedule.muadzinMaghrib,
            imamPhotoUri = currentDaySchedule.fotoImamMaghrib,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinMaghrib,
            onImamChange = { updateDay(currentDaySchedule.copy(imamMaghrib = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinMaghrib = it)) },
            onImamPhotoClick = { pickPhoto("imam_maghrib") },
            onMuadzinPhotoClick = { pickPhoto("muadzin_maghrib") },
            onImamPhotoDelete = { updateDay(currentDaySchedule.copy(fotoImamMaghrib = null)) },
            onMuadzinPhotoDelete = { updateDay(currentDaySchedule.copy(fotoMuadzinMaghrib = null)) }
        )

        PrayerOfficerRow(
            prayerName = "Isya",
            imamValue = currentDaySchedule.imamIsya,
            muadzinValue = currentDaySchedule.muadzinIsya,
            imamPhotoUri = currentDaySchedule.fotoImamIsya,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinIsya,
            onImamChange = { updateDay(currentDaySchedule.copy(imamIsya = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinIsya = it)) },
            onImamPhotoClick = { pickPhoto("imam_isya") },
            onMuadzinPhotoClick = { pickPhoto("muadzin_isya") },
            onImamPhotoDelete = { updateDay(currentDaySchedule.copy(fotoImamIsya = null)) },
            onMuadzinPhotoDelete = { updateDay(currentDaySchedule.copy(fotoMuadzinIsya = null)) }
        )

        // ---------- KHATIB & KAJIAN ----------
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

            // Khatib Jum'at
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhotoCircle(
                    photoUri = currentDaySchedule.fotoKhatibJumat,
                    onClick = { pickPhoto("khatib_jumat") },
                    onDelete = { updateDay(currentDaySchedule.copy(fotoKhatibJumat = null)) },
                    borderColor = IslamicGold
                )
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

            // Ustadz Kajian
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhotoCircle(
                    photoUri = currentDaySchedule.fotoUstadzKajian,
                    onClick = { pickPhoto("ustadz_kajian") },
                    onDelete = { updateDay(currentDaySchedule.copy(fotoUstadzKajian = null)) },
                    borderColor = IslamicGreen
                )
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

// ============================================================
// BARIS PETUGAS (IMAM + MUADZIN) — masing-masing punya foto sendiri
// ============================================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PrayerOfficerRow(
    prayerName: String,
    imamValue: String,
    muadzinValue: String,
    imamPhotoUri: String?,
    muadzinPhotoUri: String?,
    onImamChange: (String) -> Unit,
    onMuadzinChange: (String) -> Unit,
    onImamPhotoClick: () -> Unit,
    onMuadzinPhotoClick: () -> Unit,
    onImamPhotoDelete: () -> Unit,
    onMuadzinPhotoDelete: () -> Unit
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

        PhotoCircle(
            photoUri = imamPhotoUri,
            onClick = onImamPhotoClick,
            onDelete = onImamPhotoDelete,
            borderColor = IslamicGold
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

        PhotoCircle(
            photoUri = muadzinPhotoUri,
            onClick = onMuadzinPhotoClick,
            onDelete = onMuadzinPhotoDelete,
            borderColor = IslamicGreen
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

// ============================================================
// LINGKARAN FOTO — tap = upload/ganti, long-press = hapus
// ============================================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoCircle(
    photoUri: String?,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    borderColor: Color = IslamicGold
) {
    val hasPhoto = !photoUri.isNullOrBlank()

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(borderColor.copy(alpha = 0.15f))
            .border(2.dp, borderColor, CircleShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = if (hasPhoto) onDelete else null
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasPhoto) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Foto Petugas",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = "Tambah Foto",
                tint = borderColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}