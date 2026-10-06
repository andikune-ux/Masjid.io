package dev.andikune.masjidio.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.andikune.masjidio.ui.theme.IslamicGoldLight

// Extension untuk memperjelas fokus D-pad di Android TV.
// Efek: tombol membesar + border tebal saat fokus.
//
// Cara pakai:
//   Box(
//       modifier = Modifier
//           .tvFocusable()
//           .clickable { ... }
//   )
fun Modifier.tvFocusable(
    borderWidth: Dp = 4.dp,
    borderColor: Color = IslamicGoldLight,
    cornerRadius: Dp = 12.dp,
    scaleOnFocus: Float = 1.05f
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleOnFocus else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 800f
        ),
        label = "tv_focus_scale"
    )

    this
        .scale(scale)
        .border(
            width = if (isFocused) borderWidth else 0.dp,
            color = if (isFocused) borderColor else Color.Transparent,
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable()
}

// Versi minimal: hanya border + scale, tanpa focusable.
// Cocok kalau komponen sudah punya focusable sendiri.
fun Modifier.tvFocusBorder(
    borderWidth: Dp = 4.dp,
    borderColor: Color = IslamicGoldLight,
    cornerRadius: Dp = 12.dp,
    scaleOnFocus: Float = 1.05f
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleOnFocus else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 800f
        ),
        label = "tv_focus_border_scale"
    )

    this
        .scale(scale)
        .border(
            width = if (isFocused) borderWidth else 0.dp,
            color = if (isFocused) borderColor else Color.Transparent,
            shape = RoundedCornerShape(cornerRadius)
        )
        .onFocusChanged { isFocused = it.isFocused }
}
