package com.piggypulse.android.e2e.helpers

import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Direct API calls for test setup (register users, seed data).
 */
object ApiHelper {
    data class TestUser(
        val name: String,
        val email: String,
        val password: String,
        val token: String?,
    )

    /** Register a unique test user. Returns credentials + auth token. */
    fun registerUser(name: String = "E2E User"): TestUser {
        val timestamp = System.currentTimeMillis()
        val random = (100_000..999_999).random()
        val email = "e2e-$timestamp-$random@test.piggypulse.com"

        val body = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", TestConfig.TEST_PASSWORD)
        }

        val response = request("POST", "/auth/register", body)
        val token = response?.optString("token")?.takeIf { it.isNotEmpty() }

        return TestUser(name, email, TestConfig.TEST_PASSWORD, token)
    }

    /** Set user profile (currency). */
    fun setProfile(token: String, currency: String = "EUR") {
        val body = JSONObject().apply {
            put("name", "E2E User")
            put("currency", currency)
            put("avatar", "\uD83D\uDC37") // 🐷
        }
        request("PUT", "/settings/profile", body, token)
    }

    /** Create a budget period for the current month. */
    fun createPeriod(token: String) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val startDate = String.format("%04d-%02d-01", year, month)
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val endDate = String.format("%04d-%02d-%02d", year, month, lastDay)
        val periodName = SimpleDateFormat("MMMM yyyy", Locale.US).format(cal.time)

        val body = JSONObject().apply {
            put("periodType", "ManualEndDate")
            put("startDate", startDate)
            put("name", periodName)
            put("manualEndDate", endDate)
        }
        request("POST", "/periods", body, token)
    }

    /** Full seed: set profile + create period. */
    fun seedUserData(token: String) {
        setProfile(token)
        createPeriod(token)
    }

    /** Make a synchronous HTTP request to the API. */
    fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        token: String? = null,
    ): JSONObject? {
        val url = URL("${TestConfig.API_BASE_URL}$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.setRequestProperty("Content-Type", "application/json")
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000

        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer $token")
        }

        if (body != null) {
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
        }

        return try {
            val responseCode = conn.responseCode
            val stream = if (responseCode < 400) conn.inputStream else conn.errorStream
            val responseText = stream?.bufferedReader()?.readText() ?: ""

            if (responseCode >= 400) {
                throw AssertionError("API $method $path returned $responseCode: $responseText")
            }

            if (responseText.isNotEmpty()) JSONObject(responseText) else null
        } finally {
            conn.disconnect()
        }
    }
}
