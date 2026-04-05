package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountSummary(
    val id: String,
    val name: String,
    val type: String,
    val color: String,
    val status: String,
    val currentBalance: Long,
    val netChangeThisPeriod: Long? = null,
    val nextTransfer: Long? = null,
    val balanceAfterNextTransfer: Long? = null,
    val numberOfTransactions: Int? = null,
)

@Serializable
data class PaginatedAccountSummaries(
    val data: List<AccountSummary>,
    val nextCursor: String? = null,
)

@Serializable
data class AccountDetails(
    val id: String,
    val name: String,
    val type: String,
    val color: String,
    val status: String,
    val currentBalance: Long,
    val netChangeThisPeriod: Long? = null,
    val inflow: Long? = null,
    val outflow: Long? = null,
    val numberOfTransactions: Int? = null,
)

@Serializable
data class CreateAccountRequest(
    val name: String,
    val color: String,
    val accountType: String,
    val initialBalance: Long = 0,
    val currencyId: String? = null,
    val spendLimit: Long? = null,
)

@Serializable
data class UpdateAccountRequest(
    val name: String,
    val color: String,
    val accountType: String,
    val spendLimit: Long? = null,
)

@Serializable
data class AccountResponse(
    val id: String,
    val name: String,
    val color: String,
    val status: String,
    val accountType: String? = null,
    val type: String? = null,
    val initialBalance: Long? = null,
    val spendLimit: Long? = null,
)

enum class AccountType(val apiValue: String, val label: String) {
    Checking("checking", "Checking"),
    Savings("savings", "Savings"),
    CreditCard("credit_card", "Credit Card"),
    Wallet("wallet", "Wallet"),
    Allowance("allowance", "Allowance"),
}
