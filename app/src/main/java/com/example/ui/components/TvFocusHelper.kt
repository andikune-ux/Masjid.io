package com.example.ui.components

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
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.DpOffset

/**
 * Data class untuk style fokus yang konsisten.
 */
data class TvFocusStyle(
    val borderWidth: Dp = 4.dp,
    val borderColor: Color = Color(0xFFFFD700),         // Emas
    val focusedBorderColor: Color = Color(0xFFFFE44D),   // Emas lebih terang
    val cornerRadius: Dp = 12.dp,
    val scaleOnFocus: Float = 1.05f,
    val shadowElevation: Dp = 12.dp,
    val shadowColor: Color = Color(0x88FFD700),
    val animationDurationMs: Int = 200
)

/**
 * Extension untuk membuat komponen fokus-able dengan
 * border tebal + tombol membesar + shadow emas.
 *
 * Cara pakai:
 *   Box(
 *       modifier = Modifier
 *           .tvFocusablePro()
 *           .clickable { ... }
 *   )
 */
fun Modifier.tvFocusablePro(
    style: TvFocusStyle = TvFocusStyle(),
    enabled: Boolean = true
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) style.scaleOnFocus else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 800f
        ),
        label = "tv_focus_scale"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused && enabled) style.borderWidth else 0.dp,
        animationSpec = tween(style.animationDurationMs),
        label = "tv_focus_border_width"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused && enabled) style.shadowElevation else 0.dp,
        animationSpec = tween(style.animationDurationMs),
        label = "tv_focus_shadow"
    )

    val borderColor = if (isFocused) style.focusedBorderColor else style.borderColor

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
            color = if (isFocused && enabled) borderColor else Color.Transparent,
            shape = RoundedCornerShape(style.cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}

/**
 * Versi simple: hanya border + scale (tanpa shadow).
 * Cocok untuk kalau komponen sudah punya shadow sendiri.
 */
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
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "tv_focus_simple_scale"
    )

    this
        .scale(scale)
        .border(
            width = if (isFocused && enabled) borderWidth else 0.dp,
            color = if (isFocused && enabled) focusedBorderColor else Color.Transparent,
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}

/**
 * Versi compact untuk tombol-tombol kecil (icon button).
 */
fun Modifier.tvFocusableIcon(
    cornerRadius: Dp = 50.dp,   // 50.dp = lingkaran
    scaleOnFocus: Float = 1.1f,
    enabled: Boolean = true
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) scaleOnFocus else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 1000f),
        label = "tv_focus_icon_scale"
    )

    this
        .scale(scale)
        .border(
            width = if (isFocused && enabled) 4.dp else 0.dp,
            color = if (isFocused && enabled) Color(0xFFFFE44D) else Color.Transparent,
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}

/**
 * Versi untuk kartu besar (menu utama, officer card, dll).
 */
fun Modifier.tvFocusableCard(
    cornerRadius: Dp = 16.dp,
    scaleOnFocus: Float = 1.03f,
    enabled: Boolean = true
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused && enabled) scaleOnFocus else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 700f),
        label = "tv_focus_card_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused && enabled) 16.dp else 0.dp,
        animationSpec = tween(200),
        label = "tv_focus_card_shadow"
    )

    this
        .scale(scale)
        .shadow(
            elevation = shadowElevation,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = Color(0x66FFD700),
            spotColor = Color(0x66FFD700)
        )
        .border(
            width = if (isFocused && enabled) 4.dp else 1.dp,
            color = if (isFocused && enabled) Color(0xFFFFE44D)
            else Color(0x33FFD700),
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable(enabled)
}
