package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.ui.components.FilePickerMode
import com.example.ui.components.MasjidVideoPlayer
import com.example.ui.components.TvSlider
import com.example.ui.components.TvToggle
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

@Composable
fun VideoSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ============================================================
    // V1.04.425 — State Dialog Custom File Picker
    // ============================================================
    var showVideoPicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    // ============================================================
    // V1.04.425 — Helper: proses file video terpilih
    // ============================================================
    fun processVideoFile(file: File) {
        scope.launch {
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context,
                    sourceUri = file.absolutePath,
                    folder = MediaPersistenceHelper.FOLDER_VIDEO,
                    fileNamePrefix = "video"
                )
            }
            if (localPath != null) {
                MediaPersistenceHelper.cleanupFolder(
                    context,
                    MediaPersistenceHelper.FOLDER_VIDEO,
                    3
                )
                onUpdate(settings.copy(videoUri = localPath, videoEnabled = true))
            } else {
                onUpdate(settings.copy(videoUri = file.absolutePath, videoEnabled = true))
            }
        }
    }

    fun processPhotoFile(file: File) {
        scope.launch {
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context,
                    sourceUri = file.absolutePath,
                    folder = MediaPersistenceHelper.FOLDER_SLIDESHOW,
                    fileNamePrefix = "foto"
                )
            }
            if (localPath != null) {
                val newList = settings.photoSlideshowUris + localPath
                onUpdate(
                    settings.copy(
                        photoSlideshowUris = newList,
                        photoSlideshowEnabled = true
                    )
                )
            } else {
                val newList = settings.photoSlideshowUris + file.absolutePath
                onUpdate(
                    settings.copy(
                        photoSlideshowUris = newList,
                        photoSlideshowEnabled = true
                    )
                )
            }
        }
    }

    // ============================================================
    // V1.04.425 BARU — GALERI LAUNCHER (Android Native)
    // Tombol GALERI pakai galeri bawaan HP — lebih mudah untuk user HP
    // ============================================================

    // Video dari Galeri
    val videoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val localPath = withContext(Dispatchers.IO) {
                    MediaPersistenceHelper.copyVideoToPermanent(context, uri.toString())
                }
                if (localPath != null) {
                    MediaPersistenceHelper.cleanupFolder(
                        context,
                        MediaPersistenceHelper.FOLDER_VIDEO,
                        3
                    )
                    onUpdate(settings.copy(videoUri = localPath, videoEnabled = true))
                } else {
                    onUpdate(settings.copy(videoUri = uri.toString(), videoEnabled = true))
                }
            }
        }
    }

    // Foto dari Galeri (multi-select)
    val photoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val localPaths = withContext(Dispatchers.IO) {
                    uris.mapNotNull { uri ->
                        MediaPersistenceHelper.copyToPermanent(
                            context = context,
                            sourceUri = uri.toString(),
                            folder = MediaPersistenceHelper.FOLDER_SLIDESHOW,
                            fileNamePrefix = "foto"
                        )
                    }
                }
                if (localPaths.isNotEmpty()) {
                    val newUris = settings.photoSlideshowUris + localPaths
                    onUpdate(
                        settings.copy(
                            photoSlideshowUris = newUris,
                            photoSlideshowEnabled = true
                        )
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Video Kegiatan & Rutinitas Masjid",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // ============================================================
        // 1. VIDEO TOGGLE
        // ============================================================
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
                    text = "Aktifkan Pemutaran Video Masjid",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Menayangkan rekaman kegiatan, kajian, atau dokumentasi masjid secara berulang (looping) tanpa suara.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
            Switch(
                checked = settings.videoEnabled,
                onCheckedChange = { onUpdate(settings.copy(videoEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = IslamicGold,
                    checkedTrackColor = IslamicGreen
                )
            )
        }

        // ============================================================
        // 2. VIDEO PICKER — 2 tombol: GALERI + FILE MANAGER
        // ============================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "File Video Masjid (Format MP4 / MKV / WEBM)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preview video
                Box(
                    modifier = Modifier
                        .size(width = 160.dp, height = 90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black)
                        .border(1.dp, IslamicGold, RoundedCornerShape(10.dp))
                ) {
                    if (!settings.videoUri.isNullOrBlank()) {
                        MasjidVideoPlayer(
                            videoUriString = settings.videoUri,
                            isFullscreen = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak Ada Video",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (!settings.videoUri.isNullOrBlank())
                            "Video terpilih (tersimpan permanen)"
                        else "Belum ada file video yang dipilih.",
                        fontSize = 12.sp,
                        color = if (!settings.videoUri.isNullOrBlank()) IslamicGreen else TextSecondary
                    )

                    // ============================================================
                    // V1.04.425 — 2 TOMBOL: GALERI + FILE
                    // ============================================================
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Tombol GALERI (buka galeri bawaan HP)
                        TvActionButton(
                            icon = Icons.Default.PhotoLibrary,
                            label = "GALERI",
                            backgroundColor = IslamicGold,
                            textColor = Color(0xFF09141D),
                            onClick = { videoGalleryLauncher.launch("video/*") }
                        )
                        // Tombol FILE (buka custom picker)
                        TvActionButton(
                            icon = Icons.Default.FolderOpen,
                            label = "FILE",
                            backgroundColor = IslamicGold,
                            textColor = Color(0xFF09141D),
                            onClick = { showVideoPicker = true }
                        )
                        if (!settings.videoUri.isNullOrBlank()) {
                            TvActionButton(
                                icon = Icons.Default.Delete,
                                label = "HAPUS",
                                backgroundColor = Color.Transparent,
                                textColor = UrgentRed,
                                isOutlined = true,
                                onClick = {
                                    settings.videoUri?.let { MediaPersistenceHelper.deleteFile(it) }
                                    onUpdate(settings.copy(videoUri = null, videoEnabled = false))
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "💡 GALERI = pakai aplikasi galeri HP. FILE = pilih dari folder (cocok untuk TV).",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.8f)
            )
        }
        // ============================================================
// 3. PENGATURAN UKURAN FRAME
// ============================================================
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
            imageVector = Icons.Default.AspectRatio,
            contentDescription = null,
            tint = IslamicGold,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "PENGATURAN UKURAN FRAME",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(
                text = "Cara video/foto menyesuaikan diri dengan frame. " +
                        "Berlaku untuk video dan foto slideshow.",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }

    FrameModeOption(
        title = "POTONG (Crop)",
        description = "Video/foto penuhi frame, sisi berlebih dipotong. Seperti Instagram Reels.",
        emojiLabel = "✂️",
        isSelected = settings.videoFrameScale == "POTONG",
        onClick = { onUpdate(settings.copy(videoFrameScale = "POTONG")) }
    )

    FrameModeOption(
        title = "PAS (Fit / Letterbox)",
        description = "Video/foto tampil utuh, ada ruang hitam di sisi. Seperti Netflix.",
        emojiLabel = "📺",
        isSelected = settings.videoFrameScale == "PAS",
        onClick = { onUpdate(settings.copy(videoFrameScale = "PAS")) }
    )

    FrameModeOption(
        title = "ZOOM (Fill)",
        description = "Video/foto diperbesar penuhi frame, tengah fokus. Seperti TikTok.",
        emojiLabel = "🔍",
        isSelected = settings.videoFrameScale == "ZOOM",
        onClick = { onUpdate(settings.copy(videoFrameScale = "ZOOM")) }
    )

    FrameModeOption(
        title = "FULL (Fullscreen)",
        description = "Video/foto menutupi seluruh layar. Panel kiri (jam & jadwal) disembunyikan.",
        emojiLabel = "🖥️",
        isSelected = settings.videoFrameScale == "FULL",
        onClick = { onUpdate(settings.copy(videoFrameScale = "FULL")) }
    )

    FrameModeOption(
        title = "FIT (Stretch / Paksa Sesuaikan)",
        description = "Video/foto ditarik & dipaksa memenuhi frame (bisa distorsi). Untuk dimensi aneh.",
        emojiLabel = "📐",
        isSelected = settings.videoFrameScale == "FIT",
        onClick = { onUpdate(settings.copy(videoFrameScale = "FIT")) }
    )
}

// ============================================================
// 4. AUTO-SWITCH MODE
// ============================================================
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
            imageVector = Icons.Default.Autorenew,
            contentDescription = null,
            tint = IslamicGold,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "AUTO-SWITCH MODE",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Text(
                text = "Otomatis bolak-balik antara Mode Video dan Mode Normal lengkap. " +
                        "Jeda otomatis saat Mode Fokus / Slide Fullscreen / Ramadhan aktif.",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }

    TvToggle(
        label = "Aktifkan Auto-Switch",
        description = if (settings.autoSwitchEnabled)
            "Mode akan otomatis berganti sesuai interval di bawah"
        else
            "Mode Video tampil terus (tidak berganti)",
        isChecked = settings.autoSwitchEnabled,
        onToggle = { onUpdate(settings.copy(autoSwitchEnabled = it)) }
    )

    if (settings.autoSwitchEnabled) {
        TvSlider(
            label = "Interval Mode Video",
            value = settings.videoModeIntervalMinutes.toFloat(),
            onValueChange = {
                onUpdate(settings.copy(videoModeIntervalMinutes = it.toInt()))
            },
            valueRange = 1f..60f,
            steps = 58,
            unit = " menit"
        )

        TvSlider(
            label = "Durasi Mode Normal",
            value = settings.normalModeDurationMinutes.toFloat(),
            onValueChange = {
                onUpdate(settings.copy(normalModeDurationMinutes = it.toInt()))
            },
            valueRange = 1f..30f,
            steps = 28,
            unit = " menit"
        )

        TvToggle(
            label = "Tunggu Video Selesai",
            description = if (settings.waitVideoFinishBeforeSwitch)
                "Switch ke Mode Normal menunggu video selesai loop 1x (video tidak terpotong)"
            else
                "Switch langsung saat interval habis (video bisa terpotong)",
            isChecked = settings.waitVideoFinishBeforeSwitch,
            onToggle = { onUpdate(settings.copy(waitVideoFinishBeforeSwitch = it)) }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x22FFD700))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "📋 Cara Kerja:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "• Mode Video tampil ${settings.videoModeIntervalMinutes} menit\n" +
                            "• Lalu switch ke Mode Normal ${settings.normalModeDurationMinutes} menit\n" +
                            "• Balik lagi ke Mode Video, dan seterusnya\n" +
                            "• Otomatis JEDA saat:\n" +
                            "  - Mode Fokus Sholat aktif\n" +
                            "  - Slide Fullscreen tampil\n" +
                            "  - Mode Ramadhan aktif",
                    fontSize = 11.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        if (settings.videoUri.isNullOrBlank() && settings.photoSlideshowUris.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FF5252))
                    .border(1.dp, UrgentRed.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "⚠️ Belum ada video / foto yang di-upload. " +
                            "Auto-switch tidak akan jalan sampai ada video atau foto.",
                    fontSize = 11.sp,
                    color = Color(0xFFFF8A80),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
        // ============================================================
        // 5. FOTO SLIDESHOW — V1.04.425 (2 tombol: GALERI + FILE)
        // ============================================================
        Text(
            text = "Foto Kegiatan Masjid (Slideshow)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

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
                    text = "Aktifkan Foto Slideshow",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Foto akan tampil di posisi video kalau video tidak aktif.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
            Switch(
                checked = settings.photoSlideshowEnabled,
                onCheckedChange = { onUpdate(settings.copy(photoSlideshowEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = IslamicGold,
                    checkedTrackColor = IslamicGreen
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Foto (${settings.photoSlideshowUris.size} foto)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                if (settings.photoSlideshowUris.isNotEmpty()) {
                    TvActionButton(
                        icon = Icons.Default.DeleteSweep,
                        label = "HAPUS SEMUA",
                        backgroundColor = Color.Transparent,
                        textColor = UrgentRed,
                        isOutlined = true,
                        onClick = {
                            settings.photoSlideshowUris.forEach { path ->
                                MediaPersistenceHelper.deleteFile(path)
                            }
                            onUpdate(settings.copy(photoSlideshowUris = emptyList()))
                        }
                    )
                }
            }

            if (settings.photoSlideshowUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(settings.photoSlideshowUris) { index, uri ->
                        Box(
                            modifier = Modifier
                                .size(width = 140.dp, height = 90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black)
                                .border(1.dp, IslamicGold, RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Foto ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xAA000000))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    fontSize = 10.sp,
                                    color = IslamicGoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(UrgentRed)
                                    .clickable {
                                        val newList = settings.photoSlideshowUris.toMutableList()
                                        val removed = newList.removeAt(index)
                                        MediaPersistenceHelper.deleteFile(removed)
                                        onUpdate(settings.copy(photoSlideshowUris = newList))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22000000))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada foto. Tekan tombol di bawah untuk menambah.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // ============================================================
            // V1.04.425 — 2 TOMBOL: GALERI + FILE
            // ============================================================
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tombol GALERI (buka galeri bawaan HP, multi-select)
                TvActionButton(
                    icon = Icons.Default.PhotoLibrary,
                    label = "GALERI",
                    backgroundColor = IslamicGold,
                    textColor = Color(0xFF09141D),
                    onClick = { photoGalleryLauncher.launch("image/*") }
                )
                // Tombol FILE (buka custom picker)
                TvActionButton(
                    icon = Icons.Default.Add,
                    label = "FILE",
                    backgroundColor = IslamicGold,
                    textColor = Color(0xFF09141D),
                    onClick = { showPhotoPicker = true }
                )
            }

            Text(
                text = "💡 GALERI = pakai galeri HP (bisa pilih banyak foto sekaligus). FILE = pilih satu per satu dari folder.",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.8f),
                lineHeight = 15.sp
            )
        }

        TvSlider(
            label = "Interval Ganti Foto",
            value = settings.photoSlideshowIntervalSeconds.toFloat(),
            onValueChange = {
                onUpdate(settings.copy(photoSlideshowIntervalSeconds = it.toInt()))
            },
            valueRange = 3f..120f,
            steps = 38,
            unit = " detik"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "ℹ️ Info",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Foto & video otomatis disalin ke folder permanen aplikasi. " +
                            "Aman meski app ditutup, di-update, atau TV di-reboot.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        // ============================================================
        // PICKER DIALOG CALLERS (custom file picker)
        // ============================================================
        if (showVideoPicker) {
            VideoFilePickerDialog(
                mode = FilePickerMode.VIDEO,
                title = "Pilih File Video",
                onFileSelected = { file ->
                    showVideoPicker = false
                    processVideoFile(file)
                },
                onDismiss = { showVideoPicker = false }
            )
        }

        if (showPhotoPicker) {
            VideoFilePickerDialog(
                mode = FilePickerMode.IMAGE,
                title = "Pilih File Foto",
                onFileSelected = { file ->
                    showPhotoPicker = false
                    processPhotoFile(file)
                },
                onDismiss = { showPhotoPicker = false }
            )
        }
    }
}
// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================

@Composable
private fun FrameModeOption(
    title: String,
    description: String,
    emojiLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else if (isSelected) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "frame_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "frame_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 10.dp else 0.dp,
        animationSpec = tween(200),
        label = "frame_shadow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color(0x66FFD700),
                spotColor = Color(0x66FFD700)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> Color(0x33FFD700)
                    isFocused -> Color(0x22FFD700)
                    else -> Color(0x22000000)
                }
            )
            .border(
                width = borderWidth,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isSelected -> IslamicGold
                    else -> Color(0x22FFFFFF)
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) IslamicGold.copy(alpha = 0.25f)
                        else Color(0x22FFFFFF)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emojiLabel, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isSelected -> IslamicGoldLight
                        isFocused -> Color(0xFFFFE44D)
                        else -> TextPrimary
                    }
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = IslamicGold)
            )
        }
    }
}

@Composable
private fun TvActionButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    isOutlined: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else if (isOutlined) 1.5.dp else 0.dp,
        animationSpec = tween(200),
        label = "btn_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 12.dp else 0.dp,
        animationSpec = tween(200),
        label = "btn_shadow"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color(0x88FFD700),
                spotColor = Color(0x88FFD700)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(if (isOutlined) Color.Transparent else backgroundColor)
            .border(
                width = borderWidth,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isOutlined -> textColor.copy(alpha = 0.6f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
