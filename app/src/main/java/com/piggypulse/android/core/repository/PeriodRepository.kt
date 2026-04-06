package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.core.model.CreatePeriodRequest
import com.piggypulse.android.core.model.CreateScheduleRequest
import com.piggypulse.android.core.model.PeriodResponse
import com.piggypulse.android.core.model.PeriodScheduleResponse
import com.piggypulse.android.core.model.UpdatePeriodRequest
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

    suspend fun fetchSchedule(): Result<PeriodScheduleResponse> {
        return apiClient.request { apiClient.service.getPeriodSchedule() }
    }

    suspend fun create(request: CreatePeriodRequest): Result<PeriodResponse> {
        return apiClient.request { apiClient.service.createPeriod(request) }
    }

    suspend fun update(id: String, request: UpdatePeriodRequest): Result<PeriodResponse> {
        return apiClient.request { apiClient.service.updatePeriod(id, request) }
    }

    suspend fun delete(id: String): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deletePeriod(id) }
    }

    suspend fun createSchedule(request: CreateScheduleRequest): Result<PeriodScheduleResponse> {
        return apiClient.request { apiClient.service.createPeriodSchedule(request) }
    }

    suspend fun updateSchedule(request: CreateScheduleRequest): Result<PeriodScheduleResponse> {
        return apiClient.request { apiClient.service.updatePeriodSchedule(request) }
    }

    suspend fun deleteSchedule(): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deletePeriodSchedule() }
    }
}
