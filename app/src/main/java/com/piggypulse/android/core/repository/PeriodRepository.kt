package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeriodRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchPeriods(): Result<List<BudgetPeriod>> {
        return apiClient.request { apiClient.service.getPeriods(limit = 200) }
            .map { it.data }
    }
}
