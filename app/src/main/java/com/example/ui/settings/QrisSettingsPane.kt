package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Visibility
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
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import kotlin.math.roundToInt

@Composable
fun QrisSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    onTestQrisFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val qrisPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdate(settings.copy(qrisPhotoUri = uri.toString()))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Donasi QRIS & Rekening Kas Masjid",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // --- PREVIEW & PICKER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QRIS Image Box
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, IslamicGold, RoundedCornerShape(12.dp))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!settings.qrisPhotoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = settings.qrisPhotoUri,
                        contentDescription = "Preview QRIS",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = Color(0xFF0A1822),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "Belum ada QRIS",
                            fontSize = 11.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Foto Kode QRIS Donasi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Unggah barcode QRIS masjid dari galeri perangkat atau penyimpanan internal.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { qrisPickerLauncher.launch("image/*") },
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

                    if (!settings.qrisPhotoUri.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { onUpdate(settings.copy(qrisPhotoUri = null)) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(UrgentRed)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "HAPUS", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onTestQrisFocus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGreen,
                            contentColor = Color(0xFF09141D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "UJI TAMPILAN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SLIDERS 0-30 MENIT ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Pengaturan Interval & Durasi Tayang Layar Penuh QRIS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            // Slider 1: Interval (0 - 30 Menit)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interval Penayangan Otomatis:",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = if (settings.qrisIntervalMinutes == 0) "Nonaktif (Manual)" else "${settings.qrisIntervalMinutes} Menit Sekali",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGold
                    )
                }
                Slider(
                    value = settings.qrisIntervalMinutes.toFloat(),
                    onValueChange = { onUpdate(settings.copy(qrisIntervalMinutes = it.roundToInt())) },
                    valueRange = 0f..30f,
                    steps = 30,
                    colors = SliderDefaults.colors(thumbColor = IslamicGold, activeTrackColor = IslamicGold)
                )
            }

            // Slider 2: Durasi Tampil (5 - 60 Detik)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lama Tampil Setiap Sesi:",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${settings.qrisDisplayDurationSeconds} Detik",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreen
                    )
                }
                Slider(
                    value = settings.qrisDisplayDurationSeconds.toFloat(),
                    onValueChange = { onUpdate(settings.copy(qrisDisplayDurationSeconds = it.roundToInt())) },
                    valueRange = 5f..60f,
                    steps = 55,
                    colors = SliderDefaults.colors(thumbColor = IslamicGreen, activeTrackColor = IslamicGreen)
                )
            }
        }

        // --- BANK ACCOUNT DETAILS ---
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
                text = "Informasi Nomor Rekening Masjid:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = settings.bankName,
                    onValueChange = { onUpdate(settings.copy(bankName = it)) },
                    label = { Text("Nama Bank (cth. BSI / Bank Syariah Indonesia)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = settings.bankAccountNumber,
                    onValueChange = { onUpdate(settings.copy(bankAccountNumber = it)) },
                    label = { Text("Nomor Rekening") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )
            }

            OutlinedTextField(
                value = settings.bankAccountHolder,
                onValueChange = { onUpdate(settings.copy(bankAccountHolder = it)) },
                label = { Text("Atas Nama Rekening (cth. DKM MASJID AL-IKHLAS)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IslamicGold,
                    unfocusedBorderColor = Color(0x44FFFFFF)
                )
            )
        }
    }
}
