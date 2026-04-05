package com.piggypulse.android.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun PiggyPulseTheme(
    themeManager: ThemeManager? = null,
    colorThemeOverride: ColorTheme? = null,
    darkThemeOverride: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val colorTheme = if (themeManager != null) {
        val theme by themeManager.colorTheme.collectAsState()
        colorThemeOverride ?: theme
    } else {
        colorThemeOverride ?: ColorTheme.Nebula
    }

    val isDark = if (themeManager != null) {
        val mode by themeManager.appearanceMode.collectAsState()
        darkThemeOverride ?: when (mode) {
            AppearanceMode.System -> isSystemInDarkTheme()
            AppearanceMode.Light -> false
            AppearanceMode.Dark -> true
        }
    } else {
        darkThemeOverride ?: isSystemInDarkTheme()
    }

    val surface = if (isDark) DarkSurface else LightSurface

    val ppColors = PiggyPulseColors(
        primary = colorTheme.primary,
        secondary = colorTheme.secondary,
        tertiary = colorTheme.tertiary,
        destructive = Destructive,
        gradientStart = colorTheme.gradient.first,
        gradientEnd = colorTheme.gradient.second,
        background = surface.background,
        card = surface.card,
        elevated = surface.elevated,
        border = surface.border,
        borderHover = surface.borderHover,
        textPrimary = surface.textPrimary,
        textSecondary = surface.textSecondary,
        textTertiary = surface.textTertiary,
        dataPalette = colorTheme.dataPalette,
    )

    val materialColorScheme = if (isDark) {
        darkColorScheme(
            primary = colorTheme.primary,
            secondary = colorTheme.secondary,
            tertiary = colorTheme.tertiary,
            background = surface.background,
            surface = surface.card,
            surfaceVariant = surface.elevated,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onBackground = surface.textPrimary,
            onSurface = surface.textPrimary,
            onSurfaceVariant = surface.textSecondary,
            outline = surface.border,
            outlineVariant = surface.borderHover,
            error = Destructive,
            onError = Color.White,
        )
    } else {
        lightColorScheme(
            primary = colorTheme.primary,
            secondary = colorTheme.secondary,
            tertiary = colorTheme.tertiary,
            background = surface.background,
            surface = surface.card,
            surfaceVariant = surface.elevated,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onBackground = surface.textPrimary,
            onSurface = surface.textPrimary,
            onSurfaceVariant = surface.textSecondary,
            outline = surface.border,
            outlineVariant = surface.borderHover,
            error = Destructive,
            onError = Color.White,
        )
    }

    CompositionLocalProvider(LocalPiggyPulseColors provides ppColors) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = PiggyPulseTypography,
            content = content,
        )
    }
}

object PpTheme {
    val colors: PiggyPulseColors
        @Composable
        get() = LocalPiggyPulseColors.current
}
