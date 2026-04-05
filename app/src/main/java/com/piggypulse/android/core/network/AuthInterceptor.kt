package com.piggypulse.android.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.header(HEADER_NO_AUTH) != null) {
            val cleanedRequest = request.newBuilder()
                .removeHeader(HEADER_NO_AUTH)
                .build()
            return chain.proceed(cleanedRequest)
        }

        val token = tokenManager.getAccessToken()
            ?: return chain.proceed(request)

        val authenticatedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }

    companion object {
        const val HEADER_NO_AUTH = "X-No-Auth"
    }
}
