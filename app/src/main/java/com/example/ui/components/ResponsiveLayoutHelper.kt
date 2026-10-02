package com.example.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * RESPONSIVE LAYOUT HELPER
 *
 * Solusi TV dengan berbagai ukuran & aspect ratio:
 *   16:9 standar (1920x1080), 21:9 ultrawide, 4:3 lama, 4K, 720p, dll.
 *
 * Cara kerja:
 *   1. Deteksi ukuran layar real-time
 *   2. Hitung scaleFactor (base = 1920x1080)
 *   3. Auto-scale semua ukuran font/padding/icon
 *   4. Tambah safe padding untuk hindari overscan bezel TV
 */

// ============================================================
// BASE DESIGN (patokan = FHD 1920x1080)
// ============================================================
private const val BASE_WIDTH = 1920f
private const val BASE_HEIGHT = 1080f

// ============================================================
// DATA CLASS
// ============================================================
data class ScreenInfo(
    val widthPx: Int,
    val heightPx: Int,
    val widthDp: Float,
    val heightDp: Float,
    val density: Float,
    val aspectRatio: Float,
    val scaleFactor: Float,
    val aspectType: AspectType,
    val safePaddingPx: Int,
    val safePaddingDp: Dp
)

enum class AspectType {
    ULTRAWIDE,      // > 2.0 (misal 21:9 = 2.33)
    STANDARD_WIDE,  // 1.7 - 2.0 (16:9 = 1.78)
    STANDARD,       // 1.3 - 1.7 (16:10 = 1.6)
    CLASSIC_4_3,    // < 1.3 (4:3 = 1.33)
    UNKNOWN
}

// ============================================================
// COMPOSITION LOCAL
// ============================================================
val LocalScreenInfo = compositionLocalOf<ScreenInfo> {
    error("ScreenInfo belum dihitung. Pakai rememberScreenInfo() atau ResponsiveRoot { } dulu.")
}

// ============================================================
// FUNGSI HITUNG SCREEN INFO
// ============================================================
@Composable
fun rememberScreenInfo(
    autoScaleEnabled: Boolean = true,
    safeAreaPercent: Float = 3f
): ScreenInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val widthPx = with(density) { configuration.screenWidthDp.dp.toPx() }.toInt()
    val heightPx = with(density) { configuration.screenHeightDp.dp.toPx() }.toInt()
    val widthDpF = configuration.screenWidthDp.toFloat()
    val heightDpF = configuration.screenHeightDp.toFloat()
    val densityF = density.density

    val aspectRatio = if (heightDpF > 0f) widthDpF / heightDpF else 1.78f

    val aspectType = when {
        aspectRatio >= 2.0f -> AspectType.ULTRAWIDE
        aspectRatio >= 1.7f -> AspectType.STANDARD_WIDE
        aspectRatio >= 1.3f -> AspectType.STANDARD
        aspectRatio > 0f -> AspectType.CLASSIC_4_3
        else -> AspectType.UNKNOWN
    }

    // Scale factor: min(lebar/1920, tinggi/1080) — pilih yang paling kecil biar tidak overflow
    val rawScale = if (autoScaleEnabled) {
        minOf(
            widthDpF / (BASE_WIDTH / densityF),
            heightDpF / (BASE_HEIGHT / densityF)
        )
    } else 1f

    // Clamp 0.6 - 2.5 biar tidak ekstrem
    val scaleFactor = rawScale.coerceIn(0.6f, 2.5f)

    // Safe area (default 3% dari sisi terpendek)
    val safePercent = safeAreaPercent.coerceIn(0f, 10f) / 100f
    val shortestSidePx = minOf(widthPx, heightPx)
    val safePaddingPx = (shortestSidePx * safePercent).toInt()
    val safePaddingDp = (safePaddingPx / densityF).dp

    return ScreenInfo(
        widthPx = widthPx,
        heightPx = heightPx,
        widthDp = widthDpF,
        heightDp = heightDpF,
        density = densityF,
        aspectRatio = aspectRatio,
        scaleFactor = scaleFactor,
        aspectType = aspectType,
        safePaddingPx = safePaddingPx,
        safePaddingDp = safePaddingDp
    )
}

// ============================================================
// WRAPPER ROOT — Pasang sekali di atas, semua anak bisa akses
// ============================================================
@Composable
fun ResponsiveRoot(
    autoScaleEnabled: Boolean = true,
    safeAreaPercent: Float = 3f,
    content: @Composable (ScreenInfo) -> Unit
) {
    val info = rememberScreenInfo(
        autoScaleEnabled = autoScaleEnabled,
        safeAreaPercent = safeAreaPercent
    )

    CompositionLocalProvider(LocalScreenInfo provides info) {
        content(info)
    }
}

// ============================================================
// EXTENSION — Auto-scale font size
// ============================================================
fun ScreenInfo.sp(base: Float): androidx.compose.ui.unit.TextUnit {
    return (base * scaleFactor).sp
}

fun ScreenInfo.sp(base: Int): androidx.compose.ui.unit.TextUnit {
    return (base * scaleFactor).sp
}

// ============================================================
// EXTENSION — Auto-scale dp
// ============================================================
fun ScreenInfo.dp(base: Float): Dp = (base * scaleFactor).dp
fun ScreenInfo.dp(base: Int): Dp = (base * scaleFactor).dp

// ============================================================
// EXTENSION — Safe Area Padding
// ============================================================
fun ScreenInfo.safePadding(): PaddingValues {
    return PaddingValues(
        start = safePaddingDp,
        end = safePaddingDp,
        top = safePaddingDp,
        bottom = safePaddingDp
    )
}

// ============================================================
// MODIFIER — Auto-scale padding
// ============================================================
fun Modifier.responsivePadding(info: ScreenInfo, base: Dp): Modifier = composed {
    this.then(Modifier.padding(base * info.scaleFactor))
}

@Composable
fun Modifier.responsivePaddingCurrent(base: Dp): Modifier {
    val info = LocalScreenInfo.current
    return this.then(Modifier.padding(base * info.scaleFactor))
}
