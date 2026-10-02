package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import kotlinx.coroutines.delay

/**
 * AutoFocusPane — Wrapper yang otomatis memindahkan fokus D-pad
 * ke elemen focusable PERTAMA di dalam subtree pane.
 *
 * TUJUAN:
 *   Saat pane baru dibuka (misal: kategori "Audio & Adzan"),
 *   fokus D-pad TIDAK mendarat di tombol Kembali di TopBar,
 *   tapi langsung ke tombol/slider paling atas di dalam pane.
 *
 * CARA PAKAI:
 *   AutoFocusPane(paneKey = previewCategory) {
 *       when (previewCategory) {
 *           LOCATION -> LocationSettingsPane(...)
 *           AUDIO -> AudioSettingsPane(...)
 *           // ... dst
 *       }
 *   }
 *
 * @param paneKey Kunci yang berubah saat ganti pane (untuk re-trigger auto-focus)
 * @param focusDelayMs Delay sebelum request focus (default 150ms, tunggu pane render)
 */
@Composable
fun AutoFocusPane(
    paneKey: Any,
    focusDelayMs: Long = 150L,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val paneFocusRequester = remember { FocusRequester() }

    // Auto-focus saat paneKey berubah (pane baru terbuka)
    LaunchedEffect(paneKey) {
        // Tunggu pane selesai render dulu
        delay(focusDelayMs)
        // Request focus — Compose akan teruskan ke child focusable pertama
        runCatching { paneFocusRequester.requestFocus() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(paneFocusRequester)
            .focusTarget()
    ) {
        content()
    }
}
