package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary

/**
 * RunningTextMarquee — Teks berjalan di bawah layar utama.
 *
 * V1.04.422 FIX: Ganti fillMaxSize() → fillMaxWidth() di Row.
 * Bug: Row pakai fillMaxSize() bikin tinggi fullscreen → layar stuck.
 */
@Composable
fun RunningTextMarquee(
    text: String,
    speed: Int = 5,
    fontSize: Int = 20,
    modifier: Modifier = Modifier
) {
    // GUARD: Kalau text kosong, jangan render
    if (text.isBlank()) return

    // Speed 1-10
    val safeSpeed = speed.coerceIn(1, 10)
    val speedMultiplier = when (safeSpeed) {
        1 -> 40.0f
        2 -> 30.0f
        3 -> 22.0f
        4 -> 15.0f
        5 -> 8.0f
        6 -> 4.5f
        7 -> 2.0f
        8 -> 1.0f
        9 -> 0.5f
        10 -> 0.25f
        else -> 8.0f
    }

    val baseDurationMs = (text.length * 100 * speedMultiplier)
        .toInt()
        .coerceAtLeast(3000)

    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = baseDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "marquee_offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)  // ← FIX: kasih height tetap biar tidak fullscreen
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF07121A),
                        Color(0xFF0F2636),
                        Color(0xFF07121A)
                    )
                )
            )
            .border(
                1.dp,
                Color(0x33FFD700),
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()  // ← FIX: dari fillMaxSize() jadi fillMaxWidth()
                .height(40.dp)   // ← FIX: batasi tinggi Row
        ) {
            // Gold Info Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(IslamicGold.copy(alpha = 0.25f))
                    .border(1.dp, IslamicGold, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "INFO MASJID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = IslamicGoldLight,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))

            // Marquee Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clipToBounds(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = text,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            constraints.copy(maxWidth = Int.MAX_VALUE)
                        )
                        val containerWidth = constraints.maxWidth
                        val totalDistance = containerWidth + placeable.width
                        val currentX = containerWidth - (totalDistance * scrollOffset).toInt()
                        layout(placeable.width, placeable.height) {
                            placeable.place(currentX, 0)
                        }
                    }
                )
            }
        }
    }
}
