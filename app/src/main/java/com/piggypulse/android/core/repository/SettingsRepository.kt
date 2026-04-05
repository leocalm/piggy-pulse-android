package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.PreferencesResponse
import com.piggypulse.android.core.model.ProfileResponse
import com.piggypulse.android.core.model.UpdatePreferencesRequest
import com.piggypulse.android.core.model.UpdateProfileRequest
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchProfile(): Result<ProfileResponse> {
        return apiClient.request { apiClient.service.getProfile() }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<ProfileResponse> {
        return apiClient.request { apiClient.service.updateProfile(request) }
    }

    suspend fun fetchPreferences(): Result<PreferencesResponse> {
        return apiClient.request { apiClient.service.getPreferences() }
    }

    suspend fun updatePreferences(request: UpdatePreferencesRequest): Result<PreferencesResponse> {
        return apiClient.request { apiClient.service.updatePreferences(request) }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deleteUserAccount() }
    }
}
