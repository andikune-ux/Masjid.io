package dev.andikune.masjidio.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.components.FilePickerMode
import dev.andikune.masjidio.ui.components.MasjidVideoPlayer
import dev.andikune.masjidio.ui.components.TvSlider
import dev.andikune.masjidio.ui.components.TvToggle
import dev.andikune.masjidio.ui.components.VideoFilePickerDialog
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import dev.andikune.masjidio.util.MediaPersistenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun VideoSettingsPane(settings: AppSettings, onUpdate: (AppSettings) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showVideoPicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    val isVideoMissing = remember(settings.videoUri) { !settings.videoUri.isNullOrBlank() && !File(settings.videoUri).exists() }

    fun processVideoFile(file: File) {
        if (isProcessing) return
        isProcessing = true
        scope.launch {
            try {
                val localPath = withContext(Dispatchers.IO) { MediaPersistenceHelper.copyToPermanent(context, file.absolutePath, MediaPersistenceHelper.FOLDER_VIDEO, "video") }
                if (localPath != null) {
                    MediaPersistenceHelper.cleanupFolder(context, MediaPersistenceHelper.FOLDER_VIDEO, 3)
                    onUpdate(settings.copy(videoUri = localPath, videoEnabled = true))
                } else onUpdate(settings.copy(videoUri = file.absolutePath, videoEnabled = true))
            } finally { isProcessing = false }
        }
    }

    fun processPhotoFile(file: File) {
        if (isProcessing) return
        isProcessing = true
        scope.launch {
            try {
                val localPath = withContext(Dispatchers.IO) { MediaPersistenceHelper.copyToPermanent(context, file.absolutePath, MediaPersistenceHelper.FOLDER_SLIDESHOW, "foto") }
                val newItem = localPath ?: file.absolutePath
                onUpdate(settings.copy(photoSlideshowUris = settings.photoSlideshowUris + newItem, photoSlideshowEnabled = true))
            } finally { isProcessing = false }
        }
    }

    val videoGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && !isProcessing) {
            isProcessing = true
            scope.launch {
                try {
                    val localPath = withContext(Dispatchers.IO) { MediaPersistenceHelper.copyVideoToPermanent(context, uri.toString()) }
                    onUpdate(settings.copy(videoUri = localPath ?: uri.toString(), videoEnabled = true))
                } finally { isProcessing = false }
            }
        }
    }

    val photoGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty() && !isProcessing) {
            isProcessing = true
            scope.launch {
                try {
                    val localPaths = withContext(Dispatchers.IO) { uris.mapNotNull { MediaPersistenceHelper.copyToPermanent(context, it.toString(), MediaPersistenceHelper.FOLDER_SLIDESHOW, "foto") } }
                    if (localPaths.isNotEmpty()) onUpdate(settings.copy(photoSlideshowUris = settings.photoSlideshowUris + localPaths, photoSlideshowEnabled = true))
                } finally { isProcessing = false }
            }
        }
    }

    val isGalleryEnabled = settings.galleryMasterEnabled

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Galeri Kegiatan Masjid", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
        Text("Atur tampilan video dan foto yang akan diputar di layar TV.", fontSize = 13.sp, color = TextSecondary)

        TvToggle(
            label = "Aktifkan Galeri Kegiatan",
            description = "Jika dimatikan, TV hanya menampilkan Layar Default / Background.",
            isChecked = isGalleryEnabled,
            onToggle = { onUpdate(settings.copy(galleryMasterEnabled = it)) }
        )

        if (isProcessing) {
            Box(modifier = Modifier.fillMaxWidth().height(50.dp).focusable().background(Color(0x33FFD700), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Text("⏳ Memproses file, mohon tunggu...", color = IslamicGoldLight, fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.alpha(if (isGalleryEnabled) 1f else 0.35f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("📹 VIDEO KEGIATAN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                TvToggle(
                    label = "Aktifkan Video",
                    description = "Putar video kegiatan di layar TV.",
                    isChecked = settings.videoEnabled,
                    onToggle = { onUpdate(settings.copy(videoEnabled = it)) },
                    enabled = isGalleryEnabled && !isProcessing
                )

                if (settings.videoEnabled && isGalleryEnabled) {
                    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF091620)).border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("File Video Masjid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                        if (isVideoMissing) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = UrgentRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("⚠️ File video terhapus atau tidak ditemukan.", fontSize = 12.sp, color = UrgentRed)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(160.dp, 90.dp).clip(RoundedCornerShape(10.dp)).background(Color.Black).border(1.dp, IslamicGold, RoundedCornerShape(10.dp))) {
                                if (!settings.videoUri.isNullOrBlank()) MasjidVideoPlayer(settings.videoUri, false, modifier = Modifier.fillMaxSize())
                                else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(if (isProcessing) "Memproses..." else "Belum ada video", fontSize = 11.sp, color = TextSecondary) }
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TvActionButton(Icons.Default.PhotoLibrary, "GALERI", IslamicGold, Color(0xFF09141D), enabled = !isProcessing) { videoGalleryLauncher.launch("video/*") }
                                    TvActionButton(Icons.Default.FolderOpen, "FILE", IslamicGold, Color(0xFF09141D), enabled = !isProcessing) { showVideoPicker = true }
                                    if (!settings.videoUri.isNullOrBlank()) TvActionButton(Icons.Default.Delete, "HAPUS", Color.Transparent, UrgentRed, true, enabled = !isProcessing) {
                                        settings.videoUri?.let { MediaPersistenceHelper.deleteFile(it) }; onUpdate(settings.copy(videoUri = null, videoEnabled = false))
                                    }
                                }
                            }
                        }
                    }
                    TvSlider(
                        label = "Durasi Tampil Video",
                        value = settings.videoModeIntervalMinutes.toFloat(),
                        onValueChange = { onUpdate(settings.copy(videoModeIntervalMinutes = it.toInt())) },
                        valueRange = 1f..60f, steps = 58, unit = " menit"
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("📷 FOTO KEGIATAN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                TvToggle(
                    label = "Aktifkan Foto",
                    description = "Tampilkan slideshow foto kegiatan di layar TV.",
                    isChecked = settings.photoSlideshowEnabled,
                    onToggle = { onUpdate(settings.copy(photoSlideshowEnabled = it)) },
                    enabled = isGalleryEnabled && !isProcessing
                )

                if (settings.photoSlideshowEnabled && isGalleryEnabled) {
                    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF091620)).border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Daftar Foto (${settings.photoSlideshowUris.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                            if (settings.photoSlideshowUris.isNotEmpty()) TvActionButton(Icons.Default.DeleteSweep, "HAPUS SEMUA", Color.Transparent, UrgentRed, true, enabled = !isProcessing) {
                                settings.photoSlideshowUris.forEach { MediaPersistenceHelper.deleteFile(it) }; onUpdate(settings.copy(photoSlideshowUris = emptyList()))
                            }
                        }
                        if (settings.photoSlideshowUris.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                itemsIndexed(settings.photoSlideshowUris) { index, uri ->
                                    val isMissing = !File(uri).exists()
                                    Box(modifier = Modifier.size(140.dp, 90.dp).clip(RoundedCornerShape(10.dp)).background(Color.Black).border(1.dp, if(isMissing) UrgentRed else IslamicGold, RoundedCornerShape(10.dp))) {
                                        if (!isMissing) AsyncImage(uri, "Foto", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                        else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Warning, null, tint = UrgentRed) }
                                        Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(28.dp).clip(RoundedCornerShape(14.dp)).background(if(isProcessing) Color.Gray else UrgentRed).clickable(enabled = !isProcessing) {
                                            val newList = settings.photoSlideshowUris.toMutableList(); MediaPersistenceHelper.deleteFile(newList.removeAt(index)); onUpdate(settings.copy(photoSlideshowUris = newList))
                                        }, contentAlignment = Alignment.Center) { Icon(Icons.Default.Delete, "Hapus", tint = Color.White, modifier = Modifier.size(16.dp)) }
                                        if (index > 0) Box(modifier = Modifier.align(Alignment.BottomStart).padding(4.dp).size(24.dp).clip(RoundedCornerShape(12.dp)).background(if(isProcessing) Color.Gray else Color(0xAA000000)).clickable(enabled = !isProcessing) {
                                            val list = settings.photoSlideshowUris.toMutableList(); list.add(index - 1, list.removeAt(index)); onUpdate(settings.copy(photoSlideshowUris = list))
                                        }, contentAlignment = Alignment.Center) { Icon(Icons.Default.ArrowBack, "Kiri", tint = Color.White, modifier = Modifier.size(14.dp)) }
                                    }
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TvActionButton(Icons.Default.PhotoLibrary, "GALERI", IslamicGold, Color(0xFF09141D), enabled = !isProcessing) { photoGalleryLauncher.launch("image/*") }
                            TvActionButton(Icons.Default.Add, "FILE", IslamicGold, Color(0xFF09141D), enabled = !isProcessing) { showPhotoPicker = true }
                        }
                    }
                    TvSlider(
                        label = "Durasi per Foto",
                        value = settings.photoSlideshowIntervalSeconds.toFloat(),
                        onValueChange = { onUpdate(settings.copy(photoSlideshowIntervalSeconds = it.toInt())) },
                        valueRange = 3f..30f, steps = 27, unit = " detik"
                    )
                    val totalDetik = settings.photoSlideshowUris.size * settings.photoSlideshowIntervalSeconds
                    Text("ℹ️ Total waktu putar: ${totalDetik / 60} Menit ${totalDetik % 60} Detik (untuk ${settings.photoSlideshowUris.size} foto)", fontSize = 12.sp, color = TextSecondary)
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🔄 SMART SWITCH (Rotasi Cerdas)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                TvToggle(
                    label = "Tampilkan Layar Default",
                    description = "Layar bergantian: Video -> Foto -> Layar Default.",
                    isChecked = settings.smartSwitchEnabled,
                    onToggle = { onUpdate(settings.copy(smartSwitchEnabled = it)) },
                    enabled = isGalleryEnabled && !isProcessing
                )
                if (settings.smartSwitchEnabled && isGalleryEnabled) TvSlider(
                    label = "Durasi Layar Default",
                    value = settings.normalModeDurationMinutes.toFloat(),
                    onValueChange = { onUpdate(settings.copy(normalModeDurationMinutes = it.toInt())) },
                    valueRange = 1f..30f, steps = 28, unit = " menit"
                )
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.AspectRatio, null, tint = IslamicGold, modifier = Modifier.size(22.dp)); Spacer(modifier = Modifier.width(10.dp)); Text("PENGATURAN UKURAN FRAME", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight) }
                FrameModeOption("POTONG (Crop)", "Penuhi frame, potong sisi berlebih.", "✂️", settings.videoFrameScale == "POTONG", isGalleryEnabled && !isProcessing) { onUpdate(settings.copy(videoFrameScale = "POTONG")) }
                FrameModeOption("PAS (Fit)", "Tampil utuh, ada ruang hitam.", "📺", settings.videoFrameScale == "PAS", isGalleryEnabled && !isProcessing) { onUpdate(settings.copy(videoFrameScale = "PAS")) }
                FrameModeOption("ZOOM (Fill)", "Diperbesar penuhi frame, tengah fokus.", "🔍", settings.videoFrameScale == "ZOOM", isGalleryEnabled && !isProcessing) { onUpdate(settings.copy(videoFrameScale = "ZOOM")) }
                FrameModeOption("FULL (Fullscreen)", "Menutupi seluruh layar (panel kiri hilang).", "🖥️", settings.videoFrameScale == "FULL", isGalleryEnabled && !isProcessing) { onUpdate(settings.copy(videoFrameScale = "FULL")) }
                FrameModeOption("FIT (Stretch)", "Dipaksa memenuhi frame (bisa distorsi).", "📐", settings.videoFrameScale == "FIT", isGalleryEnabled && !isProcessing) { onUpdate(settings.copy(videoFrameScale = "FIT")) }
            }
        }
    }

    if (showVideoPicker) VideoFilePickerDialog(FilePickerMode.VIDEO, "Pilih File Video", { showVideoPicker = false; processVideoFile(it) }, { showVideoPicker = false })
    if (showPhotoPicker) VideoFilePickerDialog(FilePickerMode.IMAGE, "Pilih File Foto", { showPhotoPicker = false; processPhotoFile(it) }, { showPhotoPicker = false })
}

