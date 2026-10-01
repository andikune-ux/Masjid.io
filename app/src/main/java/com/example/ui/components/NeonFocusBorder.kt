package com.example.ui.components

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
 * NeonFocusBorder V4.1:
 *   - Glow di LUAR tombol (tipis, tidak menyebar jauh)
 *   - Smooth (PathMeasure + RadialGradient per dot)
 *   - Multi-fase: loading 1x → delay 1s → loop → pressed flash
 */
@Composable
fun NeonFocusBorder(
    focused: Boolean,
    pressed: Boolean = false,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 4.dp,
    cornerRadius: Dp = 12.dp,
    glowRadius: Dp = 8.dp,      // ← lebih kecil dari 13.5dp
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
        // FASE 1: 1x putaran emas
        currentPhase = BorderPhase.LOADING_1X
        rotation.snapTo(0f)
        rotation.animateTo(360f, tween(1200, easing = LinearEasing))

        // DELAY: diam 1 detik
        currentPhase = BorderPhase.DELAY
        delay(1000)

        // FASE 2: loop tanpa berhenti
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
// CORE DRAW — Glow tipis di LUAR border
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

        // ============ GLOW TAIL — TIPIS di LUAR border ============
        // Glow hanya keluar 40% dari glowPx (sekitar 3.2dp) — kecil, rapi
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

        // Heads — 1 atau 2 kutub berdasarkan fase
        val headColors = when {
            isPressed -> listOf(0xFFFFD700.toInt())
            phase == BorderPhase.LOADING_1X || phase == BorderPhase.DELAY ->
                listOf(0xFFFFD700.toInt())
            phase == BorderPhase.IDLE ->
                listOf(0xFFFFFFFF.toInt(), 0xFFFFD700.toInt())
            else -> emptyList()
        }

        val tailLenFraction = 0.25f   // 25% perimeter untuk ekor
        val dotCount = 20              // 20 dot per ekor

        headColors.forEachIndexed { idx, color ->
            val baseT = rotationDeg / 360f
            val headT = (baseT + idx * 0.5f) % 1f

            for (i in 0 until dotCount) {
                val t = headT - (i.toFloat() / dotCount) * tailLenFraction
                val wrapped = ((t % 1f) + 1f) % 1f

                val pos = FloatArray(2)
                pathMeasure.getPosTan(wrapped * pathLength, pos, null)

                // Alpha: kepala 100% → ekor 0%
                val alphaBase = 1f - (i.toFloat() / dotCount)
                val alpha = if (isPressed) alphaBase * flash else alphaBase

                // Radius dot LEBIH KECIL: 25% sampai 50% glowPx
                val dotRadius = glowPx * (0.25f + 0.25f * alphaBase)

                val headColor = if (isPressed) 0xFFFFD700.toInt() else color

                val paint = AndroidPaint().apply {
                    isAntiAlias = true
                    shader = RadialGradient(
                        pos[0], pos[1], dotRadius,
                        intArrayOf(
                            colorWithAlpha(headColor, alpha * 0.9f),
                            colorWithAlpha(headColor, alpha * 0.4f),
                            colorWithAlpha(headColor, 0f)
                        ),
                        floatArrayOf(0f, 0.4f, 1f),
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
