package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextPrimary

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
        targetValue = if (isSettingsFocused) IslamicGoldLight else IslamicGold.copy(alpha = 0.5f),
        label = "settings_border"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ============ KIRI: LOGO MASJID.IO ============
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.widthIn(min = 260.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(IslamicGold, IslamicGoldLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = "Logo Masjid",
                    tint = Color(0xFF09141D),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "MASJID.IO",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = IslamicGoldLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // ============ JARAK KIRI-TENGAH ============
        Spacer(modifier = Modifier.width(24.dp))

        // ============ TENGAH: CUACA & LOKASI ============
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xCC091620))
                .border(
                    width = 1.dp,
                    color = IslamicGold.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icon cuaca
            Icon(
                imageVector = if (weatherCondition.contains("Cerah", true) ||
                    weatherCondition.contains("Clear", true))
                    Icons.Default.WbSunny
                else Icons.Default.Cloud,
                contentDescription = weatherCondition,
                tint = if (weatherCondition.contains("Cerah", true) ||
                    weatherCondition.contains("Clear", true))
                    IslamicGold
                else Color(0xFF9FB8D0),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Suhu
            Text(
                text = "$temperature°C",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(10.dp))

            // Pemisah
            Box(
                modifier = Modifier
                    .size(width = 1.dp, height = 20.dp)
                    .background(IslamicGold.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(10.dp))

            // Lokasi
            Text(
                text = locationName.ifBlank { "Lokasi belum diatur" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = IslamicGoldLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // ============ JARAK TENGAH-KANAN ============
        Spacer(modifier = Modifier.width(24.dp))

        // ============ KANAN: TOMBOL SETTINGS ============
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xCC091620))
                .border(
                    width = if (isSettingsFocused) 3.dp else 1.5.dp,
                    color = settingsBorderColor,
                    shape = CircleShape
                )
                .onFocusChanged { isSettingsFocused = it.isFocused }
                .focusable()
                .clickable { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Pengaturan",
                tint = if (isSettingsFocused) IslamicGoldLight else IslamicGold,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
