package dev.andikune.masjidio.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class untuk style fokus yang konsisten.
 *
 * PRINSIP:
 *   - Border: INSTANT (langsung muncul saat fokus pindah)
 *   - Scale: smooth 150ms (animasi menyusul)
 *   - Shadow: smooth 150ms
 */
data class TvFocusStyle(
    val borderWidth: Dp = 4.dp,
    val borderColor: Color = Color(0xFFFFD700),
    val focusedBorderColor: Color = Color(0xFFFFE44D),
    val cornerRadius: Dp = 12.dp,
    val scaleOnFocus: Float = 1.05f,
    val shadowElevation: Dp = 12.dp,
    val shadowColor: Color = Color(0x88FFD700),
    val animationDurationMs: Int = 150
)

// ============================================================
// TV FOCUSABLE PRO
// Border INSTANT + scale smooth 150ms
// ============================================================
fun Modifier.tvFocusablePro(
    style: TvFocusStyle = TvFocusStyle(),
    enabled: Boolean = true
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) style.scaleOnFocus else 1f,
        animationSpec = tween(durationMillis = style.animationDurationMs),
        label = "tv_focus_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused && enabled) style.shadowElevation else 0.dp,
        animationSpec = tween(style.animationDurationMs),
        label = "tv_focus_shadow"
    )

    // BORDER INSTANT — tidak animate, langsung pakai value
    val borderWidth: Dp = if (isFocused && enabled) style.borderWidth else 0.dp
    val borderColor: Color = if (isFocused && enabled) style.focusedBorderColor else Color.Transparent

    this
        .scale(scale)
        .shadow(
            elevation = shadowElevation,
            shape = RoundedCornerShape(style.cornerRadius),
            ambientColor = style.shadowColor,
            spotColor = style.shadowColor
        )
        .border(
            width = borderWidth,
            color = borderColor,
            shape = RoundedCornerShape(style.cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}

// ============================================================
// TV FOCUSABLE SIMPLE
// ============================================================
fun Modifier.tvFocusableSimple(
    borderWidth: Dp = 4.dp,
    borderColor: Color = Color(0xFFFFD700),
    focusedBorderColor: Color = Color(0xFFFFE44D),
    cornerRadius: Dp = 12.dp,
    scaleOnFocus: Float = 1.05f,
    enabled: Boolean = true
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) scaleOnFocus else 1f,
        animationSpec = tween(150),
        label = "tv_focus_simple_scale"
    )

    val currentBorderWidth: Dp = if (isFocused && enabled) borderWidth else 0.dp
    val currentBorderColor: Color = if (isFocused && enabled) focusedBorderColor else Color.Transparent

    this
        .scale(scale)
        .border(
            width = currentBorderWidth,
            color = currentBorderColor,
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}

// ============================================================
// TV FOCUSABLE ICON
// ============================================================
fun Modifier.tvFocusableIcon(
    cornerRadius: Dp = 50.dp,
    scaleOnFocus: Float = 1.1f,
    enabled: Boolean = true
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) scaleOnFocus else 1f,
        animationSpec = tween(120),
        label = "tv_focus_icon_scale"
    )

    val currentBorderWidth: Dp = if (isFocused && enabled) 4.dp else 0.dp
    val currentBorderColor: Color = if (isFocused && enabled) Color(0xFFFFE44D) else Color.Transparent

    this
        .scale(scale)
        .border(
            width = currentBorderWidth,
            color = currentBorderColor,
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}

// ============================================================
// TV FOCUSABLE CARD
// ============================================================
fun Modifier.tvFocusableCard(
    cornerRadius: Dp = 16.dp,
    scaleOnFocus: Float = 1.03f,
    enabled: Boolean = true
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) scaleOnFocus else 1f,
        animationSpec = tween(150),
        label = "tv_focus_card_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused && enabled) 16.dp else 0.dp,
        animationSpec = tween(150),
        label = "tv_focus_card_shadow"
    )

    val currentBorderWidth: Dp = if (isFocused && enabled) 4.dp else 1.dp
    val currentBorderColor: Color = if (isFocused && enabled) Color(0xFFFFE44D)
    else Color(0x33FFD700)

    this
        .scale(scale)
        .shadow(
            elevation = shadowElevation,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = Color(0x66FFD700),
            spotColor = Color(0x66FFD700)
        )
        .border(
            width = currentBorderWidth,
            color = currentBorderColor,
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}
