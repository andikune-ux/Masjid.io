package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicEvent
import com.example.data.local.IslamicWisdomStore
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedBg
import kotlinx.coroutines.delay

@Composable
fun WisdomCardCarousel(
    upcomingEvent: IslamicEvent?,
    intervalSeconds: Int = 12,
    animationType: String = "Fade",
    modifier: Modifier = Modifier
) {
    val wisdomList = IslamicWisdomStore.wisdomCards
    var currentIndex by remember { mutableStateOf(0) }

    val safeInterval = intervalSeconds.coerceAtLeast(4)

    LaunchedEffect(safeInterval) {
        while (true) {
            delay(safeInterval * 1000L)
            currentIndex = (currentIndex + 1) % (wisdomList.size + if (upcomingEvent != null) 1 else 0)
        }
    }

    val transitionSpec: AnimatedContentTransitionScope<Int>.() -> ContentTransform = {
        when (animationType) {
            "Slide" -> {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(tween(500)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(500)
                        ) + fadeOut(tween(500))
            }
            "Scale" -> {
                (scaleIn(initialScale = 0.85f, animationSpec = tween(500)) + fadeIn(tween(500))) togetherWith
                        (scaleOut(targetScale = 1.1f, animationSpec = tween(500)) + fadeOut(tween(500)))
            }
            else -> { // Fade
                fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(600))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = transitionSpec,
            label = "wisdom_anim"
        ) { index ->
            if (upcomingEvent != null && index == wisdomList.size) {
                // Event countdown card
                val isUrgent = upcomingEvent.isUrgent
                val bgColor = if (isUrgent) UrgentRedBg else Color(0x660A1822)
                val borderColor = if (isUrgent) UrgentRed else Color(0x44FFD700)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(if (isUrgent) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "📅 ", fontSize = 15.sp)
                    Text(
                        text = "${upcomingEvent.daysRemaining} hari ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isUrgent) UrgentRed else IslamicGoldLight
                    )
                    Text(
                        text = "menuju ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = upcomingEvent.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) Color.White else IslamicGold
                    )
                }
            } else {
                val item = wisdomList[index % wisdomList.size]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x880C1B26))
                        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(IslamicGold.copy(alpha = 0.2f))
                            .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Title & translation
                    Text(
                        text = "${item.title}: \"${item.translation}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Source
                    Text(
                        text = item.source,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicGold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
