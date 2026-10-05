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
import com.example.data.model.AppSettings
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedBg
import kotlinx.coroutines.delay

/**
 * WisdomCardCarousel — Karusel kartu nasihat/mutiara.
 *
 * V1.04.423 — Fix Tulisan Terpotong:
 *   - AUTO-SHRINK FONT: ukuran teks mengecil otomatis sesuai panjang teks
 *   - MULTI-LINE DINAMIS: 1-3 baris otomatis
 *   - TextOverflow.Clip (bukan Ellipsis) → teks utuh, tidak ada "..."
 *   - Padding dioptimalkan agar teks punya ruang maksimal
 *
 * Strategi berdasarkan panjang teks (jumlah karakter):
 *   ≤ 60  char → 14sp, 1 baris
 *   ≤ 100 char → 13sp, 2 baris
 *   ≤ 150 char → 12sp, 2 baris
 *   ≤ 200 char → 11sp, 3 baris
 *   > 200 char → 10sp, 3 baris
 */
@Composable
fun WisdomCardCarousel(
    upcomingEvent: IslamicEvent?,
    settings: AppSettings = AppSettings(),
    intervalSeconds: Int = 12,
    animationType: String = "Fade",
    modifier: Modifier = Modifier
) {
    val wisdomList = remember(
        settings.showAsmaulHusna,
        settings.showHaditsHarian,
        settings.showAyatQuran,
        settings.showDoaHarian,
        settings.showSunnahReminder
    ) {
        IslamicWisdomStore.getFilteredItems(settings)
    }

    var currentIndex by remember { mutableStateOf(0) }
    val safeInterval = intervalSeconds.coerceAtLeast(4)

    LaunchedEffect(safeInterval, wisdomList.size) {
        while (true) {
            delay(safeInterval * 1000L)
            if (wisdomList.isNotEmpty()) {
                currentIndex = (currentIndex + 1) %
                        (wisdomList.size + if (upcomingEvent != null) 1 else 0)
            }
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
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = transitionSpec,
            label = "wisdom_anim"
        ) { index ->
            if (upcomingEvent != null && index == wisdomList.size) {
                // ============================================================
                // KARTU EVENT — Countdown hari besar Islam
                // ============================================================
                val isUrgent = upcomingEvent.isUrgent
                val bgColor = if (isUrgent) UrgentRedBg else Color(0x660A1822)
                val borderColor = if (isUrgent) UrgentRed else Color(0x44FFD700)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(if (isUrgent) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "📅 ", fontSize = 14.sp)
                    Text(
                        text = "${upcomingEvent.daysRemaining} hari ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isUrgent) UrgentRed else IslamicGoldLight
                    )
                    Text(
                        text = "menuju ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = upcomingEvent.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) Color.White else IslamicGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else if (wisdomList.isNotEmpty()) {
                // ============================================================
                // KARTU WISDOM — Auto-shrink font + multi-line
                // ============================================================
                val item = wisdomList[index % wisdomList.size]
                val displayText = "${item.title}: ${item.translation}"
                val textLength = displayText.length

                // V1.04.423: Auto-shrink font + maxLines dinamis
                val (adaptiveFontSize, adaptiveMaxLines) = when {
                    textLength <= 60 -> 14.sp to 1
                    textLength <= 100 -> 13.sp to 2
                    textLength <= 150 -> 12.sp to 2
                    textLength <= 200 -> 11.sp to 3
                    else -> 10.sp to 3
                }

                // Tinggi baris dinamis sesuai font size
                val adaptiveLineHeight = when {
                    textLength <= 60 -> 18.sp
                    textLength <= 100 -> 17.sp
                    textLength <= 150 -> 16.sp
                    textLength <= 200 -> 15.sp
                    else -> 14.sp
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x880C1B26))
                        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // ===== CATEGORY BADGE =====
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(IslamicGold.copy(alpha = 0.2f))
                            .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // ===== TEKS UTAMA — auto-shrink + multi-line =====
                    Text(
                        text = displayText,
                        fontSize = adaptiveFontSize,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        maxLines = adaptiveMaxLines,
                        lineHeight = adaptiveLineHeight,
                        // V1.04.423: Clip (bukan Ellipsis) → teks utuh tanpa "..."
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // ===== SOURCE =====
                    Text(
                        text = item.source,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
