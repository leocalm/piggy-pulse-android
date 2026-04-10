package com.piggypulse.android.e2e.helpers

import androidx.test.platform.app.InstrumentationRegistry

/**
 * Clear all app data to ensure a clean state for each test.
 * This resets authentication tokens and user preferences.
 */
fun clearAppData() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    // Clear regular prefs
    context.getSharedPreferences("piggy_pulse_prefs", 0).edit().clear().commit()
    // Clear encrypted prefs (auth tokens) — may throw if not initialized yet
    try {
        context.getSharedPreferences("piggy_pulse_secure_prefs", 0).edit().clear().commit()
    } catch (_: Exception) { /* OK */ }
    // Clear any other shared prefs files
    val prefsDir = context.filesDir.parentFile?.resolve("shared_prefs")
    prefsDir?.listFiles()?.forEach { file ->
        if (file.name.contains("piggy") || file.name.contains("encrypted")) {
            try {
                context.getSharedPreferences(file.nameWithoutExtension, 0).edit().clear().commit()
            } catch (_: Exception) { /* OK */ }
        }
    }
}
