package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TopBar(
    locationName: String,
    dayDateString: String = "",
    currentTimeString: String = "",
    temperature: Int = 30,
    weatherCondition: String = "Cerah",
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSettingsFocused by remember { mutableStateOf(false) }

    val settingsBorderColor by animateColorAsState(
        targetValue = if (isSettingsFocused) IslamicGoldLight else Color(0x33FFD700),
        label = "settings_focus"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [Sudut Kiri Atas] MASJID.IO Brand Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x550A1A24))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(IslamicGold, Color(0xFFB8860B)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = "Logo Masjid",
                    tint = Color(0xFF09141D),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MASJID.IO",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = IslamicGold
            )
        }

        // [Tengah] Cuaca & Lokasi Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(MosCardBgWithAlpha())
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            val weatherIcon = when (weatherCondition) {
                "Berawan" -> Icons.Default.Cloud
                "Hujan", "Gerimis" -> Icons.Default.WaterDrop
                else -> Icons.Default.WbSunny
            }
            val weatherColor = when (weatherCondition) {
                "Berawan" -> Color(0xFF90CAF9)
                "Hujan", "Gerimis" -> Color(0xFF64B5F6)
                else -> Color(0xFFFFD54F)
            }

            Icon(
                imageVector = weatherIcon,
                contentDescription = "Cuaca",
                tint = weatherColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$temperature°C $weatherCondition",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color(0x88FFFFFF))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = locationName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = IslamicGoldLight
            )
        }

        // [Sudut Kanan Atas] Real-Time Hari, Tanggal & Jam:Menit:Detik + Tombol Pengaturan
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Date & Time Badge
            if (dayDateString.isNotBlank() || currentTimeString.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x880C1B26))
                        .border(1.dp, Color(0x44FFD700), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Waktu Real-Time",
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currentTimeString,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        if (dayDateString.isNotBlank()) {
                            Text(
                                text = dayDateString,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IslamicGoldLight
                            )
                        }
                    }
                }
            }

            // Settings Gear Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSettingsFocused) Color(0x66B8860B) else Color(0x33000000))
                .border(
                    width = if (isSettingsFocused) 2.5.dp else 1.dp,
                    color = settingsBorderColor,
                    shape = CircleShape
                )
                .onFocusChanged { isSettingsFocused = it.isFocused }
                .focusable()
                .clickable { onSettingsClick() }
                .testTag("btn_settings"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Pengaturan",
                    tint = if (isSettingsFocused) IslamicGoldLight else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MosCardBgWithAlpha(): Color = Color(0x880C1B26)
