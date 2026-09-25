package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArabesquePattern(
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0x15FFD700)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val step = 140f

        // Draw subtle Islamic 8-point geometric star lattice grid
        for (x in 0..((w / step).toInt() + 1)) {
            for (y in 0..((h / step).toInt() + 1)) {
                val cx = x * step
                val cy = y * step
                val r1 = step * 0.22f
                val r2 = step * 0.12f

                val path = Path()
                val points = 16
                for (i in 0 until points) {
                    val angle = (i * Math.PI / 8.0)
                    val r = if (i % 2 == 0) r1 else r2
                    val px = cx + (r * cos(angle)).toFloat()
                    val py = cy + (r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                drawPath(path, color = lineColor, style = Stroke(width = 1.2f))

                // Delicate connection lines
                drawLine(
                    color = lineColor.copy(alpha = 0.05f),
                    start = Offset(cx - r1, cy),
                    end = Offset(cx + r1, cy),
                    strokeWidth = 1f
                )
                drawLine(
                    color = lineColor.copy(alpha = 0.05f),
                    start = Offset(cx, cy - r1),
                    end = Offset(cx, cy + r1),
                    strokeWidth = 1f
                )
            }
        }
    }
}
