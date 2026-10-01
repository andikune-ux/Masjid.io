package com.example.ui.components

import android.graphics.Matrix
import android.graphics.Paint as AndroidPaint
import android.graphics.RectF
import android.graphics.SweepGradient
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * NeonFocusBorder — Border fokus dengan animasi rainbow NEON.
 *
 * V2 (FIX):
 *   - Border TETAP DI TEMPAT (bentuk tidak muter)
 *   - Warna rainbow yang MUTER mengelilingi border (via SweepGradient + Matrix)
 *   - Saat D-pad ditekan (OK) → warna emas berkedip cepat
 */
@Composable
fun NeonFocusBorder(
    focused: Boolean,
    pressed: Boolean = false,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 5.dp,
    cornerRadius: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    // Animasi hanya jalan saat focused (hemat GPU)
    val infiniteTransition = rememberInfiniteTransition(label = "neon_border")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "neon_rotation"
    )

    val flashAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash_alpha"
    )

    Box(modifier = modifier) {
        // Konten utama
        content()

        // Border hanya digambar saat focused
        if (focused) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = borderWidth.toPx()
                val inset = stroke / 2f
                val radiusPx = cornerRadius.toPx()
                val cx = size.width / 2f
                val cy = size.height / 2f

                drawIntoCanvas { canvas ->
                    val paint = AndroidPaint().apply {
                        isAntiAlias = true
                        style = AndroidPaint.Style.STROKE
                        strokeWidth = stroke
                    }

                    if (pressed) {
                        // ===== EFEK DI-TEKAN: EMAS BERKEDIP =====
                        paint.color = 0xFFFFD700.toInt()
                        paint.alpha = (flashAlpha * 255).toInt()
                    } else {
                        // ===== RAINBOW GRADIENT YANG BERPUTAR =====
                        // Warna 8 titik (loop) — smooth transition antar warna
                        val rainbowColors = intArrayOf(
                            0xFFFF1744.toInt(), // Merah
                            0xFFFF6D00.toInt(), // Oranye
                            0xFFFFEA00.toInt(), // Kuning
                            0xFF00E676.toInt(), // Hijau
                            0xFF00E5FF.toInt(), // Cyan
                            0xFF2979FF.toInt(), // Biru
                            0xFFD500F9.toInt(), // Pink
                            0xFFFF1744.toInt()  // Merah (loop ke awal)
                        )

                        // SweepGradient: warna menyebar 360° dari center
                        val shader = SweepGradient(cx, cy, rainbowColors, null)

                        // Matrix rotate: putar gradient supaya warnanya "berjalan"
                        val matrix = Matrix()
                        matrix.setRotate(rotation, cx, cy)
                        shader.setLocalMatrix(matrix)

                        paint.shader = shader
                    }

                    // Gambar outline border (bentuk TETAP — hanya warna yang muter)
                    val rect = RectF(
                        inset,
                        inset,
                        size.width - inset,
                        size.height - inset
                    )
                    canvas.nativeCanvas.drawRoundRect(rect, radiusPx, radiusPx, paint)
                }
            }
        }
    }
}
