package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.CreateVendorRequest
import com.piggypulse.android.core.model.MergeVendorRequest
import com.piggypulse.android.core.model.UpdateVendorRequest
import com.piggypulse.android.core.model.VendorDetail
import com.piggypulse.android.core.model.VendorResponse
import com.piggypulse.android.core.model.VendorStatsResponse
import com.piggypulse.android.core.model.VendorSummary
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VendorRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchAll(periodId: String?): Result<List<VendorSummary>> {
        return apiClient.request { apiClient.service.getVendors(periodId, limit = 200) }
            .map { it.data }
    }

    suspend fun fetchStats(periodId: String?): Result<VendorStatsResponse> {
        return apiClient.request { apiClient.service.getVendorStats(periodId) }
    }

    suspend fun fetchDetail(id: String, periodId: String?): Result<VendorDetail> {
        return apiClient.request { apiClient.service.getVendorDetail(id, periodId) }
    }

    suspend fun create(request: CreateVendorRequest): Result<VendorResponse> {
        return apiClient.request { apiClient.service.createVendor(request) }
    }

    suspend fun update(id: String, request: UpdateVendorRequest): Result<VendorResponse> {
        return apiClient.request { apiClient.service.updateVendor(id, request) }
    }

    suspend fun delete(id: String): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deleteVendor(id) }
    }

    suspend fun merge(sourceId: String, targetId: String): Result<VendorResponse> {
        return apiClient.request { apiClient.service.mergeVendor(sourceId, MergeVendorRequest(targetId)) }
    }
}
