package com.example.ui.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.content.DzikirStore
import com.example.data.model.AppSettings
import com.example.data.model.PrayerId
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.UrgentRed
import kotlinx.coroutines.delay

private enum class FocusPhase { ADZAN, QOBLIYAH, FARDHU, DZIKIR }

@Composable
fun PrayerFocusOverlay(
    prayerId: PrayerId,
    prayerTimeFormatted: String,
    totalDurationMinutes: Int = 30,
    iqamahWaitMinutes: Int = 10,
    qobliyahWaitMinutes: Int = 5,
    settings: AppSettings? = null,
    onDismiss: () -> Unit
) {
    var currentPhase by remember { mutableStateOf(FocusPhase.ADZAN) }
    var secondsRemaining by remember { mutableIntStateOf(iqamahWaitMinutes * 60) }

    val phaseGradient = when (currentPhase) {
        FocusPhase.ADZAN -> listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF0D1B2A))
        FocusPhase.QOBLIYAH -> listOf(Color(0xFF1B3A2E), Color(0xFF2E5F44), Color(0xFF1B3A2E))
        FocusPhase.FARDHU -> listOf(Color(0xFF3A2E1B), Color(0xFF5F442E), Color(0xFF3A2E1B))
        FocusPhase.DZIKIR -> listOf(Color(0xFF1A0D2A), Color(0xFF2E1B3A), Color(0xFF1A0D2A))
    }

    val phaseNum = when (currentPhase) {
        FocusPhase.ADZAN -> 1
        FocusPhase.QOBLIYAH -> 2
        FocusPhase.FARDHU -> 3
        FocusPhase.DZIKIR -> 4
    }

    // Cek apakah dzikir aktif
    val dzikirEnabled = settings?.dzikirEnabled == true
    val totalPhases = if (dzikirEnabled) 4 else 3

    LaunchedEffect(currentPhase) {
        when (currentPhase) {
            FocusPhase.ADZAN -> secondsRemaining = iqamahWaitMinutes * 60
            FocusPhase.QOBLIYAH -> secondsRemaining = qobliyahWaitMinutes * 60
            FocusPhase.FARDHU -> secondsRemaining = (totalDurationMinutes - iqamahWaitMinutes - qobliyahWaitMinutes) * 60
            FocusPhase.DZIKIR -> secondsRemaining = settings?.dzikirDurationSeconds ?: 120
        }

        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
        }

        when (currentPhase) {
            FocusPhase.ADZAN -> currentPhase = FocusPhase.QOBLIYAH
            FocusPhase.QOBLIYAH -> currentPhase = FocusPhase.FARDHU
            FocusPhase.FARDHU -> {
                if (dzikirEnabled) currentPhase = FocusPhase.DZIKIR
                else onDismiss()
            }
            FocusPhase.DZIKIR -> onDismiss()
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val countdownText = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(phaseGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IslamicGold.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "MODE FOKUS SHOLAT  •  FASE $phaseNum DARI $totalPhases",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup Mode Fokus",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // KONTEN UTAMA
            AnimatedContent(
                targetState = currentPhase,
                transitionSpec = {
                    (slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(600)
                    ) + fadeIn(tween(600))) togetherWith
                    (slideOutVertically(
                        targetOffsetY = { -it / 4 },
                        animationSpec = tween(600)
                    ) + fadeOut(tween(600)))
                },
                modifier = Modifier.weight(1f),
                label = "focus_phase_transition"
            ) { phase ->
                when (phase) {
                    FocusPhase.ADZAN -> AdzanPhase(
                        prayerId = prayerId,
                        prayerTimeFormatted = prayerTimeFormatted,
                        countdownText = countdownText,
                        pulseScale = pulseScale
                    )
                    FocusPhase.QOBLIYAH -> QobliyahPhase(
                        prayerId = prayerId,
                        countdownText = countdownText,
                        pulseScale = pulseScale
                    )
                    FocusPhase.FARDHU -> FardhuPhase(prayerId = prayerId)
                    FocusPhase.DZIKIR -> DzikirPhase(
                        dzikirCountdown = countdownText,
                        pulseScale = pulseScale
                    )
                }
            }

            // PROGRESS BAR
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0x33FFFFFF))
            ) {
                val totalDurationSec = totalDurationMinutes * 60
                val elapsedSec = when (currentPhase) {
                    FocusPhase.ADZAN -> (iqamahWaitMinutes * 60) - secondsRemaining
                    FocusPhase.QOBLIYAH -> (iqamahWaitMinutes * 60) + ((qobliyahWaitMinutes * 60) - secondsRemaining)
                    FocusPhase.FARDHU -> (iqamahWaitMinutes * 60) + (qobliyahWaitMinutes * 60) +
                            ((totalDurationMinutes - iqamahWaitMinutes - qobliyahWaitMinutes) * 60 - secondsRemaining)
                    FocusPhase.DZIKIR -> totalDurationSec - secondsRemaining
                }
                val progress = (elapsedSec.toFloat() / totalDurationSec).coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(listOf(IslamicGold, IslamicGoldLight))
                        )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Fase $phaseNum dari $totalPhases",
                fontSize = 11.sp,
                color = Color(0xAAFFFFFF),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================
