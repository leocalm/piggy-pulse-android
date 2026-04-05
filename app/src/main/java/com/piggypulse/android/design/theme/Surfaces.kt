package com.piggypulse.android.design.theme

import androidx.compose.ui.graphics.Color

data class SurfaceColors(
    val background: Color,
    val card: Color,
    val elevated: Color,
    val border: Color,
    val borderHover: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
)

val DarkSurface = SurfaceColors(
    background = Color(0xFF0D0C14),
    card = Color(0xFF161520),
    elevated = Color(0xFF1E1D2B),
    border = Color(0xFF28263A),
    borderHover = Color(0xFF3A3850),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF8B89A0),
    textTertiary = Color(0xFF5A5870),
)

val LightSurface = SurfaceColors(
    background = Color(0xFFF5F4FA),
    card = Color(0xFFE4E1EE),
    elevated = Color(0xFFEDEBF4),
    border = Color(0xFFDBD8E6),
    borderHover = Color(0xFFCBC8DA),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF6B697E),
    textTertiary = Color(0xFF9896AA),
)
