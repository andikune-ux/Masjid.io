package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.MasjidVideoPlayer
import com.example.ui.components.TvSlider
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
            text = "Video Kegiatan & Rutinitas Masjid (Bebas Durasi & Ukuran)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // ============================================================
        // 1. VIDEO SECTION (existing)
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
                    color = TextSecondary
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
                            "Video terpilih: ${settings.videoUri?.takeLast(30)}"
                        else "Belum ada file video yang dipilih.",
                        fontSize = 12.sp,
                        color = if (!settings.videoUri.isNullOrBlank()) IslamicGreen else TextSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { videoPickerLauncher.launch("video/*") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IslamicGold,
                                contentColor = Color(0xFF09141D)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoFile,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PILIH DARI PENYIMPANAN",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!settings.videoUri.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = {
                                    onUpdate(settings.copy(videoUri = null, videoEnabled = false))
                                },
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
        }

        // ============================================================
        // 2. MODE TAMPILAN CERDAS
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSplit) Color(0x33FFD700) else Color(0x22000000))
                    .border(
                        1.dp,
                        if (isSplit) IslamicGold else Color(0x22FFFFFF),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onUpdate(settings.copy(videoSmartFullscreen = false)) }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSplit,
                        onClick = { onUpdate(settings.copy(videoSmartFullscreen = false)) },
                        colors = RadioButtonDefaults.colors(selectedColor = IslamicGold)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mode Panel Kanan (Split Screen Harmonis)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSplit) IslamicGoldLight else TextPrimary
                        )
                        Text(
                            text = "Video tayang di sisi kanan area tengah. Jam digital dan info masjid otomatis menyesuaikan tata letak agar rapi dan tidak saling bertabrakan.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            val isSmartFullscreen = settings.videoSmartFullscreen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSmartFullscreen) Color(0x33FFD700) else Color(0x22000000))
                    .border(
                        1.dp,
                        if (isSmartFullscreen) IslamicGold else Color(0x22FFFFFF),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onUpdate(settings.copy(videoSmartFullscreen = true)) }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSmartFullscreen,
                        onClick = { onUpdate(settings.copy(videoSmartFullscreen = true)) },
                        colors = RadioButtonDefaults.colors(selectedColor = IslamicGold)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mode Cerdas Layar Penuh (Smart Fullscreen)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSmartFullscreen) IslamicGoldLight else TextPrimary
                        )
                        Text(
                            text = "Video otomatis diputar layar penuh saat waktu sholat masih lama (> 30 menit). Saat mendekati sholat (< 30 menit), layar otomatis kembali ke tampilan masjid.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
        
        // ============================================================
        // 3. FOTO SLIDESHOW (NEW)
        // ============================================================
        Text(
            text = "Foto Kegiatan Masjid (Slideshow)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // Master Switch Foto
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
                    text = "Menampilkan foto kegiatan masjid secara bergantian. Foto akan tampil di posisi video kalau video tidak aktif.",
                    fontSize = 12.sp,
                    color = TextSecondary
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
                    OutlinedButton(
                        onClick = { onUpdate(settings.copy(photoSlideshowUris = emptyList())) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(UrgentRed)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "HAPUS SEMUA", fontSize = 12.sp)
                    }
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

                            // Nomor urut
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

                            // Tombol hapus
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = Color(0xFF09141D)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TAMBAH FOTO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
