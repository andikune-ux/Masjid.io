package com.example.ui.slides

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Manager rotasi slide fullscreen.
 *
 * V1.04.422 NEW:
 *   - Tombol OK / BACK / ENTER / DPAD_CENTER → keluar slide, kembali ke Home
 *   - Tombol KANAN → pindah ke slide berikutnya
 *   - Tombol KIRI  → pindah ke slide sebelumnya
 *   - Indicator dot + panah navigasi di bawah
 *
 * Dipanggil dari HomeScreen sebagai overlay.
 */
enum class SlideType {
    QRIS,
    LAPORAN,
    KAJIAN
}

@Composable
fun SlideManager(
    settings: AppSettings,
    nextPrayerSeconds: Long,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Kumpulkan slide aktif
    val activeSlides = remember(
        settings.qrisSlideEnabled,
        settings.laporanSlideEnabled,
        settings.kajianSlideEnabled
    ) {
        buildList {
            if (settings.qrisSlideEnabled) add(SlideType.QRIS)
            if (settings.laporanSlideEnabled) add(SlideType.LAPORAN)
            if (settings.kajianSlideEnabled) add(SlideType.KAJIAN)
        }
    }

    if (activeSlides.isEmpty()) {
        LaunchedEffect(Unit) {
            onDismiss()
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val safeInterval = settings.slideIntervalSeconds.coerceIn(5, 120)

    // Auto rotate
    LaunchedEffect(activeSlides.size, safeInterval) {
        if (activeSlides.size > 1) {
            while (true) {
                delay(safeInterval * 1000L)
                currentIndex = (currentIndex + 1) % activeSlides.size
            }
        }
    }

    if (currentIndex >= activeSlides.size) {
        currentIndex = 0
    }

    val currentSlide = activeSlides.getOrElse(currentIndex) { activeSlides.first() }

    // ============================================================
    // FOCUS REQUESTER — untuk terima tombol remote
    // ============================================================
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(200)
        runCatching { focusRequester.requestFocus() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        // ===== KELUAR — OK / BACK / ENTER / DPAD_CENTER =====
                        Key.Enter,
                        Key.NumPadEnter,
                        Key.DirectionCenter,
                        Key.Back,
                        Key.Escape -> {
                            onDismiss()
                            true
                        }

                        // ===== PINDAH KE SLIDE BERIKUTNYA =====
                        Key.DirectionRight -> {
                            currentIndex = (currentIndex + 1) % activeSlides.size
                            true
                        }

                        // ===== PINDAH KE SLIDE SEBELUMNYA =====
                        Key.DirectionLeft -> {
                            currentIndex = if (currentIndex - 1 < 0) {
                                activeSlides.size - 1
                            } else {
                                currentIndex - 1
                            }
                            true
                        }

                        else -> false
                    }
                } else false
            }
            .focusable()
    ) {
        // ===== KONTEN SLIDE dengan animasi =====
        AnimatedContent(
            targetState = currentSlide,
            transitionSpec = {
                (slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(600)
                ) + fadeIn(tween(600))) togetherWith
                (slideOutHorizontally(
                    targetOffsetX = { -it / 4 },
                    animationSpec = tween(600)
                ) + fadeOut(tween(600)))
            },
            label = "slide_transition"
        ) { slide ->
            when (slide) {
                SlideType.QRIS -> QrisSlide(
                    settings = settings,
                    modifier = Modifier.fillMaxSize()
                )
                SlideType.LAPORAN -> LaporanSlide(
                    settings = settings,
                    modifier = Modifier.fillMaxSize()
                )
                SlideType.KAJIAN -> KajianSlide(
                    settings = settings,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // ============================================================
        // TOMBOL CLOSE (kanan atas)
        // ============================================================
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0x88000000))
                .border(2.dp, IslamicGold.copy(alpha = 0.6f), CircleShape)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup Slide",
                tint = IslamicGoldLight,
                modifier = Modifier.size(24.dp)
            )
        }

        // ============================================================
        // PANAH NAVIGASI KIRI & KANAN (overlay kecil di tengah kiri/kanan)
        // ============================================================
        if (activeSlides.size > 1) {
            // Panah kiri
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0x66000000))
                    .border(2.dp, IslamicGold.copy(alpha = 0.5f), CircleShape)
                    .clickable {
                        currentIndex = if (currentIndex - 1 < 0) {
                            activeSlides.size - 1
                        } else {
                            currentIndex - 1
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Slide Sebelumnya",
                    tint = IslamicGoldLight,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Panah kanan
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0x66000000))
                    .border(2.dp, IslamicGold.copy(alpha = 0.5f), CircleShape)
                    .clickable {
                        currentIndex = (currentIndex + 1) % activeSlides.size
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Slide Berikutnya",
                    tint = IslamicGoldLight,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // ============================================================
        // INDIKATOR DOT + NAVIGASI HINT (bawah tengah)
        // ============================================================
        if (activeSlides.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xAA000000))
                    .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Panah kiri kecil (visual hint)
                Text(
                    text = "◀",
                    fontSize = 12.sp,
                    color = TextSecondary.copy(alpha = 0.6f)
                )

                // Dot indicators
                activeSlides.forEachIndexed { index, _ ->
                    val isActive = index == currentIndex
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) IslamicGold
                                else Color(0x66FFFFFF)
                            )
                    )
                }

                // Label slide aktif
                Text(
                    text = "  ${slideLabel(currentSlide)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    letterSpacing = 1.sp
                )

                // Panah kanan kecil (visual hint)
                Text(
                    text = "  ▶",
                    fontSize = 12.sp,
                    color = TextSecondary.copy(alpha = 0.6f)
                )
            }
        }

        // ============================================================
        // HINT TOMBOL REMOTE (pojok kiri bawah)
        // ============================================================
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x88000000))
                .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "◀ ▶ GESER  •  OK / BACK: KELUAR",
                fontSize = 10.sp,
                color = TextSecondary.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ============================================================
// HELPER: LABEL SLIDE
// ============================================================
private fun slideLabel(slide: SlideType): String = when (slide) {
    SlideType.QRIS -> "INFAQ QRIS"
    SlideType.LAPORAN -> "LAPORAN KEUANGAN"
    SlideType.KAJIAN -> "JADWAL KAJIAN"
}
