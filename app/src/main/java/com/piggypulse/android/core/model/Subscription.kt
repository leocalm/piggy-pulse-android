package com.piggypulse.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionItem(
    val id: String,
    val name: String,
    val categoryId: String? = null,
    val vendorId: String? = null,
    val billingAmount: Long,
    val billingCycle: String,
    val billingDay: Int? = null,
    val nextChargeDate: String? = null,
    val status: String,
    val cancelledAt: String? = null,
)

// API returns a bare array for subscriptions list

@Serializable
data class SubscriptionDetailResponse(
    val id: String,
    val name: String,
    val categoryId: String? = null,
    val vendorId: String? = null,
    val billingAmount: Long,
    val billingCycle: String,
    val billingDay: Int? = null,
    val nextChargeDate: String? = null,
    val status: String,
    val billingHistory: List<BillingEvent> = emptyList(),
)

@Serializable
data class BillingEvent(
    val id: String,
    val date: String,
    val amount: Long,
)

@Serializable
data class UpcomingCharge(
    val subscriptionId: String,
    val name: String,
    val billingAmount: Long,
    val billingCycle: String,
    val nextChargeDate: String,
    val vendorId: String? = null,
    val vendorName: String? = null,
)

// API returns a bare array for upcoming charges

@Serializable
data class CreateSubscriptionRequest(
    val name: String,
    val categoryId: String,
    val vendorId: String? = null,
    val billingAmount: Long,
    val billingCycle: String,
    val billingDay: Int,
    val nextChargeDate: String,
)

@Serializable
data class UpdateSubscriptionRequest(
    val name: String,
    val categoryId: String,
    val vendorId: String? = null,
    val billingAmount: Long,
    val billingCycle: String,
    val billingDay: Int,
    val nextChargeDate: String,
)

@Serializable
data class CancelSubscriptionRequest(
    val cancellationDate: String? = null,
)

@Serializable
data class SubscriptionResponse(
    val id: String,
    val name: String,
    val status: String,
)
