package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MasjidColorScheme = darkColorScheme(
    primary = IslamicGold,
    onPrimary = MosqueDeepBg,
    primaryContainer = IslamicGoldDark,
    onPrimaryContainer = IslamicGoldLight,
    secondary = IslamicGreen,
    onSecondary = MosqueDeepBg,
    tertiary = IslamicGoldLight,
    background = MosqueDeepBg,
    onBackground = TextPrimary,
    surface = MosqueSurface,
    onSurface = TextPrimary,
    surfaceVariant = MosqueCardBg,
    onSurfaceVariant = TextSecondary,
    outline = MosqueCardBorder
)

@Composable
fun MasjidTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MasjidColorScheme,
        typography = Typography,
        content = content
    )
}
