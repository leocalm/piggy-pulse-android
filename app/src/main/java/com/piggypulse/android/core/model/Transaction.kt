package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String,
    val amount: Long,
    val description: String,
    val date: String,
    val transactionType: String,
    val category: TransactionCategory,
    val fromAccount: TransactionAccount,
    val toAccount: TransactionAccount? = null,
    val vendor: TransactionVendor? = null,
)

@Serializable
data class TransactionCategory(
    val id: String,
    val name: String,
    val color: String,
    val icon: String,
    val type: String,
)

@Serializable
data class TransactionAccount(
    val id: String,
    val name: String,
    val color: String,
)

@Serializable
data class TransactionVendor(
    val id: String,
    val name: String,
)

@Serializable
data class PaginatedTransactions(
    val data: List<Transaction>,
    val nextCursor: String? = null,
)

@Serializable
data class CreateTransactionRequest(
    val date: String,
    val description: String,
    val amount: Long,
    val fromAccountId: String,
    val categoryId: String,
    val transactionType: String = "regular",
    val toAccountId: String? = null,
    val vendorId: String? = null,
)

@Serializable
data class UpdateTransactionRequest(
    val date: String,
    val description: String,
    val amount: Long,
    val fromAccountId: String,
    val categoryId: String,
    val transactionType: String = "regular",
    val toAccountId: String? = null,
    val vendorId: String? = null,
)

enum class TransactionDirection(val queryValue: String?) {
    All(null),
    Incoming("income"),
    Outgoing("expense"),
    Transfers("transfer"),
}
