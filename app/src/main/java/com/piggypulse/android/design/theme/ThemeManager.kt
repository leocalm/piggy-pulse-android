package com.piggypulse.android.design.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("piggy_pulse_theme", Context.MODE_PRIVATE)

    private val _colorTheme = MutableStateFlow(loadColorTheme())
    val colorTheme: StateFlow<ColorTheme> = _colorTheme.asStateFlow()

    private val _appearanceMode = MutableStateFlow(loadAppearanceMode())
    val appearanceMode: StateFlow<AppearanceMode> = _appearanceMode.asStateFlow()

    fun setColorTheme(theme: ColorTheme) {
        prefs.edit().putString(KEY_COLOR_THEME, theme.name).apply()
        _colorTheme.value = theme
    }

    fun setAppearanceMode(mode: AppearanceMode) {
        prefs.edit().putString(KEY_APPEARANCE_MODE, mode.name).apply()
        _appearanceMode.value = mode
    }

    private fun loadColorTheme(): ColorTheme {
        val name = prefs.getString(KEY_COLOR_THEME, null) ?: return ColorTheme.Nebula
        return try {
            ColorTheme.valueOf(name)
        } catch (_: IllegalArgumentException) {
            ColorTheme.Nebula
        }
    }

    private fun loadAppearanceMode(): AppearanceMode {
        val name = prefs.getString(KEY_APPEARANCE_MODE, null) ?: return AppearanceMode.System
        return try {
            AppearanceMode.valueOf(name)
        } catch (_: IllegalArgumentException) {
            AppearanceMode.System
        }
    }

    companion object {
        private const val KEY_COLOR_THEME = "color_theme"
        private const val KEY_APPEARANCE_MODE = "appearance_mode"
    }
}
