package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountOption(
    val id: String,
    val name: String,
    val color: String,
)

@Serializable
data class CategoryOption(
    val id: String,
    val name: String,
    val icon: String,
    val type: String,
)

@Serializable
data class VendorOption(
    val id: String,
    val name: String,
)

// API returns bare arrays for all option endpoints

data class TransactionFilterOptions(
    val accounts: List<AccountOption> = emptyList(),
    val categories: List<CategoryOption> = emptyList(),
    val vendors: List<VendorOption> = emptyList(),
)