// FASE 1: ADZAN
// ============================================================

@Composable
private fun AdzanPhase(
    prayerId: PrayerId,
    prayerTimeFormatted: String,
    countdownText: String,
    pulseScale: Float
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🕌", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "WAKTU ${prayerId.displayName.uppercase()} TELAH TIBA",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = prayerTimeFormatted,
            fontSize = 72.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 6.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x44FFD700))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(horizontal = 40.dp, vertical = 20.dp)
                .scale(pulseScale)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⏱️ IQAMAH DALAM",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = countdownText,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ReminderCard(
                icon = Icons.Default.PhoneAndroid,
                text = "HENINGKAN HP",
                color = UrgentRed
            )
            Spacer(modifier = Modifier.width(16.dp))
            ReminderCard(
                icon = Icons.Default.VolumeOff,
                text = "RAPATKAN SHAF",
                color = IslamicGreen
            )
        }
    }
}

// ============================================================
// FASE 2: QOBLIYAH
// ============================================================

@Composable
private fun QobliyahPhase(
    prayerId: PrayerId,
    countdownText: String,
    pulseScale: Float
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🕌", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "SHOLAT SUNNAH QOBLIYAH",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sebelum ${prayerId.displayName}",
            fontSize = 24.sp,
            color = Color(0xCCFFFFFF),
            fontStyle = FontStyle.Italic
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x44A5D6A7))
                .border(2.dp, IslamicGreen, RoundedCornerShape(20.dp))
                .padding(horizontal = 40.dp, vertical = 20.dp)
                .scale(pulseScale)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⏱️ SHOLAT FARDHU DALAM",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreen,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = countdownText,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x33000000))
                .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "رَبَّنَا وَاجْعَلْنَا مُسْلِمَيْنِ لَكَ",
                    fontSize = 28.sp,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ya Allah, jadikanlah kami orang yang berserah diri kepada-Mu",
                    fontSize = 14.sp,
                    color = Color(0xCCFFFFFF),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// ============================================================
// FASE 3: FARDHU
// ============================================================

@Composable
private fun FardhuPhase(prayerId: PrayerId) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🕌", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "DIRIKAN SHOLAT ${prayerId.displayName.uppercase()}",
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x33000000))
                .border(2.dp, IslamicGold, RoundedCornerShape(16.dp))
                .padding(32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَوْقُوتًا",
                    fontSize = 28.sp,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "\"Sesungguhnya sholat itu wajib atas orang-orang mukmin pada waktu yang telah ditentukan.\"",
                    fontSize = 18.sp,
                    color = Color(0xCCFFFFFF),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "(QS. An-Nisa: 103)",
                    fontSize = 14.sp,
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ============================================================
// FASE 4: DZIKIR (BARU)
// ============================================================

@Composable
private fun DzikirPhase(
    dzikirCountdown: String,
    pulseScale: Float
) {
    // Rotasi dzikir setiap 8 detik
    var dzikirIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)
            dzikirIndex = (dzikirIndex + 1) % DzikirStore.totalDzikir
        }
    }

    val dzikir = DzikirStore.getDzikir(dzikirIndex)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "📿 DZIKIR SETELAH SHOLAT",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Dzikir ${dzikirIndex + 1} dari ${DzikirStore.totalDzikir}",
            fontSize = 13.sp,
            color = Color(0xAAFFFFFF),
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Card dzikir
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x44FFFFFF))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Nama dzikir
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dzikir.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight,
                        textAlign = TextAlign.Center
                    )
                    if (dzikir.repeat.isNotBlank()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(IslamicGold)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = dzikir.repeat,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF09141D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Arab
                Text(
                    text = dzikir.arabic,
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 50.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Latin
                Text(
                    text = dzikir.latin,
                    fontSize = 16.sp,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Arti
                Text(
                    text = "\"${dzikir.translation}\"",
                    fontSize = 15.sp,
                    color = Color(0xCCFFFFFF),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Countdown
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x44FFD700))
                .border(2.dp, IslamicGold, RoundedCornerShape(16.dp))
                .padding(horizontal = 32.dp, vertical = 14.dp)
                .scale(pulseScale)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⏱️",
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Kembali ke Home dalam $dzikirCountdown",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ============================================================
// KARTU REMINDER
// ============================================================

@Composable
private fun ReminderCard(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.25f))
            .border(1.5.dp, color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 1.5.sp
            )
        }
    }
}
