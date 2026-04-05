package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class VendorSummary(
    val id: String,
    val name: String,
    val status: String,
    val description: String? = null,
    val numberOfTransactions: Int? = null,
    val totalSpend: Long? = null,
)

@Serializable
data class PaginatedVendors(
    val data: List<VendorSummary>,
    val nextCursor: String? = null,
)

@Serializable
data class VendorDetail(
    val id: String,
    val name: String,
    val status: String,
    val description: String? = null,
    val periodSpend: Long? = null,
    val transactionCount: Int? = null,
    val averageTransactionAmount: Long? = null,
)

@Serializable
data class CreateVendorRequest(
    val name: String,
    val description: String? = null,
)

@Serializable
data class UpdateVendorRequest(
    val name: String,
    val description: String? = null,
)

@Serializable
data class VendorResponse(
    val id: String,
    val name: String,
    val status: String,
)

@Serializable
data class MergeVendorRequest(
    val targetVendorId: String,
)

@Serializable
data class VendorStatsResponse(
    val totalVendors: Int,
    val totalSpendThisPeriod: Long,
    val avgSpendPerVendor: Long,
)
