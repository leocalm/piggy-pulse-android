package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryTargetItem(
    val id: String,
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String? = null,
    val categoryType: String,
    val amount: Long,
    val excluded: Boolean = false,
)

@Serializable
data class PaginatedTargets(
    val data: List<CategoryTargetItem>,
    val nextCursor: String? = null,
)

@Serializable
data class CreateTargetRequest(
    val categoryId: String,
    val amount: Long,
)

@Serializable
data class UpdateTargetRequest(
    val amount: Long,
)

@Serializable
data class TargetResponse(
    val id: String,
    val categoryId: String,
    val amount: Long,
)
