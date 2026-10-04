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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.content.DzikirStore
import com.example.data.model.AppSettings
import com.example.data.model.PrayerId
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import kotlinx.coroutines.delay

/**
 * PrayerFocusOverlay — Mode Fokus Sholat (V1.04.421).
 *
 * 2 FASE (berbeda dari versi lama yang 4 fase):
 *   FASE 1: NIAT SHOLAT FARDHU (arab + latin + arti)
 *   FASE 2: DZIKIR (rotasi 8 detik)
 *
 * Fase ADZAN & QOBLIYAH sudah dipindah ke AdzanSequenceOverlay.
 */
private enum class FocusPhase { NIAT_FARDHU, DZIKIR }

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
    var currentPhase by remember { mutableStateOf(FocusPhase.NIAT_FARDHU) }

    // Durasi dari settings (fallback: default)
    val fardhuDurationSec = settings?.fardhuNiatDisplayDurationSeconds ?: 15
    val dzikirDurationSec = settings?.dzikirDisplayDurationSeconds ?: 120

    var secondsRemaining by remember { mutableIntStateOf(fardhuDurationSec) }

    val phaseGradient = when (currentPhase) {
        FocusPhase.NIAT_FARDHU -> listOf(Color(0xFF3A2E1B), Color(0xFF5F442E), Color(0xFF3A2E1B))
        FocusPhase.DZIKIR -> listOf(Color(0xFF1A0D2A), Color(0xFF2E1B3A), Color(0xFF1A0D2A))
    }

    val phaseNum = when (currentPhase) {
        FocusPhase.NIAT_FARDHU -> 1
        FocusPhase.DZIKIR -> 2
    }

    val totalPhases = 2

    // Focus requester untuk terima tombol remote
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(200)
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    // Timer auto-advance antar fase
    LaunchedEffect(currentPhase) {
        secondsRemaining = when (currentPhase) {
            FocusPhase.NIAT_FARDHU -> fardhuDurationSec
            FocusPhase.DZIKIR -> dzikirDurationSec
        }

        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
        }

        when (currentPhase) {
            FocusPhase.NIAT_FARDHU -> {
                if (settings?.dzikirEnabled == true) {
                    currentPhase = FocusPhase.DZIKIR
                } else {
                    onDismiss()
                }
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

    val allowExit = settings?.focusModeAllowExitWithRemote ?: true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(phaseGradient))
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> {
                            // OK = skip ke fase berikutnya atau keluar
                            when (currentPhase) {
                                FocusPhase.NIAT_FARDHU -> {
                                    if (settings?.dzikirEnabled == true) {
                                        currentPhase = FocusPhase.DZIKIR
                                    } else {
                                        onDismiss()
                                    }
                                }
                                FocusPhase.DZIKIR -> onDismiss()
                            }
                            true
                        }
                        Key.Back, Key.Escape -> {
                            // Back = keluar dari Mode Fokus (kalau diizinkan)
                            if (allowExit) {
                                onDismiss()
                            }
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .focusable()
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

                // Tombol Close (kalau allowExit = true)
                if (allowExit) {
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
                    FocusPhase.NIAT_FARDHU -> NiatFardhuPhase(prayerId = prayerId)
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
                val totalSec = (fardhuDurationSec + if (settings?.dzikirEnabled == true) dzikirDurationSec else 0).coerceAtLeast(1)
                val elapsedSec = when (currentPhase) {
                    FocusPhase.NIAT_FARDHU -> fardhuDurationSec - secondsRemaining
                    FocusPhase.DZIKIR -> fardhuDurationSec + (dzikirDurationSec - secondsRemaining)
                }
                val progress = (elapsedSec.toFloat() / totalSec).coerceIn(0f, 1f)

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
                text = "Fase $phaseNum dari $totalPhases  •  OK: Lanjut  •  BACK: Keluar",
                fontSize = 11.sp,
                color = Color(0xAAFFFFFF),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
// ============================================================
// FASE 1: NIAT SHOLAT FARDHU (arab + latin + arti)
// ============================================================

@Composable
private fun NiatFardhuPhase(prayerId: PrayerId) {
    val niat = getFardhuNiat(prayerId)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🕌", fontSize = 56.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "NIAT SHOLAT FARDHU",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = prayerId.displayName.uppercase(),
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            color = IslamicGold,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x33000000))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = niat.arabic,
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 50.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(1.dp)
                        .background(IslamicGold.copy(alpha = 0.4f))
                )

                Text(
                    text = niat.latin,
                    fontSize = 16.sp,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = niat.arti,
                    fontSize = 14.sp,
                    color = Color(0xCCFFFFFF),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

// ============================================================
// FASE 2: DZIKIR (rotasi 8 detik)
// ============================================================

@Composable
private fun DzikirPhase(
    dzikirCountdown: String,
    pulseScale: Float
) {
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

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x44FFFFFF))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                Text(
                    text = dzikir.arabic,
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 50.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = dzikir.latin,
                    fontSize = 16.sp,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

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

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x44FFD700))
                .border(2.dp, IslamicGold, RoundedCornerShape(16.dp))
                .padding(horizontal = 32.dp, vertical = 14.dp)
                .scale(pulseScale)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⏱️", fontSize = 22.sp)
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
// DATA NIAT SHOLAT FARDHU (standar NU)
// Arab + Latin + Arti
// ============================================================
private data class FardhuNiat(
    val arabic: String,
    val latin: String,
    val arti: String
)

