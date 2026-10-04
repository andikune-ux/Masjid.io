package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.ui.components.MasjidVideoPlayer
import com.example.ui.components.TvSlider
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed

@Composable
fun VideoSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    // Picker Video (single)
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdate(
                settings.copy(
                    videoUri = uri.toString(),
                    videoEnabled = true
                )
            )
        }
    }

    // Picker Foto (multiple) untuk slideshow
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newUris = settings.photoSlideshowUris + uris.map { it.toString() }
            onUpdate(
                settings.copy(
                    photoSlideshowUris = newUris,
                    photoSlideshowEnabled = true
                )
            )
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
        // 2. VIDEO PICKER
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
                            "Video terpilih"
                        else "Belum ada file video yang dipilih.",
                        fontSize = 12.sp,
                        color = if (!settings.videoUri.isNullOrBlank()) IslamicGreen else TextSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TvActionButton(
                            icon = Icons.Default.VideoFile,
                            label = "PILIH VIDEO",
                            backgroundColor = IslamicGold,
                            textColor = Color(0xFF09141D),
                            onClick = { videoPickerLauncher.launch("video/*") }
                        )

                        if (!settings.videoUri.isNullOrBlank()) {
                            TvActionButton(
                                icon = Icons.Default.Delete,
                                label = "HAPUS",
                                backgroundColor = Color.Transparent,
                                textColor = UrgentRed,
                                isOutlined = true,
                                onClick = {
                                    onUpdate(settings.copy(videoUri = null, videoEnabled = false))
                                }
                            )
                        }
                    }
                }
            }
        }
        // ============================================================
// 3. MODE TAMPILAN (Split / Smart Fullscreen)
// ============================================================
Column(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFF091620))
        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
        .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
) {
    Text(
        text = "Pilihan Tata Letak Video di Layar TV:",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = IslamicGoldLight
    )

    val isSplit = !settings.videoSmartFullscreen
    RadioOption(
        title = "Mode Panel Kanan (Split Screen)",
        description = "Video tayang di sisi kanan area tengah. Jam digital menyesuaikan tata letak.",
        isSelected = isSplit,
        onClick = { onUpdate(settings.copy(videoSmartFullscreen = false)) }
    )

    val isSmartFullscreen = settings.videoSmartFullscreen
    RadioOption(
        title = "Mode Cerdas Layar Penuh",
        description = "Video otomatis layar penuh saat waktu sholat masih >30 menit. Mendekati sholat, layar kembali ke tampilan masjid.",
        isSelected = isSmartFullscreen,
        onClick = { onUpdate(settings.copy(videoSmartFullscreen = true)) }
    )
}

// ============================================================
// 4. V1.04.420 BARU — PENGATURAN UKURAN FRAME
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
        description = "Video/foto ditarik & dipaksa memenuhi frame (bisa distorsi). Untuk video/foto dimensi aneh.",
        emojiLabel = "📐",
        isSelected = settings.videoFrameScale == "FIT",
        onClick = { onUpdate(settings.copy(videoFrameScale = "FIT")) }
    )
}

// ============================================================
// 5. V1.04.420 BARU — AUTO-SWITCH MODE
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
                        "Berjalan 24 jam, tidak mengganggu Mode Fokus Sholat & Slide Fullscreen.",
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

        // Info box
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

        // WARNING kalau belum ada media
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
        // 6. FOTO SLIDESHOW
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

        // Kelola Foto
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
                        onClick = { onUpdate(settings.copy(photoSlideshowUris = emptyList())) }
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
                                        newList.removeAt(index)
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

            TvActionButton(
                icon = Icons.Default.AddPhotoAlternate,
                label = "TAMBAH FOTO",
                backgroundColor = IslamicGold,
                textColor = Color(0xFF09141D),
                onClick = { photoPickerLauncher.launch("image/*") }
            )
        }

        // Interval Ganti Foto
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

        // Info
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
                    text = "Foto slideshow akan tampil otomatis di posisi video (kanan atas layar) ketika:\n" +
                            "• Video TIDAK aktif\n" +
                            "• Foto slideshow AKTIF\n" +
                            "• Minimal ada 1 foto yang di-upload",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================

@Composable
private fun RadioOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else if (isSelected) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "radio_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "radio_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 10.dp else 0.dp,
        animationSpec = tween(200),
        label = "radio_shadow"
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
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = IslamicGold)
            )
            Spacer(modifier = Modifier.width(10.dp))
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
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
