package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerItem
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PrayerCardsRow(
    prayerItems: List<PrayerItem>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gold_pulse"
    )

    // 6 Cards horizontally in 1 line, NO SCROLL, strictly proportional
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (item in prayerItems) {
            PrayerCard(
                item = item,
                pulseAlpha = pulseAlpha,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PrayerCard(
    item: PrayerItem,
    pulseAlpha: Float,
    modifier: Modifier = Modifier
) {
    val isNext = item.isNext
    val isPassed = item.isPassed
    val isActive = item.isActive

    val cardShape = RoundedCornerShape(14.dp)

    val bgBrush = when {
        isActive -> Brush.verticalGradient(listOf(Color(0xFF0F3B2A), Color(0xFF071F15)))
        isNext -> Brush.verticalGradient(listOf(Color(0xFF2C220E), Color(0xFF161106)))
        isPassed -> Brush.verticalGradient(listOf(Color(0x770D1B26), Color(0x55071118)))
        else -> Brush.verticalGradient(listOf(Color(0xBB0F2332), Color(0x990A1722)))
    }

    val borderColor = when {
        isActive -> IslamicGreen
        isNext -> IslamicGold.copy(alpha = pulseAlpha)
        isPassed -> Color(0x33446075)
        else -> Color(0x44264A66)
    }

    val borderWidth = when {
        isNext -> 2.dp
        isActive -> 2.dp
        else -> 1.dp
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .shadow(
                elevation = if (isNext) 12.dp else 2.dp,
                shape = cardShape,
                ambientColor = if (isNext) IslamicGold else Color.Black,
                spotColor = if (isNext) IslamicGold else Color.Black
            )
            .clip(cardShape)
            .background(bgBrush)
            .border(borderWidth, borderColor, cardShape)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // [Top] Header: Arabic Script + Status Badge (immune to wrapping/clipping)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Arabic Script Name
                Text(
                    text = item.id.arabicName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNext) IslamicGoldLight else TextSecondary.copy(alpha = 0.85f),
                    textAlign = TextAlign.Start,
                    maxLines = 1
                )

                if (isPassed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selesai",
                        tint = Color(0x9969F0AE),
                        modifier = Modifier.size(15.dp)
                    )
                } else if (isNext) {
                    // Small compact pill that never overflows
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(IslamicGold.copy(alpha = 0.3f))
                            .border(0.5.dp, IslamicGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.5.dp)
                    ) {
                        Text(
                            text = "SELANJUTNYA",
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGoldLight,
                            letterSpacing = 0.5.sp,
                            maxLines = 1
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(14.dp))
                }
            }

            // [Center] Prayer Time Digits (fitted cleanly at 32sp so it never gets cut off)
            Text(
                text = item.timeFormatted,
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp,
                    color = when {
                        isNext -> IslamicGoldLight
                        isActive -> IslamicGreen
                        isPassed -> Color(0xFFA5B8C7)
                        else -> TextPrimary
                    },
                    shadow = if (isNext) Shadow(
                        color = IslamicGold.copy(alpha = pulseAlpha),
                        offset = Offset(0f, 2f),
                        blurRadius = 8f
                    ) else null
                ),
                maxLines = 1
            )

            // [Bottom] Latin Name
            Text(
                text = item.id.displayName.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = when {
                    isNext -> IslamicGold
                    isActive -> IslamicGreen
                    isPassed -> Color(0xFF7E97AD)
                    else -> TextSecondary
                }
            )
        }
    }
}
