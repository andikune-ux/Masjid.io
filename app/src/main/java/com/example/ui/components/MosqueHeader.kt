package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight

@Composable
fun MosqueHeader(
    mosqueName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Decorative Divider
        Canvas(modifier = Modifier.width(60.dp).height(12.dp)) {
            drawLine(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, IslamicGold)),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 2f
            )
            drawCircle(
                color = IslamicGold,
                radius = 3f,
                center = Offset(size.width, size.height / 2)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Mosque Name
        Text(
            text = mosqueName.uppercase(),
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp,
            color = IslamicGoldLight
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Right Decorative Divider
        Canvas(modifier = Modifier.width(60.dp).height(12.dp)) {
            drawLine(
                brush = Brush.horizontalGradient(listOf(IslamicGold, Color.Transparent)),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 2f
            )
            drawCircle(
                color = IslamicGold,
                radius = 3f,
                center = Offset(0f, size.height / 2)
            )
        }
    }
}
