package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryListItem(
    val id: String,
    val name: String,
    val type: String,
    val icon: String,
    val color: String,
    val behavior: String? = null,
    val status: String,
    val description: String? = null,
    val numberOfTransactions: Int? = null,
)

@Serializable
data class PaginatedCategories(
    val data: List<CategoryListItem>,
    val nextCursor: String? = null,
)

@Serializable
data class CategoryDetail(
    val id: String,
    val name: String,
    val type: String,
    val icon: String,
    val color: String,
    val behavior: String? = null,
    val status: String,
    val description: String? = null,
    val target: Long? = null,
    val autoComputedTarget: Long? = null,
)

@Serializable
data class CreateCategoryRequest(
    val name: String,
    val icon: String,
    val color: String,
    val type: String,
    val description: String? = null,
    val behavior: String? = null,
)

@Serializable
data class UpdateCategoryRequest(
    val name: String,
    val icon: String,
    val color: String,
    val type: String,
    val description: String? = null,
    val behavior: String? = null,
)

@Serializable
data class CategoryResponse(
    val id: String,
    val name: String,
    val type: String,
    val icon: String,
    val color: String,
    val status: String,
)
