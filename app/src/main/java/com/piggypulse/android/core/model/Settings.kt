package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val name: String,
    val email: String? = null,
    val currency: String? = null,
    val timezone: String? = null,
    val defaultCurrencyId: String? = null,
)

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val timezone: String? = null,
)

@Serializable
data class PreferencesResponse(
    val theme: String? = null,
    val dateFormat: String? = null,
    val numberFormat: String? = null,
    val language: String? = null,
)

@Serializable
data class UpdatePreferencesRequest(
    val theme: String? = null,
    val language: String? = null,
)
