package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TvSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    unit: String = "",
    formatter: ((Float) -> String)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var isSliderActive by remember { mutableStateOf(false) }

    // Border warna: hijau kalau aktif, emas terang kalau fokus, emas redup default
    val borderColor by animateColorAsState(
        targetValue = when {
            isSliderActive -> IslamicGreen
            isFocused -> Color(0xFFFFE44D)
            else -> IslamicGold.copy(alpha = 0.4f)
        },
        label = "slider_border"
    )

    val borderWidth by animateFloatAsState(
        targetValue = when {
            isSliderActive -> 3f
            isFocused -> 4f
            else -> 1.5f
        },
        label = "slider_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "slider_scale"
    )

    val shadowElevation by animateFloatAsState(
        targetValue = if (isFocused) 12f else 0f,
        label = "slider_shadow"
    )

    val stepSize: Float = if (steps > 0) {
        (valueRange.endInclusive - valueRange.start) / (steps + 1)
    } else {
        (valueRange.endInclusive - valueRange.start) / 20f
    }

    val displayValue = formatter?.invoke(value) ?: "${value.toInt()}$unit"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = shadowElevation.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x88FFD700),
                spotColor = Color(0x88FFD700)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSliderActive) Color(0x33A5D6A7) else Color(0x22000000))
            .border(
                width = borderWidth.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .onFocusChanged {
                isFocused = it.isFocused
                if (!it.isFocused) isSliderActive = false
            }
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                            isSliderActive = !isSliderActive
                            true
                        }
                        Key.DirectionLeft -> {
                            if (isSliderActive) {
                                val newValue = (value - stepSize).coerceIn(
                                    valueRange.start,
                                    valueRange.endInclusive
                                )
                                onValueChange(newValue)
                                true
                            } else false
                        }
                        Key.DirectionRight -> {
                            if (isSliderActive) {
                                val newValue = (value + stepSize).coerceIn(
                                    valueRange.start,
                                    valueRange.endInclusive
                                )
                                onValueChange(newValue)
                                true
                            } else false
                        }
                        else -> false
                    }
                } else false
            }
            .clickable {
                isSliderActive = !isSliderActive
            }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSliderActive -> IslamicGreen
                    isFocused -> Color(0xFFFFE44D)
                    else -> IslamicGoldLight
                }
            )
            Text(
                text = displayValue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSliderActive -> IslamicGreen
                    isFocused -> Color(0xFFFFE44D)
                    else -> TextPrimary
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x33FFFFFF))
            )

            // Progress track
            val progressFraction = if (valueRange.endInclusive > valueRange.start) {
                (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            } else 0f

            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSliderActive) IslamicGreen else IslamicGold)
            )

            // Thumb
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                    .height(28.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isSliderActive) 30.dp else 26.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSliderActive -> IslamicGreen
                                isFocused -> Color(0xFFFFE44D)
                                else -> IslamicGold
                            }
                        )
                        .border(3.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "◀",
                    fontSize = 16.sp,
                    color = if (isSliderActive) IslamicGreen else TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSliderActive) "KIRI / KANAN untuk atur" else "Tekan OK untuk aktifkan",
                    fontSize = 11.sp,
                    color = if (isSliderActive) IslamicGreen else TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "▶",
                    fontSize = 16.sp,
                    color = if (isSliderActive) IslamicGreen else TextSecondary
                )
            }
            if (isSliderActive) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(IslamicGreen.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AKTIF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreen
                    )
                }
            }
        }
    }
}
