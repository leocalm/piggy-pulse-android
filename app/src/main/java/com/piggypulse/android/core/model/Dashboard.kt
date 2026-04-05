package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardCurrentPeriod(
    val periodName: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val remainingDays: Int? = null,
    val totalSpent: Long? = null,
    val totalBudgeted: Long? = null,
    val expectedIncome: Long? = null,
    val projectedSpend: Long? = null,
    val percentTimeElapsed: Double? = null,
    val percentBudgetUsed: Double? = null,
)

@Serializable
data class DashboardNetPosition(
    val total: Long = 0,
    val liquid: Long = 0,
    val protected_: Long = 0,
    val debt: Long = 0,
    val periodChange: Long? = null,
    val accounts: List<NetPositionAccount> = emptyList(),
)

@Serializable
data class NetPositionAccount(
    val id: String,
    val name: String,
    val type: String,
    val color: String,
    val balance: Long,
)

@Serializable
data class DashboardCashFlow(
    val inflows: Long = 0,
    val outflows: Long = 0,
    val net: Long = 0,
)

@Serializable
data class DashboardSpendingTrend(
    val periods: List<SpendingTrendItem> = emptyList(),
    val average: Long? = null,
)

@Serializable
data class SpendingTrendItem(
    val periodName: String,
    val totalSpent: Long,
)

@Serializable
data class DashboardTopVendors(
    val vendors: List<TopVendorItem> = emptyList(),
)

@Serializable
data class TopVendorItem(
    val id: String,
    val name: String,
    val totalSpent: Long,
    val transactionCount: Int,
)

@Serializable
data class DashboardSubscriptions(
    val activeCount: Int = 0,
    val monthlyTotal: Long = 0,
    val yearlyTotal: Long = 0,
    val upcoming: List<UpcomingCharge> = emptyList(),
)

@Serializable
data class DashboardVariableCategories(
    val categories: List<VariableCategoryItem> = emptyList(),
    val totalSpent: Long = 0,
    val totalBudgeted: Long = 0,
)

@Serializable
data class VariableCategoryItem(
    val id: String,
    val name: String,
    val icon: String? = null,
    val spent: Long,
    val budgeted: Long,
)

@Serializable
data class DashboardFixedCategories(
    val categories: List<FixedCategoryItem> = emptyList(),
)

@Serializable
data class FixedCategoryItem(
    val id: String,
    val name: String,
    val icon: String? = null,
    val spent: Long,
    val budgeted: Long,
    val status: String? = null,
)

