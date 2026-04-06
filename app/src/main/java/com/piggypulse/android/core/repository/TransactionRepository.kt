package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.CreateTransactionRequest
import com.piggypulse.android.core.model.PaginatedTransactions
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.model.TransactionFilterOptions
import com.piggypulse.android.core.model.UpdateTransactionRequest
import com.piggypulse.android.core.network.ApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchTransactions(
        periodId: String,
        cursor: String? = null,
        direction: String? = null,
        accountIds: List<String>? = null,
        categoryIds: List<String>? = null,
        vendorIds: List<String>? = null,
    ): Result<PaginatedTransactions> {
        return apiClient.request {
            apiClient.service.getTransactions(
                periodId = periodId,
                cursor = cursor,
                direction = direction,
                accountIds = accountIds?.ifEmpty { null },
                categoryIds = categoryIds?.ifEmpty { null },
                vendorIds = vendorIds?.ifEmpty { null },
            )
        }
    }

    suspend fun createTransaction(request: CreateTransactionRequest): Result<Transaction> {
        return apiClient.request { apiClient.service.createTransaction(request) }
    }

    suspend fun updateTransaction(id: String, request: UpdateTransactionRequest): Result<Transaction> {
        return apiClient.request { apiClient.service.updateTransaction(id, request) }
    }

    suspend fun deleteTransaction(id: String): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deleteTransaction(id) }
    }

    suspend fun fetchFilterOptions(): Result<TransactionFilterOptions> {
        return try {
            coroutineScope {
                val accounts = async { apiClient.request { apiClient.service.getAccountOptions() } }
                val categories = async { apiClient.request { apiClient.service.getCategoryOptions() } }
                val vendors = async { apiClient.request { apiClient.service.getVendorOptions() } }

                val a = accounts.await().getOrNull()
                val c = categories.await().getOrNull()
                val v = vendors.await().getOrNull()

                Result.success(
                    TransactionFilterOptions(
                        accounts = a ?: emptyList(),
                        categories = c ?: emptyList(),
                        vendors = v ?: emptyList(),
                    ),
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
