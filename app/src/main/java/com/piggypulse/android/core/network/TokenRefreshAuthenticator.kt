package com.piggypulse.android.core.network

import com.piggypulse.android.BuildConfig
import com.piggypulse.android.core.model.RefreshResponse
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val json: Json,
) : Authenticator {

    // Dedicated client for token refresh — separate from the main OkHttpClient to
    // avoid circular dependency and prevent the refresh call itself going through
    // the AuthInterceptor / this authenticator again.
    private val refreshClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

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
            performRefresh(refreshToken)
        } catch (_: Exception) {
            tokenManager.clearTokens()
            return null
        }

        if (refreshed == null) {
            tokenManager.clearTokens()
            return null
        }

        tokenManager.setTokens(accessToken = refreshed.token, refreshToken = refreshed.token)

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${refreshed.token}")
            .header("X-Retry-After-Refresh", "true")
            .build()
    }

    private fun performRefresh(currentToken: String): RefreshResponse? {
        // The refresh endpoint uses bearer auth (current token), no request body
        val request = Request.Builder()
            .url("${BuildConfig.API_BASE_URL}/auth/refresh")
            .post("".toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer $currentToken")
            .build()

        val response = refreshClient.newCall(request).execute()

        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        return json.decodeFromString(RefreshResponse.serializer(), responseBody)
    }
}
