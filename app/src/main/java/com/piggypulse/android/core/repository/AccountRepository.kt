package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.AccountDetails
import com.piggypulse.android.core.model.AccountResponse
import com.piggypulse.android.core.model.AccountSummary
import com.piggypulse.android.core.model.CreateAccountRequest
import com.piggypulse.android.core.model.UpdateAccountRequest
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchSummaries(): Result<List<AccountSummary>> {
        return apiClient.request { apiClient.service.getAccountSummaries(limit = 200) }
            .map { it.data }
    }

    suspend fun fetchDetails(id: String): Result<AccountDetails> {
        return apiClient.request { apiClient.service.getAccountDetails(id) }
    }

    suspend fun create(request: CreateAccountRequest): Result<AccountResponse> {
        return apiClient.request { apiClient.service.createAccount(request) }
    }

    suspend fun update(id: String, request: UpdateAccountRequest): Result<AccountResponse> {
        return apiClient.request { apiClient.service.updateAccount(id, request) }
    }

    suspend fun delete(id: String): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deleteAccount(id) }
    }

    suspend fun archive(id: String): Result<AccountResponse> {
        return apiClient.request { apiClient.service.archiveAccount(id) }
    }

    suspend fun unarchive(id: String): Result<AccountResponse> {
        return apiClient.request { apiClient.service.unarchiveAccount(id) }
    }
}
