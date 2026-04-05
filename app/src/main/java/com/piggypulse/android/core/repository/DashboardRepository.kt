package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.DashboardCashFlow
import com.piggypulse.android.core.model.DashboardCurrentPeriod
import com.piggypulse.android.core.model.DashboardFixedCategoryItem
import com.piggypulse.android.core.model.DashboardNetPosition
import com.piggypulse.android.core.model.DashboardSpendingTrend
import com.piggypulse.android.core.model.DashboardSubscriptions
import com.piggypulse.android.core.model.DashboardTopVendorItem
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
    val topVendors: List<DashboardTopVendorItem> = emptyList(),
    val subscriptions: DashboardSubscriptions? = null,
    val fixedCategories: List<DashboardFixedCategoryItem> = emptyList(),
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
                    apiClient.request { apiClient.service.getDashboardTopVendors(periodId) }
                        .getOrElse { emptyList() }
                }
                val subs = async {
                    apiClient.request { apiClient.service.getDashboardSubscriptions(periodId) }.getOrNull()
                }
                val fixed = async {
                    apiClient.request { apiClient.service.getDashboardFixedCategories(periodId) }
                        .getOrElse { emptyList() }
                }
                val recent = async {
                    apiClient.request { apiClient.service.getRecentTransactions(periodId, 7) }
                        .map { it.data }.getOrElse { emptyList() }
                }

                Result.success(
                    DashboardData(
                        currentPeriod = currentPeriod.await(),
                        netPosition = netPosition.await(),
                        cashFlow = cashFlow.await(),
                        spendingTrend = spendingTrend.await(),
                        topVendors = topVendors.await(),
                        subscriptions = subs.await(),
                        fixedCategories = fixed.await(),
                        recentTransactions = recent.await(),
                    ),
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
