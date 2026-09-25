package com.example.ui.focus

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun QRISFocusOverlay(
    settings: AppSettings,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationSeconds = settings.qrisDisplayDurationSeconds.coerceAtLeast(5)
    var secondsRemaining by remember { mutableStateOf(durationSeconds) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xF0051118))
            .clickable { onDismiss() }
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0C241E), Color(0xFF061410))
                    )
                )
                .border(2.dp, IslamicGold, RoundedCornerShape(24.dp))
                .padding(28.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [Left Side] QRIS Code Image / Banner
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, IslamicGold, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!settings.qrisPhotoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = settings.qrisPhotoUri,
                        contentDescription = "QRIS Donasi Infaq",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QRIS Placeholder",
                            tint = Color(0xFF0A1D16),
                            modifier = Modifier.size(160.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "QRIS DONASI MASJID",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A1D16)
                        )
                        Text(
                            text = "Unggah gambar QRIS di menu Pengaturan",
                            fontSize = 13.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            // [Right Side] Information & Bank Accounts
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Header
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INFAQ & SHODAQOH DIGITAL",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = IslamicGold
                        )

                        // Countdown to dismiss badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33FFFFFF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Menutup dalam ${secondsRemaining}s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = settings.mosqueName,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = settings.mosqueAddress,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // Bank details card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x66000000))
                        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "REKENING RESMI MASJID",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${settings.bankName}: ${settings.bankAccountNumber}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "a.n. ${settings.bankAccountHolder}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = IslamicGreen
                    )
                }

                // Inspirational Verse / Hadits
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33091B16))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "\"Perumpamaan orang yang menginfakkan hartanya di jalan Allah seperti sebutir biji yang menumbuhkan tujuh tangkai, pada setiap tangkai ada seratus biji.\" (QS. Al-Baqarah: 261)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimary.copy(alpha = 0.9f),
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Start
                    )
                }

                // Dismiss hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tekan Sembarang Tombol Remote untuk Kembali",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
