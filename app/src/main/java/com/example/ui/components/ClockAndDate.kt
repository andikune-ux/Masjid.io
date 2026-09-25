package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ClockAndDate(
    timeString: String, // "12:34:56"
    hijriDateString: String, // "17 Rajab 1447 H"
    gregorianDateString: String, // "Jum'at, 24 September 2026"
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Grand Digital Clock - Refined font size from 82sp to 54sp for elegant breathing room
        Text(
            text = timeString,
            style = TextStyle(
                fontSize = 54.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 3.sp,
                color = Color.White,
                shadow = Shadow(
                    color = Color(0x99000000),
                    offset = Offset(0f, 4f),
                    blurRadius = 12f
                )
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Dual Calendar Date
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = hijriDateString,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0x66FFD700),
                        offset = Offset(0f, 2f),
                        blurRadius = 6f
                    )
                )
            )

            Text(
                text = "  •  ",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGold.copy(alpha = 0.6f)
            )

            Text(
                text = gregorianDateString,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}
