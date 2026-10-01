package com.example.ui.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint as AndroidPaint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private enum class BorderPhase {
    HIDDEN, LOADING_1X, DELAY, IDLE, PRESSED
}

/**
 * NeonFocusBorder V4.2:
 *   - Glow SMOOTH: 48 dot + BlurMaskFilter (bukan petak/bulat-bulat)
 *   - Glow "jreng" (vibrant) dengan alpha kepala 100%
 *   - Multi-fase: loading 1x → delay 1s → loop → pressed flash
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
        targetValue = if (focused) 1.05f else 1f,
        animationSpec = tween(200),
        label = "focus_scale"
    )

    val rotation = remember { Animatable(0f) }
    var currentPhase by remember { mutableStateOf(BorderPhase.HIDDEN) }

    LaunchedEffect(focused) {
        if (!focused) {
            currentPhase = BorderPhase.HIDDEN
            rotation.snapTo(0f)
            return@LaunchedEffect
        }
        currentPhase = BorderPhase.LOADING_1X
        rotation.snapTo(0f)
        rotation.animateTo(360f, tween(1200, easing = LinearEasing))

        currentPhase = BorderPhase.DELAY
        delay(1000)

        currentPhase = BorderPhase.IDLE
        while (true) {
            rotation.snapTo(0f)
            rotation.animateTo(360f, tween(2000, easing = LinearEasing))
        }
    }

    val flashAlpha = remember { Animatable(1f) }
    LaunchedEffect(pressed) {
        if (pressed) {
            while (true) {
                flashAlpha.animateTo(1f, tween(150, easing = LinearEasing))
                flashAlpha.animateTo(0.3f, tween(150, easing = LinearEasing))
            }
        } else {
            flashAlpha.snapTo(1f)
        }
    }

    Box(modifier = modifier.scale(scale)) {
        if (focused) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawGlowBorder(
                    canvasSize = size,
                    strokePx = borderWidth.toPx(),
                    glowPx = glowRadius.toPx(),
                    radiusPx = cornerRadius.toPx(),
                    rotationDeg = rotation.value,
                    phase = currentPhase,
                    isPressed = pressed,
                    flash = flashAlpha.value
                )
            }
        }
        content()
    }
}

// ============================================================
// CORE DRAW — Glow SMOOTH (48 dot + BlurMaskFilter)
// ============================================================
private fun DrawScope.drawGlowBorder(
    canvasSize: Size,
    strokePx: Float,
    glowPx: Float,
    radiusPx: Float,
    rotationDeg: Float,
    phase: BorderPhase,
    isPressed: Boolean,
    flash: Float
) {
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas

        // ============ CORE BORDER (dasar dim emas di tepi tombol) ============
        val coreInset = strokePx / 2f
        val coreRect = RectF(
            coreInset,
            coreInset,
            canvasSize.width - coreInset,
            canvasSize.height - coreInset
        )
        val corePaint = AndroidPaint().apply {
            isAntiAlias = true
            style = AndroidPaint.Style.STROKE
            strokeWidth = strokePx
            color = 0x33FFD700.toInt()
        }
        native.drawRoundRect(coreRect, radiusPx, radiusPx, corePaint)

        // ============ GLOW TAIL ============
        val glowOutset = glowPx * 0.4f
        val glowRect = RectF(
            -glowOutset,
            -glowOutset,
            canvasSize.width + glowOutset,
            canvasSize.height + glowOutset
        )
        val glowPath = Path().apply {
            addRoundRect(
                glowRect,
                radiusPx + glowOutset,
                radiusPx + glowOutset,
                Path.Direction.CW
            )
        }

        val pathMeasure = PathMeasure(glowPath, false)
        val pathLength = pathMeasure.length

        val headColors = when {
            isPressed -> listOf(0xFFFFD700.toInt())
            phase == BorderPhase.LOADING_1X || phase == BorderPhase.DELAY ->
                listOf(0xFFFFD700.toInt())
            phase == BorderPhase.IDLE ->
                listOf(0xFFFFFFFF.toInt(), 0xFFFFD700.toInt())
            else -> emptyList()
        }

        // ============ SMOOTH: 48 dot + blur ============
        val tailLenFraction = 0.30f     // ekor sedikit lebih panjang
        val dotCount = 48               // 2.4x dari sebelumnya (halus)
        val blurRadius = glowPx * 1.2f  // soft edge

        headColors.forEachIndexed { idx, color ->
            val baseT = rotationDeg / 360f
            val headT = (baseT + idx * 0.5f) % 1f

            for (i in 0 until dotCount) {
                val t = headT - (i.toFloat() / dotCount) * tailLenFraction
                val wrapped = ((t % 1f) + 1f) % 1f

                val pos = FloatArray(2)
                pathMeasure.getPosTan(wrapped * pathLength, pos, null)

                // Alpha: kepala 100% → ekor 0%, kurva eksponensial (lebih smooth)
                val linear = 1f - (i.toFloat() / dotCount)
                val alphaBase = linear * linear * 0.7f + linear * 0.3f   // boost kepala
                val alpha = if (isPressed) alphaBase * flash else alphaBase

                // Radius: 35% - 70% glowPx (overlap antar dot)
                val dotRadius = glowPx * (0.35f + 0.35f * linear)

                val headColor = if (isPressed) 0xFFFFD700.toInt() else color

                val paint = AndroidPaint().apply {
                    isAntiAlias = true
                    // Blur untuk smooth edge
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                    shader = RadialGradient(
                        pos[0], pos[1], dotRadius,
                        intArrayOf(
                            colorWithAlpha(headColor, alpha * 1.0f),    // core 100%
                            colorWithAlpha(headColor, alpha * 0.7f),    // mid 70%
                            colorWithAlpha(headColor, alpha * 0.3f),    // outer 30%
                            colorWithAlpha(headColor, 0f)               // fade 0%
                        ),
                        floatArrayOf(0f, 0.3f, 0.7f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                native.drawCircle(pos[0], pos[1], dotRadius, paint)
            }
        }
    }
}

// ============================================================
// HELPER — Apply alpha ke warna
// ============================================================
private fun colorWithAlpha(color: Int, alpha: Float): Int {
    val a = (alpha * 255).toInt().coerceIn(0, 255)
    return (color and 0x00FFFFFF) or (a shl 24)
}
