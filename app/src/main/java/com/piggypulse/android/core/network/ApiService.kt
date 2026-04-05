package com.piggypulse.android.core.network

import com.piggypulse.android.core.model.ChangePasswordRequest
import com.piggypulse.android.core.model.ForgotPasswordRequest
import com.piggypulse.android.core.model.ForgotPasswordResponse
import com.piggypulse.android.core.model.LoginRequest
import com.piggypulse.android.core.model.LoginResponse
import com.piggypulse.android.core.model.RegisterRequest
import com.piggypulse.android.core.model.TwoFactorRequest
import com.piggypulse.android.core.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    // Auth — no auth required
    @POST("auth/login")
    suspend fun login(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: LoginRequest,
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: RegisterRequest,
    ): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: ForgotPasswordRequest,
    ): Response<ForgotPasswordResponse>

    @POST("auth/2fa/verify")
    suspend fun verifyTwoFactor(
        @Header(AuthInterceptor.HEADER_NO_AUTH) noAuth: String = "true",
        @Body request: TwoFactorRequest,
    ): Response<LoginResponse>

    // Auth — requires auth
    @GET("auth/me")
    suspend fun getMe(): Response<User>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("auth/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): Response<Unit>
}
