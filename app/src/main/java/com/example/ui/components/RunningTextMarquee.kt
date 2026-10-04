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
 * V1.04.418: Speed 1 dibuat JAUH LEBIH LAMBAT (40x multiplier)
 * biar teks panjang bisa dibaca pelan & tenang di TV masjid.
 *
 * @param text Teks yang akan berjalan
 * @param speed Kecepatan 1-10 (1 = PALING LAMBAT, 5 = normal, 10 = paling cepat)
 * @param fontSize Ukuran huruf dalam sp
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

    // ============================================================
    // V1.04.418 — KECEPATAN DIPERLAMBAT (formula baru)
    // ============================================================
    // Speed 1  = 40.0x (PALING LAMBAT — ±6-7 menit untuk teks panjang)
    // Speed 2  = 30.0x (Sangat lambat — ±5 menit)
    // Speed 3  = 22.0x (Lambat — ±3,5 menit)
    // Speed 4  = 15.0x (Agak lambat — ±2,5 menit)
    // Speed 5  = 8.0x  (Sedang — ±1,5 menit)
    // Speed 6  = 4.5x  (Normal — ±50 detik)
    // Speed 7  = 2.0x  (Agak cepat — ±25 detik)
    // Speed 8  = 1.0x  (Cepat — ±12 detik)
    // Speed 9  = 0.5x  (Sangat cepat — ±6 detik)
    // Speed 10 = 0.25x (Paling cepat — ±3 detik)
    // ============================================================
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

    // Durasi dasar: panjang teks × 100 ms × multiplier
    // Minimal 3 detik (biar teks pendek tidak terlalu cepat)
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
            modifier = Modifier.fillMaxSize()
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
                    .clipToBounds()
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
