package dev.andikune.masjidio.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * NeonFocusBorder — Border fokus neon terang dan berputar untuk navigasi D-Pad TV.
 *
 * Diperbarui untuk memastikan:
 * 1. Border 100% TERANG dan JELAS langsung saat posisi fokus D-pad berada di tombol
 *    (tidak buram/redup di awal, tidak hanya muncul saat ditekan OK).
 * 2. Menggunakan shader hardware-accelerated 60 FPS yang kompatibel dengan seluruh Android TV.
 * 3. Efek rotasi berkilau emas-putih yang dinamis sehingga posisi kursor selalu terlihat jelas.
 * 4. Efek flash putih terang saat ditekan (OK/Enter).
 */
@Composable
fun NeonFocusBorder(
    focused: Boolean,
    pressed: Boolean = false,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 4.dp,
    cornerRadius: Dp = 12.dp,
    glowRadius: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = when {
            pressed -> 1.07f
            focused -> 1.04f
            else -> 1f
        },
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = "focus_scale"
    )

    val rotation = remember { Animatable(0f) }

    LaunchedEffect(focused) {
        if (!focused) {
            rotation.snapTo(0f)
            return@LaunchedEffect
        }
        while (true) {
            rotation.snapTo(0f)
            rotation.animateTo(360f, tween(2400, easing = LinearEasing))
        }
    }

    val flashAlpha = remember { Animatable(1f) }
    LaunchedEffect(pressed) {
        if (pressed) {
            while (true) {
                flashAlpha.animateTo(1f, tween(90, easing = LinearEasing))
                flashAlpha.animateTo(0.3f, tween(90, easing = LinearEasing))
            }
        } else {
            flashAlpha.snapTo(1f)
        }
    }

    Box(modifier = modifier.scale(scale)) {
        content()
        if (focused) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawVisibleNeonBorder(
                    canvasSize = size,
                    strokePx = borderWidth.toPx(),
                    radiusPx = cornerRadius.toPx(),
                    rotationDeg = rotation.value,
                    isPressed = pressed,
                    flash = flashAlpha.value
                )
            }
        }
    }
}

private fun DrawScope.drawVisibleNeonBorder(
    canvasSize: Size,
    strokePx: Float,
    radiusPx: Float,
    rotationDeg: Float,
    isPressed: Boolean,
    flash: Float
) {
    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val corner = CornerRadius(radiusPx, radiusPx)

    // 1. Soft inner highlight agar tombol tampak menyala dari dalam
    drawRoundRect(
        brush = SolidColor(if (isPressed) Color(0x44FFFFFF) else Color(0x22FFD700)),
        size = canvasSize,
        cornerRadius = corner
    )

    // 2. Outer ambient halo glow (tebal dan lembut)
    val haloWidth = strokePx * 1.8f
    val haloColor = if (isPressed) {
        Color.White.copy(alpha = (0.7f * flash).coerceIn(0f, 1f))
    } else {
        Color(0xFFFFD700).copy(alpha = 0.45f)
    }
    drawRoundRect(
        brush = SolidColor(haloColor),
        size = canvasSize,
        cornerRadius = CornerRadius(radiusPx + 2f, radiusPx + 2f),
        style = Stroke(width = haloWidth)
    )

    // 3. Core solid border — Emas terang (100% solid, tidak transparan)
    val coreColor = if (isPressed) {
        Color(0xFFFFFFFF)
    } else {
        Color(0xFFFFD700) // Emas solid #FFD700
    }
    drawRoundRect(
        brush = SolidColor(coreColor),
        size = canvasSize,
        cornerRadius = corner,
        style = Stroke(width = strokePx)
    )

    // 4. Rotating sweep gradient — kilau neon berputar di atas border
    if (!isPressed) {
        rotate(degrees = rotationDeg, pivot = center) {
            val sweepGradient = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFFFD700),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFD700),
                    Color(0xFFFFE082),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFD700)
                ),
                center = center
            )
            drawRoundRect(
                brush = sweepGradient,
                size = canvasSize,
                cornerRadius = corner,
                style = Stroke(width = strokePx * 0.95f)
            )
        }
    }
}
