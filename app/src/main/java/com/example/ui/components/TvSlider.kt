package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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

/**
 * Slider khusus Android TV.
 *
 * Cara pakai:
 * 1. Fokus slider → tekan OK → slider "aktif"
 * 2. Saat aktif → tombol KIRI/KANAN untuk geser nilai
 * 3. Tekan OK lagi → slider "nonaktif"
 *
 * @param label Label di atas slider
 * @param value Nilai saat ini
 * @param onValueChange Callback saat nilai berubah
 * @param valueRange Range nilai (min..max)
 * @param steps Jumlah step (0 = smooth)
 * @param unit Satuan (contoh: "%", " menit")
 * @param formatter Custom formatter (opsional)
 */
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

    val borderColor by animateColorAsState(
        targetValue = when {
            isSliderActive -> IslamicGreen
            isFocused -> IslamicGoldLight
            else -> IslamicGold.copy(alpha = 0.4f)
        },
        label = "slider_border"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1f,
        label = "slider_scale"
    )

    // Hitung step size
    val stepSize: Float = if (steps > 0) {
        (valueRange.endInclusive - valueRange.start) / (steps + 1)
    } else {
        (valueRange.endInclusive - valueRange.start) / 20f
    }

    // Format nilai untuk ditampilkan
    val displayValue = formatter?.invoke(value) ?: "${value.toInt()}$unit"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSliderActive) Color(0x33A5D6A7) else Color(0x22000000))
            .border(
                width = if (isFocused) 3.dp else 1.5.dp,
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
        // HEADER: label + nilai
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSliderActive) IslamicGreen else IslamicGoldLight
            )
            Text(
                text = displayValue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSliderActive) IslamicGreen else TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SLIDER BAR
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

            // Thumb (lingkaran)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                    .height(24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isSliderActive) 28.dp else 24.dp)
                        .clip(CircleShape)
                        .background(if (isSliderActive) IslamicGreen else IslamicGold)
                        .border(3.dp, Color.White, CircleShape)
                )
            }
        }

        // HINT
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

/**
 * Versi toggle sederhana untuk TV (on/off).
 */
@Composable
fun TvToggle(
    label: String,
    description: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) 1.02f else 1f,
        label = "toggle_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isFocused && enabled) Color(0x33FFD700) else Color(0x22000000)
            )
            .border(
                if (isFocused && enabled) 3.dp else 1.5.dp,
                if (isFocused && enabled) IslamicGoldLight
                else IslamicGold.copy(alpha = if (enabled) 0.5f else 0.2f),
                RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled)
            .clickable(enabled = enabled) { onToggle(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) IslamicGoldLight else TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isChecked) IslamicGreen else Color(0x55FFFFFF))
                .padding(3.dp),
            contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color.White)
            )
        }
    }
}
