package com.piggypulse.android.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

enum class ColorTheme(
    val label: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val gradient: Pair<Color, Color>,
) {
    Nebula(
        label = "Nebula",
        primary = Color(0xFF8B7EC8),
        secondary = Color(0xFFC48BA0),
        tertiary = Color(0xFF7CA8C4),
        gradient = Color(0xFF8B7EC8) to Color(0xFFC48BA0),
    ),
    Sunrise(
        label = "Sunrise",
        primary = Color(0xFF4A7CFF),
        secondary = Color(0xFFF0A25C),
        tertiary = Color(0xFF9B8AE0),
        gradient = Color(0xFF4A7CFF) to Color(0xFFF0A25C),
    ),
    SageStone(
        label = "Sage Stone",
        primary = Color(0xFF7A9E8E),
        secondary = Color(0xFFB5A98A),
        tertiary = Color(0xFF8FA3A8),
        gradient = Color(0xFF7A9E8E) to Color(0xFFB5A98A),
    ),
    DeepOcean(
        label = "Deep Ocean",
        primary = Color(0xFF3A6E8C),
        secondary = Color(0xFF5B8FA8),
        tertiary = Color(0xFF4A7A8C),
        gradient = Color(0xFF3A6E8C) to Color(0xFF5B8FA8),
    ),
    WarmRose(
        label = "Warm Rose",
        primary = Color(0xFFC4786A),
        secondary = Color(0xFFD4A07A),
        tertiary = Color(0xFFB88A8A),
        gradient = Color(0xFFC4786A) to Color(0xFFD4A07A),
    ),
    Moonlit(
        label = "Moonlit",
        primary = Color(0xFF8B7EC8),
        secondary = Color(0xFFA8B4C4),
        tertiary = Color(0xFF7AADCF),
        gradient = Color(0xFF8B7EC8) to Color(0xFFA8B4C4),
    );

    val dataPalette: List<Color> by lazy { buildDataPalette(listOf(primary, secondary, tertiary)) }
}

// Terracotta — used for destructive actions only, not as a positive/negative financial signal.
val Destructive = Color(0xFFB05A4A)

// -- Data palette generation (ported from web tokens.ts) --

private fun colorToHsl(color: Color): Triple<Int, Int, Int> {
    val r = color.red
    val g = color.green
    val b = color.blue
    val maxC = max(r, max(g, b))
    val minC = min(r, min(g, b))
    val l = (maxC + minC) / 2f

    if (maxC == minC) return Triple(0, 0, (l * 100).roundToInt())

    val d = maxC - minC
    val s = if (l > 0.5f) d / (2f - maxC - minC) else d / (maxC + minC)
    val h = when (maxC) {
        r -> ((g - b) / d + (if (g < b) 6f else 0f)) / 6f
        g -> ((b - r) / d + 2f) / 6f
        else -> ((r - g) / d + 4f) / 6f
    }
    return Triple((h * 360).roundToInt(), (s * 100).roundToInt(), (l * 100).roundToInt())
}

private fun hslToColor(h: Int, s: Int, l: Int): Color {
    val sN = s / 100f
    val lN = l / 100f
    val a = sN * min(lN, 1f - lN)
    fun f(n: Int): Float {
        val k = (n + h / 30f) % 12f
        return lN - a * max(min(k - 3f, min(9f - k, 1f)), -1f)
    }
    return Color(f(0), f(8), f(4))
}

private fun tint(color: Color, lightnessOffset: Int): Color {
    val (h, s, l) = colorToHsl(color)
    return hslToColor(h, s, (l + lightnessOffset).coerceIn(0, 100))
}

private fun buildDataPalette(accents: List<Color>): List<Color> {
    val palette = accents.toMutableList()
    // Alternate lighter/darker variants to avoid blowing out to white or black.
    // Offsets: +15, -15, +25, -25, +10, … cycling through accents.
    val offsets = intArrayOf(15, -15, 25, -25, 10, -10, 20, -20)
    var idx = 0
    while (palette.size < 8) {
        val offset = offsets[idx % offsets.size]
        palette.add(tint(accents[idx % accents.size], offset))
        idx++
    }
    return palette
}
