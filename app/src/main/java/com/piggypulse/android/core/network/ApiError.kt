package com.piggypulse.android.core.network

import kotlinx.serialization.Serializable

sealed class ApiError : Exception() {
    data object Unauthorized : ApiError() {
        private fun readResolve(): Any = Unauthorized
    }

    data class TwoFactorRequired(val twoFactorToken: String) : ApiError()
    data object Forbidden : ApiError() {
        private fun readResolve(): Any = Forbidden
    }

    data object NotFound : ApiError() {
        private fun readResolve(): Any = NotFound
    }

    data class Validation(
        override val message: String,
        val fields: Map<String, String>? = null,
    ) : ApiError()

    data class Server(val code: Int, override val message: String) : ApiError()
    data class Network(override val cause: Throwable) : ApiError()
    data class Decoding(override val cause: Throwable) : ApiError()
}

@Serializable
internal data class ApiErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val twoFactorToken: String? = null,
)

@Serializable
internal data class ValidationErrorResponse(
    val message: String,
    val fields: Map<String, String>? = null,
)
