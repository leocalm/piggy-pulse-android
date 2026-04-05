package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.DashboardCashFlow
import com.piggypulse.android.core.model.DashboardCurrentPeriod
import com.piggypulse.android.core.model.DashboardFixedCategories
import com.piggypulse.android.core.model.DashboardNetPosition
import com.piggypulse.android.core.model.DashboardVariableCategories
import com.piggypulse.android.core.model.DashboardSpendingTrend
import com.piggypulse.android.core.model.DashboardSubscriptions
import com.piggypulse.android.core.model.DashboardTopVendors
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.network.ApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

data class DashboardData(
    val currentPeriod: DashboardCurrentPeriod? = null,
    val netPosition: DashboardNetPosition? = null,
    val cashFlow: DashboardCashFlow? = null,
    val spendingTrend: DashboardSpendingTrend? = null,
    val topVendors: DashboardTopVendors? = null,
    val subscriptions: DashboardSubscriptions? = null,
    val fixedCategories: DashboardFixedCategories? = null,
    val variableCategories: DashboardVariableCategories? = null,
    val recentTransactions: List<Transaction> = emptyList(),
)

@Singleton
class DashboardRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchAll(periodId: String): Result<DashboardData> {
        return try {
            coroutineScope {
                val currentPeriod = async {
                    apiClient.request { apiClient.service.getDashboardCurrentPeriod(periodId) }.getOrNull()
                }
                val netPosition = async {
                    apiClient.request { apiClient.service.getDashboardNetPosition() }.getOrNull()
                }
                val cashFlow = async {
                    apiClient.request { apiClient.service.getDashboardCashFlow(periodId) }.getOrNull()
                }
                val spendingTrend = async {
                    apiClient.request { apiClient.service.getDashboardSpendingTrend(periodId) }.getOrNull()
                }
                val topVendors = async {
                    apiClient.request { apiClient.service.getDashboardTopVendors(periodId) }.getOrNull()
                }
                val subs = async {
                    apiClient.request { apiClient.service.getDashboardSubscriptions(periodId) }.getOrNull()
                }
                val fixed = async {
                    apiClient.request { apiClient.service.getDashboardFixedCategories(periodId) }.getOrNull()
                }
                val recent = async {
                    apiClient.request { apiClient.service.getRecentTransactions(periodId, 7) }
                        .map { it.data }.getOrElse { emptyList() }
                }

                // Variable categories data is not a dedicated endpoint —
                // it will be populated when the categories overview endpoint is added.
                // For now, this field stays null.

                Result.success(
                    DashboardData(
                        currentPeriod = currentPeriod.await(),
                        netPosition = netPosition.await(),
                        cashFlow = cashFlow.await(),
                        spendingTrend = spendingTrend.await(),
                        topVendors = topVendors.await(),
                        subscriptions = subs.await(),
                        fixedCategories = fixed.await(),
                        variableCategories = null,
                        recentTransactions = recent.await(),
                    ),
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
