package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicEvent
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedBg

@Composable
fun IslamicEventCard(
    event: IslamicEvent,
    modifier: Modifier = Modifier
) {
    val isUrgent = event.isUrgent // < 7 days

    val bgColor by animateColorAsState(
        targetValue = if (isUrgent) UrgentRedBg else Color(0x660A1822),
        label = "ev_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isUrgent) UrgentRed else Color(0x44FFD700),
        label = "ev_border"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(if (isUrgent) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 18.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📅 ",
                fontSize = 17.sp
            )
            Text(
                text = "${event.daysRemaining} hari ",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isUrgent) UrgentRed else IslamicGoldLight
            )
            Text(
                text = "menuju ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = event.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUrgent) Color.White else IslamicGold
            )
        }
    }
}
