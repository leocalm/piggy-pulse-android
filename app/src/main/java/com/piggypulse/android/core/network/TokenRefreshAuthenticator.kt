package com.piggypulse.android.core.network

import com.piggypulse.android.core.model.RefreshRequest
import com.piggypulse.android.core.model.RefreshResponse
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val json: Json,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't retry if we already attempted a refresh for this request
        if (response.request.header("X-Retry-After-Refresh") != null) {
            tokenManager.clearTokens()
            return null
        }

        val refreshToken = tokenManager.getRefreshToken() ?: run {
            tokenManager.clearTokens()
            return null
        }

        val refreshed = try {
            performRefresh(refreshToken, response.request.url.toString())
        } catch (_: Exception) {
            tokenManager.clearTokens()
            return null
        }

        if (refreshed == null) {
            tokenManager.clearTokens()
            return null
        }

        tokenManager.setTokens(refreshed.accessToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${refreshed.accessToken}")
            .header("X-Retry-After-Refresh", "true")
            .build()
    }

    private fun performRefresh(refreshToken: String, originalUrl: String): RefreshResponse? {
        val baseUrl = originalUrl.substringBefore("/api/v2") + "/api/v2"
        val body = json.encodeToString(RefreshRequest.serializer(), RefreshRequest(refreshToken))
        val request = Request.Builder()
            .url("$baseUrl/auth/refresh")
            .post(body.toRequestBody("application/json".toMediaType()))
            .header(AuthInterceptor.HEADER_NO_AUTH, "true")
            .build()

        val client = OkHttpClient.Builder().build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        return json.decodeFromString(RefreshResponse.serializer(), responseBody)
    }
}
