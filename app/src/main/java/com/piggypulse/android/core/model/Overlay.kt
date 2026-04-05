package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class OverlayItem(
    val id: String,
    val name: String,
    val icon: String? = null,
    val startDate: String,
    val endDate: String,
    val totalCap: Long? = null,
    val totalSpent: Long? = null,
    val status: String? = null,
)

@Serializable
data class PaginatedOverlays(
    val data: List<OverlayItem>,
    val nextCursor: String? = null,
)

@Serializable
data class CreateOverlayRequest(
    val name: String,
    val icon: String? = null,
    val startDate: String,
    val endDate: String,
    val totalCap: Long? = null,
)

@Serializable
data class UpdateOverlayRequest(
    val name: String,
    val icon: String? = null,
    val startDate: String,
    val endDate: String,
    val totalCap: Long? = null,
)

@Serializable
data class OverlayResponse(
    val id: String,
    val name: String,
)
