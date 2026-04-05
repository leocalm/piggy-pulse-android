package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.CancelSubscriptionRequest
import com.piggypulse.android.core.model.CreateSubscriptionRequest
import com.piggypulse.android.core.model.SubscriptionDetailResponse
import com.piggypulse.android.core.model.SubscriptionItem
import com.piggypulse.android.core.model.SubscriptionResponse
import com.piggypulse.android.core.model.UpcomingCharge
import com.piggypulse.android.core.model.UpdateSubscriptionRequest
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchAll(periodId: String?): Result<List<SubscriptionItem>> {
        return apiClient.request { apiClient.service.getSubscriptions(periodId) }
            .map { it.data }
    }

    suspend fun fetchDetail(id: String): Result<SubscriptionDetailResponse> {
        return apiClient.request { apiClient.service.getSubscriptionDetail(id) }
    }

    suspend fun fetchUpcoming(periodId: String?, limit: Int? = 5): Result<List<UpcomingCharge>> {
        return apiClient.request { apiClient.service.getUpcomingCharges(periodId, limit) }
            .map { it.data }
    }

    suspend fun create(request: CreateSubscriptionRequest): Result<SubscriptionResponse> {
        return apiClient.request { apiClient.service.createSubscription(request) }
    }

    suspend fun update(id: String, request: UpdateSubscriptionRequest): Result<SubscriptionResponse> {
        return apiClient.request { apiClient.service.updateSubscription(id, request) }
    }

    suspend fun delete(id: String): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deleteSubscription(id) }
    }

    suspend fun cancel(id: String, date: String?): Result<SubscriptionResponse> {
        return apiClient.request { apiClient.service.cancelSubscription(id, CancelSubscriptionRequest(date)) }
    }
}
