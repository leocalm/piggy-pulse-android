package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.CreateOverlayRequest
import com.piggypulse.android.core.model.OverlayItem
import com.piggypulse.android.core.model.OverlayResponse
import com.piggypulse.android.core.model.UpdateOverlayRequest
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchAll(): Result<List<OverlayItem>> {
        return apiClient.request { apiClient.service.getOverlays(limit = 200) }
            .map { it.data }
    }

    suspend fun create(request: CreateOverlayRequest): Result<OverlayResponse> {
        return apiClient.request { apiClient.service.createOverlay(request) }
    }

    suspend fun update(id: String, request: UpdateOverlayRequest): Result<OverlayResponse> {
        return apiClient.request { apiClient.service.updateOverlay(id, request) }
    }

    suspend fun delete(id: String): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deleteOverlay(id) }
    }
}
