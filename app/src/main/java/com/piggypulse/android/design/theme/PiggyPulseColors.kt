package com.piggypulse.android.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class PiggyPulseColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val destructive: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val background: Color,
    val card: Color,
    val elevated: Color,
    val border: Color,
    val borderHover: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val dataPalette: List<Color>,
)

val LocalPiggyPulseColors = staticCompositionLocalOf {
    PiggyPulseColors(
        primary = Color.Unspecified,
        secondary = Color.Unspecified,
        tertiary = Color.Unspecified,
        destructive = Color.Unspecified,
        gradientStart = Color.Unspecified,
        gradientEnd = Color.Unspecified,
        background = Color.Unspecified,
        card = Color.Unspecified,
        elevated = Color.Unspecified,
        border = Color.Unspecified,
        borderHover = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textTertiary = Color.Unspecified,
        dataPalette = emptyList(),
    )
}
