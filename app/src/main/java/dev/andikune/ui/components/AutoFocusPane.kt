package dev.andikune.masjidio.ui.components

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * AutoFocusPane — Wrapper untuk container pane di Settings.
 *
 * V1.30.7 (revisi): Pakai focusGroup() (kompatibel Compose lama).
 *
 * Prinsip:
 *   - focusGroup(): grup elemen fokus di dalam pane
 *   - TIDAK panggil requestFocus() otomatis (bikin chaos)
 *   - Biarkan sistem Compose yang atur navigasi dalam group
 *
 * @param paneKey Kunci untuk reset state (kompatibilitas)
 * @param modifier Modifier dari pemanggil
 * @param content Konten pane
 */
@Composable
fun AutoFocusPane(
    paneKey: Any,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .focusGroup()  // ← grup fokus (kompatibel Compose lama)
    ) {
        content()
    }
}
