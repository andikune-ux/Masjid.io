package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

@Composable
fun WeatherAmbientOverlay(
    modifier: Modifier = Modifier,
    weatherCondition: String = "Cerah", // "Cerah", "Berawan", "Hujan", "Gerimis"
    showBirds: Boolean = true
) {
    // Bird soaring animation cycle across screen (takes 35 seconds to fly across screen)
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    val birdProgress by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 35000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bird_progress"
    )

    // Wing flapping animation
    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing_flap"
    )

    // Cloud drift animation
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_drift"
    )

    // Rain drop animation
    val rainDropProg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_drop"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Weather condition effects
        when (weatherCondition) {
            "Hujan", "Gerimis" -> {
                val drops = 35
                val isDrizzle = weatherCondition == "Gerimis"
                val dropAlpha = if (isDrizzle) 0.25f else 0.45f
                val dropLength = if (isDrizzle) 20f else 45f

                for (i in 0 until drops) {
                    val startX = (w * (i.toFloat() / drops) + (i * 37f)) % w
                    val initialY = (i * 97f) % h
                    val currentY = (initialY + (rainDropProg * h)) % h

                    drawLine(
                        color = Color(0xFF90CAF9).copy(alpha = dropAlpha),
                        start = Offset(startX, currentY),
                        end = Offset(startX - 8f, currentY + dropLength),
                        strokeWidth = if (isDrizzle) 1.5f else 2.5f
                    )
                }
            }
            "Berawan" -> {
                // Soft floating cloud wisps
                val driftX = cloudOffset * w
                val cloudPath = Path().apply {
                    val cx = (driftX + 200f) % (w + 400f) - 200f
                    val cy = h * 0.12f
                    moveTo(cx, cy)
                    cubicTo(cx + 80f, cy - 25f, cx + 180f, cy - 15f, cx + 240f, cy + 10f)
                    cubicTo(cx + 280f, cy + 30f, cx + 220f, cy + 60f, cx + 160f, cy + 50f)
                    cubicTo(cx + 100f, cy + 65f, cx + 30f, cy + 40f, cx, cy)
                    close()
                }
                drawPath(cloudPath, color = Color.White.copy(alpha = 0.08f), style = Fill)
            }
            else -> {
                // Cerah: soft subtle sun glow in corner
                drawCircle(
                    color = Color(0x18FFD54F),
                    radius = 280f,
                    center = Offset(w * 0.95f, 40f)
                )
            }
        }

        // 2. Birds soaring across screen
        if (showBirds && birdProgress in -0.1f..1.1f) {
            val birdX = birdProgress * w
            val birdBaseY = h * 0.16f + (sin(birdProgress * Math.PI * 4).toFloat() * 25f)

            // Draw a small flock of 3 birds in V-formation
            val flockOffsets = listOf(
                Pair(0f, 0f),
                Pair(-45f, 25f),
                Pair(-85f, 45f)
            )

            for ((ox, oy) in flockOffsets) {
                val bx = birdX + ox
                val by = birdBaseY + oy
                val wingSpan = 18f
                val wingYOffset = wingFlap * 7f

                val birdPath = Path().apply {
                    // Left wing
                    moveTo(bx - wingSpan, by - wingYOffset)
                    quadraticTo(bx - (wingSpan * 0.5f), by - 2f, bx, by)
                    // Right wing
                    quadraticTo(bx + (wingSpan * 0.5f), by - 2f, bx + wingSpan, by - wingYOffset)
                }

                drawPath(
                    birdPath,
                    color = Color(0x60E0E6ED),
                    style = Stroke(width = 2.2f)
                )
            }
        }
    }
}
