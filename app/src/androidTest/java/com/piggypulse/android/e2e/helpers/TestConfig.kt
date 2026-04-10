package com.piggypulse.android.e2e.helpers

/**
 * Configuration for E2E tests.
 * The API URL defaults to the Android emulator's gateway IP for the Docker backend.
 */
object TestConfig {
    // Android emulator uses 10.0.2.2 to reach the host machine's localhost
    const val API_BASE_URL = "http://10.0.2.2:18080/v2"
    const val TEST_PASSWORD = "E2E-TestPass-2026!Strong"
    const val DEFAULT_TIMEOUT_MS = 10_000L
    const val LONG_TIMEOUT_MS = 30_000L
}
