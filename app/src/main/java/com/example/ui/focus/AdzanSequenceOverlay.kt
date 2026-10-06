package dev.andikune.masjidio.ui.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andikune.masjidio.data.model.PrayerId
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * AdzanSequenceOverlay — 3 tahap pembuka sebelum Mode Fokus Sholat.
 *
 * TAHAP 1: OVERLAY ADZAN (durasi diatur user)
 *   - Full screen
 *   - Tulisan "ADZAN [WAKTU]" + "Selamat menunaikan ibadah sholat [WAKTU]"
 *   - Center
 *
 * TAHAP 2: OVERLAY HIMBAUAN HP (durasi diatur user)
 *   - Full screen
 *   - Tulisan "HENINGKAN HP" + kata-kata bagus
 *   - Center
 *
 * TAHAP 3: NIAT QOBLIYAH + COUNTDOWN IQOMAH (durasi diatur user)
 *   - Full screen
 *   - Niat sholat sunnah qobliyah (arab + latin + arti)
 *   - Countdown mundur waktu iqomah
 *
 * Setelah tahap 3 selesai → callback onSequenceComplete → masuk Mode Fokus
 */

enum class AdzanPhase {
    ADZAN,
    SILENT_PHONE,
    QOBLIYAH_NIAT
}

@Composable
fun AdzanSequenceOverlay(
    prayerId: PrayerId,
    adzanDurationSeconds: Int,
    silentPhoneDurationSeconds: Int,
    qobliyahDurationSeconds: Int,
    iqamahWaitMinutes: Int,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    var currentPhase by remember { mutableStateOf(AdzanPhase.ADZAN) }
    var secondsRemaining by remember { mutableIntStateOf(adzanDurationSeconds) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Auto-advance antar tahap
    LaunchedEffect(currentPhase) {
        when (currentPhase) {
            AdzanPhase.ADZAN -> secondsRemaining = adzanDurationSeconds
            AdzanPhase.SILENT_PHONE -> secondsRemaining = silentPhoneDurationSeconds
            AdzanPhase.QOBLIYAH_NIAT -> secondsRemaining = qobliyahDurationSeconds
        }

        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        }

        // Advance ke tahap berikutnya
        when (currentPhase) {
            AdzanPhase.ADZAN -> currentPhase = AdzanPhase.SILENT_PHONE
            AdzanPhase.SILENT_PHONE -> currentPhase = AdzanPhase.QOBLIYAH_NIAT
            AdzanPhase.QOBLIYAH_NIAT -> onComplete()
        }
    }

    // Request focus untuk menerima tombol remote
    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0E1A),
                        Color(0xFF05121E),
                        Color(0xFF020810)
                    )
                )
            )
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> {
                            // OK = skip ke tahap berikutnya
                            when (currentPhase) {
                                AdzanPhase.ADZAN -> currentPhase = AdzanPhase.SILENT_PHONE
                                AdzanPhase.SILENT_PHONE -> currentPhase = AdzanPhase.QOBLIYAH_NIAT
                                AdzanPhase.QOBLIYAH_NIAT -> onComplete()
                            }
                            true
                        }
                        Key.Back, Key.Escape -> {
                            // Back = keluar langsung dari sequence
                            onSkip()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .focusable()
    ) {
        AnimatedContent(
            targetState = currentPhase,
            transitionSpec = {
                (fadeIn(tween(600)) + scaleIn(tween(600), initialScale = 0.95f)) togetherWith
                (fadeOut(tween(600)) + scaleOut(tween(600), targetScale = 1.05f))
            },
            label = "adzan_phase"
        ) { phase ->
            when (phase) {
                AdzanPhase.ADZAN -> AdzanPhaseContent(prayerId = prayerId)
                AdzanPhase.SILENT_PHONE -> SilentPhonePhaseContent()
                AdzanPhase.QOBLIYAH_NIAT -> QobliyahPhaseContent(
                    prayerId = prayerId,
                    countdownSeconds = iqamahWaitMinutes * 60,
                    secondsRemainingInPhase = secondsRemaining
                )
            }
        }

        // Hint tombol (kanan atas)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .background(Color(0x88000000), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "OK: Lanjut • BACK: Skip",
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
// ============================================================
// TAHAP 1 — KONTEN ADZAN
// ============================================================
@Composable
private fun AdzanPhaseContent(prayerId: PrayerId) {
    val infiniteTransition = rememberInfiniteTransition(label = "adzan_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "adzan_pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🕌",
            fontSize = 72.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ADZAN",
            fontSize = 44.sp,
            fontWeight = FontWeight.Light,
            color = TextSecondary,
            letterSpacing = 8.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = prayerId.displayName.uppercase(),
            fontSize = 96.sp,
            fontWeight = FontWeight.ExtraBold,
            color = IslamicGoldLight,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(pulseScale)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            IslamicGold,
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Selamat menunaikan ibadah sholat",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = prayerId.displayName,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGold,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        )
    }
}

// ============================================================
// TAHAP 2 — KONTEN HIMBAUAN HP
// ============================================================
@Composable
private fun SilentPhonePhaseContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    IslamicGold.copy(alpha = 0.15f),
                    RoundedCornerShape(70.dp)
                )
                .border(3.dp, IslamicGold, RoundedCornerShape(70.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = IslamicGoldLight,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "HENINGKAN HP ANDA",
            fontSize = 56.sp,
            fontWeight = FontWeight.ExtraBold,
            color = IslamicGoldLight,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            IslamicGold,
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Demi kekhusyukan ibadah bersama,",
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "mohon matikan atau senyapkan HP Anda",
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .background(
                    IslamicGold.copy(alpha = 0.15f),
                    RoundedCornerShape(12.dp)
                )
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VolumeOff,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Terima kasih atas perhatiannya",
                fontSize = 18.sp,
                color = IslamicGoldLight,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================================
// TAHAP 3 — KONTEN NIAT QOBLIYAH + COUNTDOWN IQOMAH
// ============================================================
@Composable
private fun QobliyahPhaseContent(
    prayerId: PrayerId,
    countdownSeconds: Int,
    secondsRemainingInPhase: Int
) {
    val niat = getQobliyahNiat(prayerId)

    // Hitung countdown iqomah dari sisa waktu tahap ini
    val totalCountdownSeconds = countdownSeconds
    val currentCountdownSeconds = (totalCountdownSeconds - (15 - secondsRemainingInPhase))
        .coerceAtLeast(0)

    val minutes = currentCountdownSeconds / 60
    val seconds = currentCountdownSeconds % 60
    val countdownText = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NIAT SHOLAT SUNNAH QOBLIYAH",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = prayerId.displayName.uppercase(),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = IslamicGold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    IslamicGold.copy(alpha = 0.08f),
                    RoundedCornerShape(20.dp)
                )
                .border(2.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
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
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = niat.arti,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Countdown menuju iqomah
        Box(
            modifier = Modifier
                .background(
                    IslamicGreen.copy(alpha = 0.15f),
                    RoundedCornerShape(16.dp)
                )
                .border(2.dp, IslamicGreen, RoundedCornerShape(16.dp))
                .padding(horizontal = 40.dp, vertical = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⏱️ IQOMAH DALAM",
                    fontSize = 16.sp,
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
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 3.sp
                )
            }
        }
    }
}
// ============================================================
// DATA NIAT SHOLAT SUNNAH QOBLIYAH (standar NU)
// Arab + Latin + Arti
// ============================================================
private data class QobliyahNiat(
    val arabic: String,
    val latin: String,
    val arti: String
)

