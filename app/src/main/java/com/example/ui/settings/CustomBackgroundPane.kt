package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
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
import com.example.data.model.BackgroundMode
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed

@Composable
fun CustomBackgroundPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdate(
                settings.copy(
                    customBackgroundUri = uri.toString(),
                    backgroundMode = BackgroundMode.CUSTOM
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
            text = "Pengaturan Latar Belakang (Background) Layar TV",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // Custom Background Picker Card
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
                text = "Gambar Background Sendiri (Dari Galeri / Penyimpanan Internal):",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preview
                Box(
                    modifier = Modifier
                        .size(width = 160.dp, height = 90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black)
                        .border(1.5.dp, IslamicGold, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!settings.customBackgroundUri.isNullOrBlank()) {
                        AsyncImage(
                            model = settings.customBackgroundUri,
                            contentDescription = "Background Kustom",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(text = "Belum Ada Gambar", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Unggah foto resolusi tinggi (1920x1080 Full HD atau 4K) interior masjid, arsitektur kubah, atau motif kaligrafi pilihan Anda.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { bgPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IslamicGold,
                                contentColor = Color(0xFF09141D)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "PILIH DARI GALERI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (!settings.customBackgroundUri.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = {
                                    onUpdate(
                                        settings.copy(
                                            customBackgroundUri = null,
                                            backgroundMode = BackgroundMode.NATURE
                                        )
                                    )
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(UrgentRed)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "HAPUS", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Preset Background Themes
        Text(
            text = "Pilihan Tema Latar Bawaan:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        val presetList = listOf(
            Triple(
                BackgroundMode.NATURE,
                "Langit Dinamis Real-Time (Cerdas)",
                "Menyesuaikan waktu asli matahari: Siang hari (12:00-15:30) langit biru cerah alami, Ashar keemasan hangat, Maghrib senja syahdu, Malam gelap bertabur bintang."
            ),
            Triple(
                BackgroundMode.KABAH,
                "Ka'bah Al-Mukarramah",
                "Gradien malam Ka'bah dengan kilau ornamen Kiswah emas yang megah."
            ),
            Triple(
                BackgroundMode.EMERALD_GEOMETRIC,
                "Emerald Geometris Arabesque",
                "Pola arabesque islami mewah bernuansa hijau zamrud tua dan garis emas."
            ),
            Triple(
                BackgroundMode.CUSTOM,
                "Gambar Kustom Pengguna",
                if (!settings.customBackgroundUri.isNullOrBlank()) "Menggunakan foto dari galeri yang telah Anda pilih." else "Pilih foto dari galeri pada menu di atas terlebih dahulu."
            )
        )

        for ((mode, title, desc) in presetList) {
            val isSelected = settings.backgroundMode == mode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0x33FFD700) else Color(0xFF091620))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) IslamicGold else Color(0x22FFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onUpdate(settings.copy(backgroundMode = mode)) }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) IslamicGoldLight else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Terpilih",
                            tint = IslamicGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
