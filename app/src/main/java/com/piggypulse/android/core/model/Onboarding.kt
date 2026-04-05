package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingStatusResponse(
    val completed: Boolean = false,
    val currentStep: String? = null,
)

@Serializable
data class OnboardingCompleteResponse(
    val message: String? = null,
)