@Composable
private fun FrameModeOption(title: String, desc: String, emoji: String, isSelected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val borderWidth by animateDpAsState(if (isFocused) 4.dp else if (isSelected) 2.dp else 1.dp, label = "border")
    val scale by animateFloatAsState(if (isFocused) 1.02f else 1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f), label = "scale")
    Box(modifier = Modifier.fillMaxWidth().scale(scale).clip(RoundedCornerShape(10.dp)).background(if (isSelected) Color(0x33FFD700) else if (isFocused) Color(0x22FFD700) else Color(0x22000000)).border(borderWidth, if (isFocused) Color(0xFFFFE44D) else if (isSelected) IslamicGold else Color(0x22FFFFFF), RoundedCornerShape(10.dp)).onFocusChanged { isFocused = it.isFocused }.focusable(enabled).clickable(enabled = enabled) { onClick() }.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(if (isSelected) IslamicGold.copy(alpha = 0.25f) else Color(0x22FFFFFF)), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 18.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) IslamicGoldLight else if (isFocused) Color(0xFFFFE44D) else TextPrimary); Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp) }
            RadioButton(selected = isSelected, onClick = onClick, enabled = enabled, colors = RadioButtonDefaults.colors(selectedColor = IslamicGold))
        }
    }
}

@Composable
private fun TvActionButton(icon: ImageVector, label: String, bg: Color, tc: Color, isOutlined: Boolean = false, enabled: Boolean = true, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val borderWidth by animateDpAsState(if (isFocused) 4.dp else if (isOutlined) 1.5.dp else 0.dp, label = "border")
    Row(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(if (isOutlined) Color.Transparent else if (!enabled) bg.copy(alpha=0.4f) else bg).border(borderWidth, if (isFocused) Color(0xFFFFE44D) else if (isOutlined) tc.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(10.dp)).onFocusChanged { isFocused = it.isFocused }.focusable(enabled).clickable(enabled = enabled) { onClick() }.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, label, tint = if (!enabled) tc.copy(alpha=0.5f) else tc, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!enabled) tc.copy(alpha=0.5f) else tc)
    }
}