private fun getFardhuNiat(prayerId: PrayerId): FardhuNiat {
    return when (prayerId) {
        PrayerId.SUBUH -> FardhuNiat(
            arabic = "أُصَلِّي فَرْضَ الصُّبْحِ رَكْعَتَيْنِ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً لِلَّهِ تَعَالَى",
            latin = "Ushallī fardhash-shubhi rak'ataini mustaqbilal-qiblati adā'an lillāhi ta'ālā",
            arti = "Aku niat sholat fardhu Subuh dua rakaat, menghadap kiblat, tunai, karena Allah Ta'ala."
        )
        PrayerId.DZUHUR -> FardhuNiat(
            arabic = "أُصَلِّي فَرْضَ الظُّهْرِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً لِلَّهِ تَعَالَى",
            latin = "Ushallī fardhazh-zhuhri arba'a raka'ātin mustaqbilal-qiblati adā'an lillāhi ta'ālā",
            arti = "Aku niat sholat fardhu Dzuhur empat rakaat, menghadap kiblat, tunai, karena Allah Ta'ala."
        )
        PrayerId.ASHAR -> FardhuNiat(
            arabic = "أُصَلِّي فَرْضَ الْعَصْرِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً لِلَّهِ تَعَالَى",
            latin = "Ushallī fardhal-'ashri arba'a raka'ātin mustaqbilal-qiblati adā'an lillāhi ta'ālā",
            arti = "Aku niat sholat fardhu Ashar empat rakaat, menghadap kiblat, tunai, karena Allah Ta'ala."
        )
        PrayerId.MAGHRIB -> FardhuNiat(
            arabic = "أُصَلِّي فَرْضَ الْمَغْرِبِ ثَلَاثَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً لِلَّهِ تَعَالَى",
            latin = "Ushallī fardhal-maghribi tsalātsa raka'ātin mustaqbilal-qiblati adā'an lillāhi ta'ālā",
            arti = "Aku niat sholat fardhu Maghrib tiga rakaat, menghadap kiblat, tunai, karena Allah Ta'ala."
        )
        PrayerId.ISYA -> FardhuNiat(
            arabic = "أُصَلِّي فَرْضَ الْعِشَاءِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً لِلَّهِ تَعَالَى",
            latin = "Ushallī fardhal-'isyā'i arba'a raka'ātin mustaqbilal-qiblati adā'an lillāhi ta'ālā",
            arti = "Aku niat sholat fardhu Isya empat rakaat, menghadap kiblat, tunai, karena Allah Ta'ala."
        )
        PrayerId.SYURUQ -> FardhuNiat(
            arabic = "أُصَلِّي سُنَّةَ الضُّحَى رَكْعَتَيْنِ لِلَّهِ تَعَالَى",
            latin = "Ushallī sunnatadh-dhuḥā rak'ataini lillāhi ta'ālā",
            arti = "Aku niat sholat sunnah Dhuha dua rakaat, karena Allah Ta'ala."
        )
    }
}
