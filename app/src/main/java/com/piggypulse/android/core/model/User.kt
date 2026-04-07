package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val currency: String? = null,
    val twoFactorEnabled: Boolean? = null,
    val onboardingStatus: String? = null,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val requiresTwoFactor: Boolean = false,
    val user: User? = null,
    val token: String? = null,
    val twoFactorToken: String? = null,
)

@Serializable
data class TwoFactorRequest(
    val twoFactorToken: String,
    val code: String,
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)

@Serializable
data class ForgotPasswordResponse(
    val message: String,
)

@Serializable
data class RefreshResponse(
    val token: String,
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
