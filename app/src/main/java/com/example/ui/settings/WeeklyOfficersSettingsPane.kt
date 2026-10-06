package dev.andikune.masjidio.ui.settings

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.data.model.DailyOfficerItem
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import dev.andikune.masjidio.util.MediaPersistenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TARGET_GLOBAL = "__global__"

@Composable
fun WeeklyOfficersSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    // ============================================================
    // V1.04.421: Copy foto ke folder permanen (semua target)
    // ============================================================
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val target = photoTarget
        photoTarget = null
        if (uri == null || target == null) return@rememberLauncherForActivityResult

        scope.launch {
            // Copy file ke folder permanen
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context,
                    sourceUri = uri.toString(),
                    folder = MediaPersistenceHelper.FOLDER_OFFICER,
                    fileNamePrefix = "petugas"
                )
            }
            // Fallback kalau copy gagal
            val finalPath = localPath ?: uri.toString()

            if (target == TARGET_GLOBAL) {
                // Hapus foto lama
                settings.officerPhotoUri?.let { old ->
                    if (old != finalPath) MediaPersistenceHelper.deleteFile(old)
                }
                onUpdate(settings.copy(officerPhotoUri = finalPath))
                return@launch
            }

            val freshList = if (settings.weeklyOfficers.size == 7) settings.weeklyOfficers
                            else AppSettings.createDefaultWeeklySchedule()

            val dayItem = freshList.getOrElse(selectedDayIndex) {
                DailyOfficerItem(dayName = days[selectedDayIndex])
            }

            // Hapus foto lama untuk target ini
            val oldPath = when (target) {
                "imam_subuh" -> dayItem.fotoImamSubuh
                "muadzin_subuh" -> dayItem.fotoMuadzinSubuh
                "imam_dzuhur" -> dayItem.fotoImamDzuhur
                "muadzin_dzuhur" -> dayItem.fotoMuadzinDzuhur
                "imam_ashar" -> dayItem.fotoImamAshar
                "muadzin_ashar" -> dayItem.fotoMuadzinAshar
                "imam_maghrib" -> dayItem.fotoImamMaghrib
                "muadzin_maghrib" -> dayItem.fotoMuadzinMaghrib
                "imam_isya" -> dayItem.fotoImamIsya
                "muadzin_isya" -> dayItem.fotoMuadzinIsya
                "khatib_jumat" -> dayItem.fotoKhatibJumat
                "ustadz_kajian" -> dayItem.fotoUstadzKajian
                else -> null
            }
            if (oldPath != null && oldPath != finalPath) {
                MediaPersistenceHelper.deleteFile(oldPath)
            }

            val updated = when (target) {
                "imam_subuh"      -> dayItem.copy(fotoImamSubuh = finalPath)
                "muadzin_subuh"   -> dayItem.copy(fotoMuadzinSubuh = finalPath)
                "imam_dzuhur"     -> dayItem.copy(fotoImamDzuhur = finalPath)
                "muadzin_dzuhur"  -> dayItem.copy(fotoMuadzinDzuhur = finalPath)
                "imam_ashar"      -> dayItem.copy(fotoImamAshar = finalPath)
                "muadzin_ashar"   -> dayItem.copy(fotoMuadzinAshar = finalPath)
                "imam_maghrib"    -> dayItem.copy(fotoImamMaghrib = finalPath)
                "muadzin_maghrib" -> dayItem.copy(fotoMuadzinMaghrib = finalPath)
                "imam_isya"       -> dayItem.copy(fotoImamIsya = finalPath)
                "muadzin_isya"    -> dayItem.copy(fotoMuadzinIsya = finalPath)
                "khatib_jumat"    -> dayItem.copy(fotoKhatibJumat = finalPath)
                "ustadz_kajian"   -> dayItem.copy(fotoUstadzKajian = finalPath)
                else -> dayItem
            }

            val newList = freshList.toMutableList()
            newList[selectedDayIndex] = updated
            onUpdate(settings.copy(weeklyOfficers = newList))
        }
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
                            onClick = {
                                settings.officerPhotoUri?.let { old ->
                                    MediaPersistenceHelper.deleteFile(old)
                                }
                                onUpdate(settings.copy(officerPhotoUri = null))
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                            border = BorderStroke(1.dp, UrgentRed),
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

        Text(
            text = "Jadwal Petugas Hari ${days[selectedDayIndex]}:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGreen
        )

        // ---------- 5 ROW PETUGAS (Subuh - Isya) ----------
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
            onImamPhotoDelete = {
                currentDaySchedule.fotoImamSubuh?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoImamSubuh = null))
            },
            onMuadzinPhotoDelete = {
                currentDaySchedule.fotoMuadzinSubuh?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoMuadzinSubuh = null))
            }
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
            onImamPhotoDelete = {
                currentDaySchedule.fotoImamDzuhur?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoImamDzuhur = null))
            },
            onMuadzinPhotoDelete = {
                currentDaySchedule.fotoMuadzinDzuhur?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoMuadzinDzuhur = null))
            }
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
            onImamPhotoDelete = {
                currentDaySchedule.fotoImamAshar?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoImamAshar = null))
            },
            onMuadzinPhotoDelete = {
                currentDaySchedule.fotoMuadzinAshar?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoMuadzinAshar = null))
            }
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
            onImamPhotoDelete = {
                currentDaySchedule.fotoImamMaghrib?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoImamMaghrib = null))
            },
            onMuadzinPhotoDelete = {
                currentDaySchedule.fotoMuadzinMaghrib?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoMuadzinMaghrib = null))
            }
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
            onImamPhotoDelete = {
                currentDaySchedule.fotoImamIsya?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoImamIsya = null))
            },
            onMuadzinPhotoDelete = {
                currentDaySchedule.fotoMuadzinIsya?.let { MediaPersistenceHelper.deleteFile(it) }
                updateDay(currentDaySchedule.copy(fotoMuadzinIsya = null))
            }
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhotoCircle(
                    photoUri = currentDaySchedule.fotoKhatibJumat,
                    onClick = { pickPhoto("khatib_jumat") },
                    onDelete = {
                        currentDaySchedule.fotoKhatibJumat?.let { MediaPersistenceHelper.deleteFile(it) }
                        updateDay(currentDaySchedule.copy(fotoKhatibJumat = null))
                    },
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhotoCircle(
                    photoUri = currentDaySchedule.fotoUstadzKajian,
                    onClick = { pickPhoto("ustadz_kajian") },
                    onDelete = {
                        currentDaySchedule.fotoUstadzKajian?.let { MediaPersistenceHelper.deleteFile(it) }
                        updateDay(currentDaySchedule.copy(fotoUstadzKajian = null))
                    },
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
