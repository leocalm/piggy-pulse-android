package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardCurrentPeriod(
    val spent: Long = 0,
    val target: Long = 0,
    val incomeTarget: Long = 0,
    val daysRemaining: Long = 0,
    val daysInPeriod: Long = 0,
    val projectedSpend: Long = 0,
    val dailySpend: List<Long> = emptyList(),
)

@Serializable
data class DashboardNetPosition(
    val total: Long = 0,
    val differenceThisPeriod: Long = 0,
    val numberOfAccounts: Long = 0,
    val liquidAmount: Long = 0,
    val protectedAmount: Long = 0,
    val debtAmount: Long = 0,
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
    val periodAverage: Long = 0,
)

@Serializable
data class SpendingTrendItem(
    val periodId: String,
    val periodName: String,
    val totalSpent: Long,
)

@Serializable
data class DashboardTopVendorItem(
    val vendorId: String,
    val vendorName: String,
    val totalSpent: Long,
    val transactionCount: Long,
)

@Serializable
data class DashboardSubscriptionItem(
    val id: String,
    val name: String,
    val billingAmount: Long,
    val billingCycle: String,
    val nextChargeDate: String,
    val displayStatus: String,
)

@Serializable
data class DashboardSubscriptions(
    val activeCount: Long = 0,
    val monthlyTotal: Long = 0,
    val yearlyTotal: Long = 0,
    val subscriptions: List<DashboardSubscriptionItem> = emptyList(),
)

@Serializable
data class DashboardFixedCategoryItem(
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val status: String,
    val spent: Long,
    val budgeted: Long,
)
