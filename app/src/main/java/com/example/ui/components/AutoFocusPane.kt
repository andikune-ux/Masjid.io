package com.example.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.focusRestorer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * AutoFocusPane — Wrapper untuk container pane di Settings.
 *
 * V1.30.7: Ganti logika ke `focusRestorer()`.
 *
 * SEBELUMNYA (V1.30.5):
 *   - Pakai LaunchedEffect + delay + requestFocus()
 *   - Bikin fokus tarik-menarik dengan sistem
 *   - Efek: fokus "bolak-balik" ke tombol Kembali
 *
 * SEKARANG:
 *   - Pakai focusRestorer() sesuai referensi Android
 *   - Fokus diingat posisi terakhir
 *   - Tidak panggil requestFocus() otomatis
 *   - Tidak pakai delay
 *
 * CARA KERJA:
 *   - Saat user masuk ke pane → Compose pilih elemen pertama
 *   - Saat user navigasi → posisi diingat
 *   - Saat user keluar-masuk pane → fokus balik ke posisi terakhir
 *   - Tidak ada tarik-menarik dengan sidebar atau TopBar
 *
 * @param paneKey Kunci untuk reset state saat pane berubah (opsional)
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
            .focusRestorer()  // ← KUNCI: ingat posisi fokus terakhir
    ) {
        content()
    }
}
