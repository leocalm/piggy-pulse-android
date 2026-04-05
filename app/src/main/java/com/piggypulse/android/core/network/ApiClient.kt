package com.piggypulse.android.core.network

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    val service: ApiService,
    private val json: Json,
) {
    suspend fun <T> request(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(ApiError.Server(response.code(), "Empty response body"))
                }
            } else {
                Result.failure(mapHttpError(response))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiError) {
            Result.failure(e)
        } catch (e: kotlinx.serialization.SerializationException) {
            Result.failure(ApiError.Decoding(e))
        } catch (e: java.io.IOException) {
            Result.failure(ApiError.Network(e))
        } catch (e: Exception) {
            Result.failure(ApiError.Network(e))
        }
    }

    suspend fun requestUnit(call: suspend () -> Response<Unit>): Result<Unit> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(mapHttpError(response))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiError) {
            Result.failure(e)
        } catch (e: java.io.IOException) {
            Result.failure(ApiError.Network(e))
        } catch (e: Exception) {
            Result.failure(ApiError.Network(e))
        }
    }

    private fun <T> mapHttpError(response: Response<T>): ApiError {
        val errorBody = response.errorBody()?.string()

        return when (response.code()) {
            401 -> ApiError.Unauthorized
            403 -> {
                if (errorBody != null) {
                    try {
                        val parsed = json.decodeFromString(ApiErrorResponse.serializer(), errorBody)
                        if (parsed.twoFactorToken != null) {
                            return ApiError.TwoFactorRequired(parsed.twoFactorToken)
                        }
                    } catch (_: Exception) { }
                }
                ApiError.Forbidden
            }
            404 -> ApiError.NotFound
            422 -> {
                if (errorBody != null) {
                    try {
                        val parsed = json.decodeFromString(ValidationErrorResponse.serializer(), errorBody)
                        return ApiError.Validation(parsed.message, parsed.fields)
                    } catch (_: Exception) { }
                }
                ApiError.Validation(errorBody ?: "Validation error")
            }
            else -> {
                val message = if (errorBody != null) {
                    try {
                        val parsed = json.decodeFromString(ApiErrorResponse.serializer(), errorBody)
                        parsed.message ?: parsed.error ?: "Unknown error"
                    } catch (_: Exception) {
                        errorBody
                    }
                } else {
                    "HTTP ${response.code()}"
                }
                ApiError.Server(response.code(), message)
            }
        }
    }
}
