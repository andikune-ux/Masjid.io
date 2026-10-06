package com.example.ui.settings

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
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.data.model.DailyOfficerItem
import com.example.ui.components.FilePickerMode
import com.example.ui.components.VideoFilePickerDialog
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import com.example.util.MediaPersistenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TARGET_GLOBAL = "__global__"

// ============================================================
// V1.04.425 — Menu Pilihan untuk Setiap Foto
// ============================================================
private data class PhotoTarget(
    val key: String,
    val label: String,
    val currentUri: String?
)

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

    // ============================================================
    // V1.04.425 — State Menu Pilihan Foto
    // photoTarget = file/foto mana yang sedang dipilih user
    // ============================================================
    var activePhotoTarget by remember { mutableStateOf<String?>(null) }   // key target
    var showPhotoMenu by remember { mutableStateOf(false) }                // menu picker muncul
    var showFilePicker by remember { mutableStateOf(false) }               // custom file picker

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
    // V1.04.425 — Process File Terpilih (dari GALERI atau FILE)
    // ============================================================
    fun processPickedFile(sourceUriString: String, target: String) {
        scope.launch {
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context,
                    sourceUri = sourceUriString,
                    folder = MediaPersistenceHelper.FOLDER_OFFICER,
                    fileNamePrefix = "petugas"
                )
            }
            val finalPath = localPath ?: sourceUriString

            if (target == TARGET_GLOBAL) {
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

    // ============================================================
    // GALERI LAUNCHER (Android Native)
    // ============================================================
    val photoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val target = activePhotoTarget
        activePhotoTarget = null
        if (uri == null || target == null) return@rememberLauncherForActivityResult
        processPickedFile(uri.toString(), target)
    }

    // ============================================================
    // Helper: Buka Menu Pilihan Foto (GALERI / FILE / HAPUS)
    // ============================================================
    fun openPhotoMenu(targetKey: String) {
        activePhotoTarget = targetKey
        showPhotoMenu = true
    }

    // ============================================================
    // Helper: Cek apakah foto sudah ada (untuk opsi HAPUS di menu)
    // ============================================================
    fun hasExistingPhoto(targetKey: String): Boolean {
        return when (targetKey) {
            TARGET_GLOBAL -> !settings.officerPhotoUri.isNullOrBlank()
            "imam_subuh" -> !currentDaySchedule.fotoImamSubuh.isNullOrBlank()
            "muadzin_subuh" -> !currentDaySchedule.fotoMuadzinSubuh.isNullOrBlank()
            "imam_dzuhur" -> !currentDaySchedule.fotoImamDzuhur.isNullOrBlank()
            "muadzin_dzuhur" -> !currentDaySchedule.fotoMuadzinDzuhur.isNullOrBlank()
            "imam_ashar" -> !currentDaySchedule.fotoImamAshar.isNullOrBlank()
            "muadzin_ashar" -> !currentDaySchedule.fotoMuadzinAshar.isNullOrBlank()
            "imam_maghrib" -> !currentDaySchedule.fotoImamMaghrib.isNullOrBlank()
            "muadzin_maghrib" -> !currentDaySchedule.fotoMuadzinMaghrib.isNullOrBlank()
            "imam_isya" -> !currentDaySchedule.fotoImamIsya.isNullOrBlank()
            "muadzin_isya" -> !currentDaySchedule.fotoMuadzinIsya.isNullOrBlank()
            "khatib_jumat" -> !currentDaySchedule.fotoKhatibJumat.isNullOrBlank()
            "ustadz_kajian" -> !currentDaySchedule.fotoUstadzKajian.isNullOrBlank()
            else -> false
        }
    }

    // ============================================================
    // Helper: Hapus Foto (dari menu)
    // ============================================================
    fun deletePhotoForTarget(targetKey: String) {
        if (targetKey == TARGET_GLOBAL) {
            settings.officerPhotoUri?.let { MediaPersistenceHelper.deleteFile(it) }
            onUpdate(settings.copy(officerPhotoUri = null))
            return
        }

        val updated = when (targetKey) {
            "imam_subuh"      -> currentDaySchedule.copy(fotoImamSubuh = null)
            "muadzin_subuh"   -> currentDaySchedule.copy(fotoMuadzinSubuh = null)
            "imam_dzuhur"     -> currentDaySchedule.copy(fotoImamDzuhur = null)
            "muadzin_dzuhur"  -> currentDaySchedule.copy(fotoMuadzinDzuhur = null)
            "imam_ashar"      -> currentDaySchedule.copy(fotoImamAshar = null)
            "muadzin_ashar"   -> currentDaySchedule.copy(fotoMuadzinAshar = null)
            "imam_maghrib"    -> currentDaySchedule.copy(fotoImamMaghrib = null)
            "muadzin_maghrib" -> currentDaySchedule.copy(fotoMuadzinMaghrib = null)
            "imam_isya"       -> currentDaySchedule.copy(fotoImamIsya = null)
            "muadzin_isya"    -> currentDaySchedule.copy(fotoMuadzinIsya = null)
            "khatib_jumat"    -> currentDaySchedule.copy(fotoKhatibJumat = null)
            "ustadz_kajian"   -> currentDaySchedule.copy(fotoUstadzKajian = null)
            else -> currentDaySchedule
        }
        updateDay(updated)
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

        // ============================================================
        // FOTO DEFAULT (FALLBACK)
        // ============================================================
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
                PhotoCircle(
                    photoUri = settings.officerPhotoUri,
                    onClick = { openPhotoMenu(TARGET_GLOBAL) },
                    onDelete = { deletePhotoForTarget(TARGET_GLOBAL) },
                    borderColor = IslamicGold
                )
                Column {
                    Text(
                        text = "Tap foto untuk upload/ganti",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "💡 Pilih GALERI (HP) atau FILE (TV)",
                        fontSize = 11.sp,
                        color = IslamicGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ============================================================
        // PILIH HARI
        // ============================================================
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

        // ============================================================
        // 5 ROW PETUGAS (Subuh - Isya)
        // ============================================================
        PrayerOfficerRow(
            prayerName = "Subuh",
            imamValue = currentDaySchedule.imamSubuh,
            muadzinValue = currentDaySchedule.muadzinSubuh,
            imamPhotoUri = currentDaySchedule.fotoImamSubuh,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinSubuh,
            onImamChange = { updateDay(currentDaySchedule.copy(imamSubuh = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinSubuh = it)) },
            onImamPhotoClick = { openPhotoMenu("imam_subuh") },
            onMuadzinPhotoClick = { openPhotoMenu("muadzin_subuh") },
            onImamPhotoDelete = { deletePhotoForTarget("imam_subuh") },
            onMuadzinPhotoDelete = { deletePhotoForTarget("muadzin_subuh") }
        )

        PrayerOfficerRow(
            prayerName = "Dzuhur",
            imamValue = currentDaySchedule.imamDzuhur,
            muadzinValue = currentDaySchedule.muadzinDzuhur,
            imamPhotoUri = currentDaySchedule.fotoImamDzuhur,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinDzuhur,
            onImamChange = { updateDay(currentDaySchedule.copy(imamDzuhur = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinDzuhur = it)) },
            onImamPhotoClick = { openPhotoMenu("imam_dzuhur") },
            onMuadzinPhotoClick = { openPhotoMenu("muadzin_dzuhur") },
            onImamPhotoDelete = { deletePhotoForTarget("imam_dzuhur") },
            onMuadzinPhotoDelete = { deletePhotoForTarget("muadzin_dzuhur") }
        )

        PrayerOfficerRow(
            prayerName = "Ashar",
            imamValue = currentDaySchedule.imamAshar,
            muadzinValue = currentDaySchedule.muadzinAshar,
            imamPhotoUri = currentDaySchedule.fotoImamAshar,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinAshar,
            onImamChange = { updateDay(currentDaySchedule.copy(imamAshar = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinAshar = it)) },
            onImamPhotoClick = { openPhotoMenu("imam_ashar") },
            onMuadzinPhotoClick = { openPhotoMenu("muadzin_ashar") },
            onImamPhotoDelete = { deletePhotoForTarget("imam_ashar") },
            onMuadzinPhotoDelete = { deletePhotoForTarget("muadzin_ashar") }
        )

        PrayerOfficerRow(
            prayerName = "Maghrib",
            imamValue = currentDaySchedule.imamMaghrib,
            muadzinValue = currentDaySchedule.muadzinMaghrib,
            imamPhotoUri = currentDaySchedule.fotoImamMaghrib,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinMaghrib,
            onImamChange = { updateDay(currentDaySchedule.copy(imamMaghrib = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinMaghrib = it)) },
            onImamPhotoClick = { openPhotoMenu("imam_maghrib") },
            onMuadzinPhotoClick = { openPhotoMenu("muadzin_maghrib") },
            onImamPhotoDelete = { deletePhotoForTarget("imam_maghrib") },
            onMuadzinPhotoDelete = { deletePhotoForTarget("muadzin_maghrib") }
        )

        PrayerOfficerRow(
            prayerName = "Isya",
            imamValue = currentDaySchedule.imamIsya,
            muadzinValue = currentDaySchedule.muadzinIsya,
            imamPhotoUri = currentDaySchedule.fotoImamIsya,
            muadzinPhotoUri = currentDaySchedule.fotoMuadzinIsya,
            onImamChange = { updateDay(currentDaySchedule.copy(imamIsya = it)) },
            onMuadzinChange = { updateDay(currentDaySchedule.copy(muadzinIsya = it)) },
            onImamPhotoClick = { openPhotoMenu("imam_isya") },
            onMuadzinPhotoClick = { openPhotoMenu("muadzin_isya") },
            onImamPhotoDelete = { deletePhotoForTarget("imam_isya") },
            onMuadzinPhotoDelete = { deletePhotoForTarget("muadzin_isya") }
        )

        // ============================================================
        // KHATIB & KAJIAN
        // ============================================================
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
                    onClick = { openPhotoMenu("khatib_jumat") },
                    onDelete = { deletePhotoForTarget("khatib_jumat") },
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
                    onClick = { openPhotoMenu("ustadz_kajian") },
                    onDelete = { deletePhotoForTarget("ustadz_kajian") },
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

    // ============================================================
    // V1.04.425 — DIALOG MENU PILIHAN FOTO (GALERI / FILE / HAPUS)
    // ============================================================
    if (showPhotoMenu && activePhotoTarget != null) {
        val targetKey = activePhotoTarget!!
        val hasExisting = hasExistingPhoto(targetKey)

        PhotoPickerMenuDialog(
            hasExistingPhoto = hasExisting,
            onGallery = {
                showPhotoMenu = false
                photoGalleryLauncher.launch("image/*")
            },
            onFile = {
                showPhotoMenu = false
                showFilePicker = true
            },
            onDelete = {
                showPhotoMenu = false
                deletePhotoForTarget(targetKey)
                activePhotoTarget = null
            },
            onDismiss = {
                showPhotoMenu = false
                activePhotoTarget = null
            }
        )
    }

    // ============================================================
    // V1.04.425 — CUSTOM FILE PICKER DIALOG
    // ============================================================
    if (showFilePicker && activePhotoTarget != null) {
        VideoFilePickerDialog(
            mode = FilePickerMode.IMAGE,
            title = "Pilih Foto Petugas",
            onFileSelected = { file: File ->
                showFilePicker = false
                val target = activePhotoTarget ?: return@VideoFilePickerDialog
                processPickedFile(file.absolutePath, target)
                activePhotoTarget = null
            },
            onDismiss = {
                showFilePicker = false
                activePhotoTarget = null
            }
        )
    }
}
// ============================================================
// V1.04.425 — DIALOG MENU PILIHAN FOTO
// 3 tombol: GALERI / FILE / HAPUS (kalau foto sudah ada)
// ============================================================
@Composable
private fun PhotoPickerMenuDialog(
    hasExistingPhoto: Boolean,
    onGallery: () -> Unit,
    onFile: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var isFocusedGallery by remember { mutableStateOf(false) }
    var isFocusedFile by remember { mutableStateOf(false) }
    var isFocusedDelete by remember { mutableStateOf(false) }
    var isFocusedCancel by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(440.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0B1720))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // HEADER
            Text(
                text = "PILIH FOTO",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(
                text = "Pilih sumber foto:",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ===== TOMBOL GALERI =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFocusedGallery) IslamicGoldLight else IslamicGold)
                    .border(
                        width = if (isFocusedGallery) 3.dp else 0.dp,
                        color = if (isFocusedGallery) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .onFocusChanged { isFocusedGallery = it.isFocused }
                    .focusable()
                    .clickable { onGallery() }
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = Color(0xFF09141D),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GALERI",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF09141D)
                    )
                    Text(
                        text = "Buka aplikasi galeri HP",
                        fontSize = 11.sp,
                        color = Color(0xFF09141D).copy(alpha = 0.7f)
                    )
                }
            }

            // ===== TOMBOL FILE =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFocusedFile) IslamicGoldLight else IslamicGold)
                    .border(
                        width = if (isFocusedFile) 3.dp else 0.dp,
                        color = if (isFocusedFile) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .onFocusChanged { isFocusedFile = it.isFocused }
                    .focusable()
                    .clickable { onFile() }
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = Color(0xFF09141D),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FILE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF09141D)
                    )
                    Text(
                        text = "Pilih dari folder (cocok untuk TV)",
                        fontSize = 11.sp,
                        color = Color(0xFF09141D).copy(alpha = 0.7f)
                    )
                }
            }

            // ===== TOMBOL HAPUS (hanya kalau foto sudah ada) =====
            if (hasExistingPhoto) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFocusedDelete) UrgentRed.copy(alpha = 0.3f)
                            else Color(0x33FF5252)
                        )
                        .border(
                            width = if (isFocusedDelete) 3.dp else 1.5.dp,
                            color = if (isFocusedDelete) UrgentRed else UrgentRed.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .onFocusChanged { isFocusedDelete = it.isFocused }
                        .focusable()
                        .clickable { onDelete() }
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = UrgentRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HAPUS FOTO",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UrgentRed
                        )
                        Text(
                            text = "Hilangkan foto ini",
                            fontSize = 11.sp,
                            color = UrgentRed.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ===== TOMBOL BATAL =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isFocusedCancel) Color(0x44FFFFFF) else Color.Transparent)
                    .border(
                        width = if (isFocusedCancel) 2.dp else 1.dp,
                        color = if (isFocusedCancel) IslamicGoldLight else TextSecondary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .onFocusChanged { isFocusedCancel = it.isFocused }
                    .focusable()
                    .clickable { onDismiss() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BATAL",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }
    }
}

// ============================================================
// BARIS PETUGAS (IMAM + MUADZIN)
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
// LINGKARAN FOTO — tap = buka menu pilihan (GALERI/FILE/HAPUS)
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
