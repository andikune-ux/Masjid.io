package dev.andikune.masjidio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

data class NeonColors(
    val outer: Color = Color(0xFF00E676),
    val inner: Color = Color(0xFF69F0AE),
    val glow: Color = Color(0xFF00E676).copy(alpha = 0.3f)
)

object NeonDefaults {
    val colors = NeonColors()
    val borderWidthDp = 2.dp
    val innerBorderWidthDp = 1.dp
    val scaleFactor = 1.05f
    val glowRadiusDp = 8.dp
}

fun Modifier.neonFocusBorder(
    focusRequester: FocusRequester? = null,
    showInitialFocus: Boolean = false,
    neonColors: NeonColors = NeonDefaults.colors,
): Modifier = composed {
    var isFocused by remember { mutableStateOf(showInitialFocus) }
    val infiniteTransition = rememberInfiniteTransition(label = "neonGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) NeonDefaults.scaleFactor else 1f,
        animationSpec = tween(150),
        label = "focusScale"
    )

    this
        .then(
            if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
        )
        .onFocusChanged { state ->
            isFocused = state.isFocused || (showInitialFocus && !state.hasFocus)
        }
        .scale(scale)
        .then(
            if (isFocused) {
                Modifier
                    .graphicsLayer {
                        shadowElevation = NeonDefaults.glowRadiusDp.toPx()
                        ambientShadowColor = neonColors.glow.copy(alpha = glowAlpha)
                        spotShadowColor = neonColors.glow.copy(alpha = glowAlpha)
                    }
                    .border(
                        width = NeonDefaults.borderWidthDp,
                        color = neonColors.outer.copy(alpha = glowAlpha),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(all=2.dp)
                    .border(
                        width=NeonDefaults.innerBorderWidthDp,
                        color=neonColors.inner.copy(alpha=glowAlpha*0.7f),
                        shape=RoundedCornerShape(10.dp)
                    )
            } else {
                Modifier.border(
                    width=1.dp,
                    color=Color.Transparent,
                    shape=RoundedCornerShape(12.dp)
                )
            }
        )
}