private fun getQobliyahNiat(prayerId: PrayerId): QobliyahNiat {
    return when (prayerId) {
        PrayerId.SUBUH -> QobliyahNiat(
            arabic = "أُصَلِّي سُنَّةَ الصُّبْحِ رَكْعَتَيْنِ قِبْلِيَّةً لِلَّهِ تَعَالَى",
            latin = "Ushallī sunnatash-shubhi rak'ataini qabliyyatan lillāhi ta'ālā",
            arti = "Aku niat sholat sunnah Subuh dua rakaat, sebelum (qobliyah), karena Allah Ta'ala."
        )
        PrayerId.DZUHUR -> QobliyahNiat(
            arabic = "أُصَلِّي سُنَّةَ الظُّهْرِ رَكْعَتَيْنِ قِبْلِيَّةً لِلَّهِ تَعَالَى",
            latin = "Ushallī sunnatadh-dhuhri rak'ataini qabliyyatan lillāhi ta'ālā",
            arti = "Aku niat sholat sunnah Dzuhur dua rakaat, sebelum (qobliyah), karena Allah Ta'ala."
        )
        PrayerId.ASHAR -> QobliyahNiat(
            arabic = "أُصَلِّي سُنَّةَ الْعَصْرِ رَكْعَتَيْنِ قِبْلِيَّةً لِلَّهِ تَعَالَى",
            latin = "Ushallī sunnatal-'ashri rak'ataini qabliyyatan lillāhi ta'ālā",
            arti = "Aku niat sholat sunnah Ashar dua rakaat, sebelum (qobliyah), karena Allah Ta'ala."
        )
        PrayerId.MAGHRIB -> QobliyahNiat(
            arabic = "أُصَلِّي سُنَّةَ الْمَغْرِبِ رَكْعَتَيْنِ قِبْلِيَّةً لِلَّهِ تَعَالَى",
            latin = "Ushallī sunnatal-maghribi rak'ataini qabliyyatan lillāhi ta'ālā",
            arti = "Aku niat sholat sunnah Maghrib dua rakaat, sebelum (qobliyah), karena Allah Ta'ala."
        )
        PrayerId.ISYA -> QobliyahNiat(
            arabic = "أُصَلِّي سُنَّةَ الْعِشَاءِ رَكْعَتَيْنِ قِبْلِيَّةً لِلَّهِ تَعَالَى",
            latin = "Ushallī sunnatal-'isyā'i rak'ataini qabliyyatan lillāhi ta'ālā",
            arti = "Aku niat sholat sunnah Isya dua rakaat, sebelum (qobliyah), karena Allah Ta'ala."
        )
        PrayerId.SYURUQ -> QobliyahNiat(
            arabic = "أُصَلِّي سُنَّةَ الضُّحَى رَكْعَتَيْنِ لِلَّهِ تَعَالَى",
            latin = "Ushallī sunnatadh-dhuḥā rak'ataini lillāhi ta'ālā",
            arti = "Aku niat sholat sunnah Dhuha dua rakaat, karena Allah Ta'ala."
        )
    }
}
