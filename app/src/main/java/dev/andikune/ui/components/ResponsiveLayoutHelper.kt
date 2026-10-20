package dev.andikune.masjidio.ui.components

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
 * CATATAN: Extension dinamai scaledDp() dan scaledSp()
 * JANGAN pakai dp()/sp() karena bentrok dengan Compose.
 */

private const val BASE_WIDTH = 1920f
private const val BASE_HEIGHT = 1080f

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
    ULTRAWIDE,
    STANDARD_WIDE,
    STANDARD,
    CLASSIC_4_3,
    UNKNOWN
}

val LocalScreenInfo = compositionLocalOf<ScreenInfo> {
    error("ScreenInfo belum dihitung. Pakai rememberScreenInfo() atau ResponsiveRoot { } dulu.")
}

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

    val rawScale = if (autoScaleEnabled) {
        minOf(
            widthDpF / (BASE_WIDTH / densityF),
            heightDpF / (BASE_HEIGHT / densityF)
        )
    } else 1f

    val scaleFactor = rawScale.coerceIn(0.6f, 2.5f)

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

fun ScreenInfo.scaledSp(base: Float): androidx.compose.ui.unit.TextUnit {
    return (base * scaleFactor).sp
}

fun ScreenInfo.scaledSp(base: Int): androidx.compose.ui.unit.TextUnit {
    return (base * scaleFactor).sp
}

fun ScreenInfo.scaledDp(base: Float): Dp = (base * scaleFactor).dp
fun ScreenInfo.scaledDp(base: Int): Dp = (base * scaleFactor).dp

fun ScreenInfo.safePadding(): PaddingValues {
    return PaddingValues(
        start = safePaddingDp,
        end = safePaddingDp,
        top = safePaddingDp,
        bottom = safePaddingDp
    )
}

fun Modifier.responsivePadding(info: ScreenInfo, base: Dp): Modifier = composed {
    this.then(Modifier.padding(base * info.scaleFactor))
}

@Composable
fun Modifier.responsivePaddingCurrent(base: Dp): Modifier {
    val info = LocalScreenInfo.current
    return this.then(Modifier.padding(base * info.scaleFactor))
}
