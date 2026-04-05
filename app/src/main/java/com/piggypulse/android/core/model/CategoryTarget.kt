package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TargetItem(
    val id: String,
    val name: String,
    val type: String,
    val parentId: String? = null,
    val previousTarget: Long? = null,
    val currentTarget: Long? = null,
    val projectedVariance: Long = 0,
    val status: String,
    val spentInPeriod: Long = 0,
)

@Serializable
data class CategoriesWithTargets(
    val withTargets: Long = 0,
    val total: Long = 0,
)

@Serializable
data class TargetSummary(
    val periodName: String,
    val periodStart: String,
    val periodEnd: String? = null,
    val currentPosition: Long = 0,
    val incomeTarget: Long = 0,
    val categoriesWithTargets: CategoriesWithTargets = CategoriesWithTargets(),
    val periodProgress: Long = 0,
)

@Serializable
data class CategoryTargetsResponse(
    val summary: TargetSummary,
    val targets: List<TargetItem>,
)

@Serializable
data class CreateTargetRequest(
    val categoryId: String,
    val value: Long,
)

@Serializable
data class UpdateTargetRequest(
    val value: Long,
)

@Serializable
data class TargetResponse(
    val id: String,
    val categoryId: String? = null,
    val value: Long? = null,
)
