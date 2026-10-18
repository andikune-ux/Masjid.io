package dev.andikune.masjidio.ui.settings

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import dev.andikune.masjidio.util.MediaPersistenceHelper
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

    var showVideoPicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    
    // Cek apakah file video ada
    val isVideoMissing = remember(settings.videoUri) {
        !settings.videoUri.isNullOrBlank() && !File(settings.videoUri).exists()
    }

    fun processVideoFile(file: File) {
        scope.launch {
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context, sourceUri = file.absolutePath,
                    folder = MediaPersistenceHelper.FOLDER_VIDEO, fileNamePrefix = "video"
                )
            }
            if (localPath != null) {
                MediaPersistenceHelper.cleanupFolder(context, MediaPersistenceHelper.FOLDER_VIDEO, 3)
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
                    context = context, sourceUri = file.absolutePath,
                    folder = MediaPersistenceHelper.FOLDER_SLIDESHOW, fileNamePrefix = "foto"
                )
            }
            val newItem = localPath ?: file.absolutePath
            val newList = settings.photoSlideshowUris + newItem
            onUpdate(settings.copy(photoSlideshowUris = newList, photoSlideshowEnabled = true))
        }
    }

    val videoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val localPath = withContext(Dispatchers.IO) {
                    MediaPersistenceHelper.copyVideoToPermanent(context, uri.toString())
                }
                val newItem = localPath ?: uri.toString()
                onUpdate(settings.copy(videoUri = newItem, videoEnabled = true))
            }
        }
    }

    val photoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val localPaths = withContext(Dispatchers.IO) {
                    uris.mapNotNull { uri ->
                        MediaPersistenceHelper.copyToPermanent(
                            context = context, sourceUri = uri.toString(),
                            folder = MediaPersistenceHelper.FOLDER_SLIDESHOW, fileNamePrefix = "foto"
                        )
                    }
                }
                if (localPaths.isNotEmpty()) {
                    val newUris = settings.photoSlideshowUris + localPaths
                    onUpdate(settings.copy(photoSlideshowUris = newUris, photoSlideshowEnabled = true))
                }
            }
        }
    }

    val isGalleryEnabled = settings.galleryMasterEnabled

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Galeri Kegiatan Masjid",
            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight
        )
        Text(
            text = "Atur tampilan video dan foto yang akan diputar di layar TV.",
            fontSize = 13.sp, color = TextSecondary
        )

        // ============================================================
        // MASTER SWITCH
        // ============================================================
        TvToggle(
            label = "Aktifkan Galeri Kegiatan",
            description = "Jika dimatikan, TV hanya menampilkan Layar Default / Background.",
            isChecked = isGalleryEnabled,
            onToggle = { onUpdate(settings.copy(galleryMasterEnabled = it)) }
        )

        // ============================================================
        // KONTEN (REDUP JIKA MASTER OFF)
        // ============================================================
        Column(
            modifier = Modifier
                .alpha(if (isGalleryEnabled) 1f else 0.35f)
                .focusable(isGalleryEnabled), // Kunci fokus jika mati
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ============================================================
            // BAGIAN VIDEO
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "📹 VIDEO KEGIATAN",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight
                )
                
                TvToggle(
                    label = "Aktifkan Video",
                    description = "Putar video kegiatan di layar TV.",
                    isChecked = settings.videoEnabled,
                    onToggle = { onUpdate(settings.copy(videoEnabled = it)) }
                )

                if (settings.videoEnabled) {
                    // Kotak Input Video
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF091620))
                            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("File Video Masjid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                        
                        if (isVideoMissing) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = UrgentRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("⚠️ File video terhapus atau tidak ditemukan.", fontSize = 12.sp, color = UrgentRed)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(width = 160.dp, height = 90.dp)
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
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Belum ada video", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TvActionButton(
                                        icon = Icons.Default.PhotoLibrary, label = "GALERI",
                                        backgroundColor = IslamicGold, textColor = Color(0xFF09141D),
                                        onClick = { videoGalleryLauncher.launch("video/*") }
                                    )
                                    TvActionButton(
                                        icon = Icons.Default.FolderOpen, label = "FILE",
                                        backgroundColor = IslamicGold, textColor = Color(0xFF09141D),
                                        onClick = { showVideoPicker = true }
                                    )
                                    if (!settings.videoUri.isNullOrBlank()) {
                                        TvActionButton(
                                            icon = Icons.Default.Delete, label = "HAPUS",
                                            backgroundColor = Color.Transparent, textColor = UrgentRed, isOutlined = true,
                                            onClick = {
                                                settings.videoUri?.let { MediaPersistenceHelper.deleteFile(it) }
                                                onUpdate(settings.copy(videoUri = null, videoEnabled = false))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Slider Durasi Video
                    TvSlider(
                        label = "Durasi Tampil Video",
                        value = settings.videoModeIntervalMinutes.toFloat(),
                        onValueChange = { onUpdate(settings.copy(videoModeIntervalMinutes = it.toInt())) },
                        valueRange = 1f..60f, steps = 58, unit = " menit"
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))
                        // ============================================================
            // BAGIAN FOTO
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "📷 FOTO KEGIATAN",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight
                )
                
                TvToggle(
                    label = "Aktifkan Foto",
                    description = "Tampilkan slideshow foto kegiatan di layar TV.",
                    isChecked = settings.photoSlideshowEnabled,
                    onToggle = { onUpdate(settings.copy(photoSlideshowEnabled = it)) }
                )

                if (settings.photoSlideshowEnabled) {
                    // Kotak Input Foto
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF091620))
                            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Daftar Foto (${settings.photoSlideshowUris.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                            if (settings.photoSlideshowUris.isNotEmpty()) {
                                TvActionButton(
                                    icon = Icons.Default.DeleteSweep, label = "HAPUS SEMUA",
                                    backgroundColor = Color.Transparent, textColor = UrgentRed, isOutlined = true,
                                    onClick = {
                                        settings.photoSlideshowUris.forEach { MediaPersistenceHelper.deleteFile(it) }
                                        onUpdate(settings.copy(photoSlideshowUris = emptyList()))
                                    }
                                )
                            }
                        }

                        if (settings.photoSlideshowUris.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                itemsIndexed(settings.photoSlideshowUris) { index, uri ->
                                    val isMissing = !File(uri).exists()
                                    Box(
                                        modifier = Modifier.size(width = 140.dp, height = 90.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.Black)
                                            .border(1.dp, if(isMissing) UrgentRed else IslamicGold, RoundedCornerShape(10.dp))
                                    ) {
                                        if (!isMissing) {
                                            AsyncImage(
                                                model = uri, contentDescription = "Foto ${index + 1}",
                                                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Warning, null, tint = UrgentRed)
                                            }
                                        }
                                        
                                        // Tombol Hapus (X)
                                        Box(
                                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(28.dp)
                                                .clip(RoundedCornerShape(14.dp)).background(UrgentRed)
                                                .clickable {
                                                    val newList = settings.photoSlideshowUris.toMutableList()
                                                    val removed = newList.removeAt(index)
                                                    MediaPersistenceHelper.deleteFile(removed)
                                                    onUpdate(settings.copy(photoSlideshowUris = newList))
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Delete, "Hapus", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                        
                                        // Tombol Geser Kiri (jika bukan index 0)
                                        if (index > 0) {
                                            Box(
                                                modifier = Modifier.align(Alignment.BottomStart).padding(4.dp).size(24.dp)
                                                    .clip(RoundedCornerShape(12.dp)).background(Color(0xAA000000))
                                                    .clickable {
                                                        val list = settings.photoSlideshowUris.toMutableList()
                                                        val item = list.removeAt(index)
                                                        list.add(index - 1, item)
                                                        onUpdate(settings.copy(photoSlideshowUris = list))
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.ArrowBack, "Kiri", tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TvActionButton(
                                icon = Icons.Default.PhotoLibrary, label = "GALERI",
                                backgroundColor = IslamicGold, textColor = Color(0xFF09141D),
                                onClick = { photoGalleryLauncher.launch("image/*") }
                            )
                            TvActionButton(
                                icon = Icons.Default.Add, label = "FILE",
                                backgroundColor = IslamicGold, textColor = Color(0xFF09141D),
                                onClick = { showPhotoPicker = true }
                            )
                        }
                    }

                    // Slider Durasi Per Foto
                    TvSlider(
                        label = "Durasi per Foto",
                        value = settings.photoSlideshowIntervalSeconds.toFloat(),
                        onValueChange = { onUpdate(settings.copy(photoSlideshowIntervalSeconds = it.toInt())) },
                        valueRange = 3f..30f, steps = 27, unit = " detik"
                    )
                    
                    // Hitung Total Durasi
                    val totalDetik = settings.photoSlideshowUris.size * settings.photoSlideshowIntervalSeconds
                    val menit = totalDetik / 60
                    val sisaDetik = totalDetik % 60
                    Text(
                        text = "ℹ️ Total waktu putar: $menit Menit $sisaDetik Detik (untuk ${settings.photoSlideshowUris.size} foto)",
                        fontSize = 12.sp, color = TextSecondary
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))

            // ============================================================
            // SMART SWITCH
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🔄 SMART SWITCH (Rotasi Cerdas)",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight
                )
                
                TvToggle(
                    label = "Tampilkan Layar Default",
                    description = "Jika aktif, layar akan bergantian: Video -> Foto -> Layar Default.",
                    isChecked = settings.smartSwitchEnabled,
                    onToggle = { onUpdate(settings.copy(smartSwitchEnabled = it)) }
                )

                if (settings.smartSwitchEnabled) {
                    TvSlider(
                        label = "Durasi Layar Default",
                        value = settings.normalModeDurationMinutes.toFloat(),
                        onValueChange = { onUpdate(settings.copy(normalModeDurationMinutes = it.toInt())) },
                        valueRange = 1f..30f, steps = 28, unit = " menit"
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))

            // ============================================================
            // PENGATURAN FRAME (Berlaku untuk Video & Foto)
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AspectRatio, null, tint = IslamicGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("PENGATURAN UKURAN FRAME", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                }

                FrameModeOption(title = "POTONG (Crop)", desc = "Penuhi frame, potong sisi berlebih.", emoji = "✂️", isSelected = settings.videoFrameScale == "POTONG") { onUpdate(settings.copy(videoFrameScale = "POTONG")) }
                FrameModeOption(title = "PAS (Fit)", desc = "Tampil utuh, ada ruang hitam.", emoji = "📺", isSelected = settings.videoFrameScale == "PAS") { onUpdate(settings.copy(videoFrameScale = "PAS")) }
                FrameModeOption(title = "ZOOM (Fill)", desc = "Diperbesar penuhi frame, tengah fokus.", emoji = "🔍", isSelected = settings.videoFrameScale == "ZOOM") { onUpdate(settings.copy(videoFrameScale = "ZOOM")) }
                FrameModeOption(title = "FULL (Fullscreen)", desc = "Menutupi seluruh layar (panel kiri hilang).", emoji = "🖥️", isSelected = settings.videoFrameScale == "FULL") { onUpdate(settings.copy(videoFrameScale = "FULL")) }
                FrameModeOption(title = "FIT (Stretch)", desc = "Dipaksa memenuhi frame (bisa distorsi).", emoji = "📐", isSelected = settings.videoFrameScale == "FIT") { onUpdate(settings.copy(videoFrameScale = "FIT")) }
            }
        }
    }

    // PICKER DIALOG CALLERS
    if (showVideoPicker) {
        VideoFilePickerDialog(
            mode = FilePickerMode.VIDEO, title = "Pilih File Video",
            onFileSelected = { file -> showVideoPicker = false; processVideoFile(file) },
            onDismiss = { showVideoPicker = false }
        )
    }

    if (showPhotoPicker) {
        VideoFilePickerDialog(
            mode = FilePickerMode.IMAGE, title = "Pilih File Foto",
            onFileSelected = { file -> showPhotoPicker = false; processPhotoFile(file) },
            onDismiss = { showPhotoPicker = false }
        )
    }
}

// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================
@Composable
private fun FrameModeOption(
    title: String, desc: String, emoji: String, isSelected: Boolean, onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderWidth by animateDpAsState(if (isFocused) 4.dp else if (isSelected) 2.dp else 1.dp, label = "border")
    val scale by animateFloatAsState(if (isFocused) 1.02f else 1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f), label = "scale")

    Box(
        modifier = Modifier.fillMaxWidth().scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0x33FFD700) else if (isFocused) Color(0x22FFD700) else Color(0x22000000))
            .border(borderWidth, if (isFocused) Color(0xFFFFE44D) else if (isSelected) IslamicGold else Color(0x22FFFFFF), RoundedCornerShape(10.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(if (isSelected) IslamicGold.copy(alpha = 0.25f) else Color(0x22FFFFFF)), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) IslamicGoldLight else if (isFocused) Color(0xFFFFE44D) else TextPrimary)
                Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
            }
            RadioButton(selected = isSelected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = IslamicGold))
        }
    }
}

@Composable
private fun TvActionButton(
    icon: ImageVector, label: String, backgroundColor: Color, textColor: Color, onClick: () -> Unit, isOutlined: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderWidth by animateDpAsState(if (isFocused) 4.dp else if (isOutlined) 1.5.dp else 0.dp, label = "border")
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isOutlined) Color.Transparent else backgroundColor)
            .border(borderWidth, if (isFocused) Color(0xFFFFE44D) else if (isOutlined) textColor.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(10.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, label, tint = textColor, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
