package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class BudgetPeriod(
    val id: String,
    val name: String,
    val startDate: String,
    val length: Int? = null,
    val remainingDays: Int? = null,
    val status: String? = null,
    val totalSpent: Long? = null,
    val totalBudgeted: Long? = null,
    val numberOfTransactions: Int? = null,
    val percentageOfTargetUsed: Double? = null,
)

@Serializable
data class PaginatedPeriods(
    val data: List<BudgetPeriod>,
    val nextCursor: String? = null,
)
