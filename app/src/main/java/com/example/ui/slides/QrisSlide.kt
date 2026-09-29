package com.example.ui.slides

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

/**
 * Slide QRIS Infaq — menampilkan:
 * - QRIS code besar (dari foto yang di-upload)
 * - Ajakan berinfaq
 * - Info rekening bank (opsional)
 * - Animasi pulse halus pada QRIS
 */
@Composable
fun QrisSlide(
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    // Pulse animation untuk QRIS
    val infiniteTransition = rememberInfiniteTransition(label = "qris_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "qris_pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B3A2E),
                        Color(0xFF0D1B2A)
                    )
                )
            )
            .padding(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ============ KIRI: AJAKAN INFAQ ============
            Column(
                modifier = Modifier.weight(0.55f),
                verticalArrangement = Arrangement.Center
            ) {
                // Logo/icon infaq
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IslamicGold.copy(alpha = 0.2f))
                        .border(2.dp, IslamicGold, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "SALURKAN",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    color = TextSecondary,
                    letterSpacing = 4.sp
                )

                Text(
                    text = "INFAQMU",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = IslamicGoldLight,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "LEWAT QRIS",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreen,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Garis emas
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(3.dp)
                        .background(IslamicGold)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Jangan lewatkan keberkahan hari tanpa berinfaq.\nSekarang infaq lebih mudah, yuk infaq sekarang!",
                    fontSize = 18.sp,
                    color = TextPrimary,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Info rekening bank (kalau ada)
                if (settings.bankAccountNumber.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = settings.bankName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldLight
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = settings.bankAccountNumber,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "a.n ${settings.bankAccountHolder}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            // ============ KANAN: QRIS CODE ============
            Column(
                modifier = Modifier.weight(0.45f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(360.dp)
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(4.dp, IslamicGold, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!settings.qrisPhotoUri.isNullOrBlank()) {
                        AsyncImage(
                            model = settings.qrisPhotoUri,
                            contentDescription = "QRIS Infaq",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Placeholder QRIS
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = Color(0xFF0A1822),
                                modifier = Modifier.size(120.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "QRIS belum di-upload",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF555555)
                            )
                            Text(
                                text = "Upload di Pengaturan → QRIS",
                                fontSize = 12.sp,
                                color = Color(0xFF888888)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Label di bawah QRIS
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(IslamicGold)
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = Color(0xFF09141D),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SCAN DI SINI UNTUK BERINFAQ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF09141D),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Satu QRIS untuk semua • Cek aplikasi penyelenggara di www.aspi-indonesia.or.id",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
