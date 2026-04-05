package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.CategoryTargetItem
import com.piggypulse.android.core.model.CreateTargetRequest
import com.piggypulse.android.core.model.TargetResponse
import com.piggypulse.android.core.model.UpdateTargetRequest
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TargetRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchTargets(periodId: String): Result<List<CategoryTargetItem>> {
        return apiClient.request { apiClient.service.getTargets(periodId, limit = 200) }
            .map { it.data }
    }

    suspend fun create(request: CreateTargetRequest): Result<TargetResponse> {
        return apiClient.request { apiClient.service.createTarget(request) }
    }

    suspend fun update(id: String, request: UpdateTargetRequest): Result<TargetResponse> {
        return apiClient.request { apiClient.service.updateTarget(id, request) }
    }
}
