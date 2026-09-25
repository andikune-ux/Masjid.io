package com.example.ui.focus

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerId
import com.example.ui.components.ArabesquePattern
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun PrayerFocusOverlay(
    prayerId: PrayerId,
    prayerTimeFormatted: String,
    totalDurationMinutes: Int = 30,
    iqamahWaitMinutes: Int = 10,
    qobliyahWaitMinutes: Int = 5,
    onDismiss: () -> Unit
) {
    var elapsedSeconds by remember { mutableStateOf(0) }
    val safeTotalMinutes = totalDurationMinutes.coerceIn(1, 60)
    val totalSeconds = safeTotalMinutes * 60

    LaunchedEffect(Unit) {
        while (elapsedSeconds < totalSeconds) {
            delay(1000)
            elapsedSeconds++
        }
        onDismiss()
    }

    val iqamahEndSeconds = (iqamahWaitMinutes.coerceIn(0, 30) * 60).coerceAtLeast(60)
    val qobliyahEndSeconds = iqamahEndSeconds + (qobliyahWaitMinutes.coerceIn(0, 30) * 60)

    val currentPhase = when {
        elapsedSeconds < iqamahEndSeconds -> 1 // Fase 1: Adzan & Menanti Iqamah
        elapsedSeconds < qobliyahEndSeconds -> 2 // Fase 2: Qobliyah & Rapatkan Shaf
        else -> 3 // Fase 3: Sholat Fardhu
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF030A0E),
                        Color(0xFF081822),
                        Color(0xFF040D12)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Geometric arabesque background
        ArabesquePattern(lineColor = Color(0x18FFD700))

        // Exit button on top-right (for takmir)
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0x33000000))
                .border(1.dp, Color(0x44FFFFFF), CircleShape)
                .testTag("btn_close_focus")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Keluar Mode Fokus",
                tint = TextSecondary
            )
        }

        // Main Focus Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x55091722))
                    .border(1.dp, Color(0x33FFD700), RoundedCornerShape(18.dp))
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "MODE FOKUS SHOLAT — FASE $currentPhase DARI 3",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = IslamicGoldLight
                )
            }

            // Phase Content Switcher
            AnimatedContent(
                targetState = currentPhase,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "phase_content"
            ) { phase ->
                when (phase) {
                    1 -> {
                        // FASE 1: ADZAN & MENUJU IQAMAH
                        val remainingIqamahSeconds = (iqamahEndSeconds - elapsedSeconds).coerceAtLeast(0)
                        val mm = remainingIqamahSeconds / 60
                        val ss = remainingIqamahSeconds % 60
                        val iqamahTimeStr = String.format("%02d:%02d", mm, ss)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Text(
                                text = "🕌  WAKTU ${prayerId.displayName.uppercase()} TELAH TIBA",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = IslamicGoldLight
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "$prayerTimeFormatted WIB",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Iqamah Countdown Box
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x44FFD700))
                                    .border(2.dp, IslamicGold, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = IslamicGoldLight,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Iqamah dalam $iqamahTimeStr",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Adab & Phone Reminder Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x55E53935))
                                        .border(1.dp, Color(0xFFE53935), RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "MOHON HENINGKAN NADA DERING HP ANDA",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x550C3224))
                                        .border(1.dp, IslamicGreen, RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ViewHeadline,
                                            contentDescription = null,
                                            tint = IslamicGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "RAPATKAN & LURUSKAN SHAF SHOLAT",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IslamicGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // FASE 2: QOBLIYAH & PERSIAPAN SHAF
                        val remainingQobliyahSeconds = (qobliyahEndSeconds - elapsedSeconds).coerceAtLeast(0)
                        val mm = remainingQobliyahSeconds / 60
                        val ss = remainingQobliyahSeconds % 60
                        val qobliyahTimeStr = String.format("%02d:%02d", mm, ss)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Text(
                                text = "⏱️  SHOLAT SUNNAH QOBLIYAH & LURUSKAN SHAF",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = IslamicGoldLight
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = qobliyahTimeStr,
                                style = TextStyle(
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif,
                                    color = IslamicGreen,
                                    shadow = Shadow(
                                        color = IslamicGreen.copy(alpha = 0.5f),
                                        offset = Offset(0f, 4f),
                                        blurRadius = 14f
                                    )
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Rapatkan Shaf Card
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0x770D1E2C))
                                    .border(1.5.dp, IslamicGold, RoundedCornerShape(16.dp))
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "سَوُّوا صُفُوفَكُمْ فَإِنَّ تَسْوِيَةَ الصَّفِّ مِنْ تَمَامِ الصَّلَاةِ",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 38.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "\"Luruskanlah shaf-shaf kalian, karena sesungguhnya meluruskan shaf termasuk kesempurnaan sholat.\" (HR. Bukhari & Muslim)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = IslamicGoldLight,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        // FASE 3: SHOLAT FARDHU KHUSYUK
                        val returnSeconds = (totalSeconds - elapsedSeconds).coerceAtLeast(0)
                        val mm = returnSeconds / 60
                        val ss = returnSeconds % 60
                        val returnTimeStr = String.format("%02d:%02d", mm, ss)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Text(
                                text = "🕌  DIRIKAN SHOLAT FARDHU ${prayerId.displayName.uppercase()}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = IslamicGoldLight
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَوْقُوتًا",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "\"Sesungguhnya sholat itu adalah kewajiban yang ditentukan waktunya atas orang-orang yang beriman.\" (QS. An-Nisa: 103)",
                                fontSize = 16.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Quiet Reminder Box
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x44000000))
                                    .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "Harap hening dan khusyuk • Kembali ke layar utama dalam $returnTimeStr",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = IslamicGoldLight
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Progress Indicator across total duration
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { (elapsedSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = IslamicGold,
                    trackColor = Color(0x33FFFFFF)
                )
                Spacer(modifier = Modifier.height(4.dp))
                val totalRemaining = (totalSeconds - elapsedSeconds).coerceAtLeast(0)
                Text(
                    text = "Mode fokus berlangsung $safeTotalMinutes menit (${totalRemaining / 60}m ${totalRemaining % 60}s tersisa)",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }
        }
    }
}
