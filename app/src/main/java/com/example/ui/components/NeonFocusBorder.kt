package com.example.ui.components

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * NeonFocusBorder — Border fokus dengan animasi rainbow neon.
 *
 * Fitur:
 *   - 8 warna smooth (merah → oranye → kuning → hijau → cyan → biru → pink → merah)
 *   - Animasi berputar searah jarum jam (3 detik per putaran)
 *   - Saat D-pad ditekan (OK) → warna emas berkedip cepat
 *   - Hanya aktif saat `focused = true` (hemat GPU)
 *
 * Cara pakai:
 *   NeonFocusBorder(
 *       focused = isFocused,
 *       pressed = isPressed,
 *       cornerRadius = 12.dp
 *   ) {
 *       // konten di sini
 *   }
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

                if (pressed) {
                    // ===== EFEK DI-TEKAN: EMAS BERKEDIP =====
                    drawRoundRect(
                        color = Color(0xFFFFD700).copy(alpha = flashAlpha),
                        topLeft = Offset(inset, inset),
                        size = Size(
                            width = size.width - stroke,
                            height = size.height - stroke
                        ),
                        cornerRadius = CornerRadius(radiusPx, radiusPx),
                        style = Stroke(width = stroke * 1.3f)
                    )
                } else {
                    // ===== RAINBOW BERPUTAR =====
                    val rainbowColors = listOf(
                        Color(0xFFFF1744),  // Merah
                        Color(0xFFFF6D00),  // Oranye
                        Color(0xFFFFEA00),  // Kuning
                        Color(0xFF00E676),  // Hijau
                        Color(0xFF00E5FF),  // Cyan
                        Color(0xFF2979FF),  // Biru
                        Color(0xFFD500F9),  // Pink
                        Color(0xFFFF1744)   // Merah (loop)
                    )

                    val sweep = Brush.sweepGradient(
                        colors = rainbowColors,
                        center = Offset(size.width / 2f, size.height / 2f)
                    )

                    rotate(rotation, pivot = Offset(size.width / 2f, size.height / 2f)) {
                        drawRoundRect(
                            brush = sweep,
                            topLeft = Offset(inset, inset),
                            size = Size(
                                width = size.width - stroke,
                                height = size.height - stroke
                            ),
                            cornerRadius = CornerRadius(radiusPx, radiusPx),
                            style = Stroke(width = stroke)
                        )
                    }
                }
            }
        }
    }
}
