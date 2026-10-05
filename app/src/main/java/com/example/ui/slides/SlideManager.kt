package com.example.ui.slides

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import com.example.data.model.AppSettings
import com.example.ui.slides.QrisSlide
import com.example.ui.slides.LaporanSlide
import com.example.ui.slides.KajianSlide
import kotlinx.coroutines.delay

/**
 * Manager rotasi slide fullscreen.
 *
 * V1.04.423 — CLEAN VIEW:
 *   - Hapus semua elemen visual navigasi (tombol X, panah, indicator, hint)
 *   - Tampilan slide full bersih tanpa overlay apapun
 *   - Fungsi tombol remote TETAP JALAN:
 *     • OK / BACK / ENTER / DPAD_CENTER → keluar slide
 *     • KIRI → slide sebelumnya
 *     • KANAN → slide berikutnya
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
    // FOCUS REQUESTER — supaya bisa terima tombol remote
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
        // ===== KONTEN SLIDE (tanpa overlay apapun) =====
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
    }
}
