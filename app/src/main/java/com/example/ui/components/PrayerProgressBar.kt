package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary

@Composable
fun PrayerProgressBar(
    nextPrayerName: String,
    secondsRemaining: Long,
    progress: Float, // 0.0 to 1.0 real-time ratio
    modifier: Modifier = Modifier
) {
    val hours = secondsRemaining / 3600
    val minutes = (secondsRemaining % 3600) / 60
    val seconds = secondsRemaining % 60
    val formattedCountdown = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    // Animated glow pulse on progress head
    val infiniteTransition = rememberInfiniteTransition(label = "progress_glow")
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_rad"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Text Countdown Badge: ⏱️ Menuju [Next Prayer] dalam HH:mm:ss
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Text(
                text = "⏱️ Menuju $nextPrayerName dalam ",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = formattedCountdown,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = IslamicGoldLight
            )
            Text(
                text = "  (${ (progress * 100).toInt() }%)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = IslamicGold.copy(alpha = 0.8f)
            )
        }

        // Horizontal Real-Time Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0B1720))
                .border(1.dp, Color(0x44FFD700), RoundedCornerShape(8.dp))
        ) {
            // Filled dynamic progress
            val clampedProgress = progress.coerceIn(0.01f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(clampedProgress)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF0F4C3A),
                                IslamicGreen,
                                IslamicGold,
                                IslamicGoldLight
                            )
                        )
                    )
            )

            // Luminous glowing dot cursor at the head of progress
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(clampedProgress)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(16.dp)
                        .shadow(
                            elevation = 8.dp * glowRadius,
                            shape = CircleShape,
                            ambientColor = IslamicGold,
                            spotColor = Color.White
                        )
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, IslamicGold, CircleShape)
                )
            }
        }
    }
}
