package com.piggypulse.android.feature.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    // Auth
    @Serializable data object Login : Route
    @Serializable data object Register : Route
    @Serializable data object ForgotPassword : Route
    @Serializable data class TwoFactor(val token: String) : Route

    // Main tabs
    @Serializable data object Dashboard : Route
    @Serializable data object Transactions : Route
    @Serializable data object Accounts : Route
    @Serializable data object More : Route

    // Catalog screens (via More tab)
    @Serializable data object Categories : Route
    @Serializable data object Vendors : Route
    @Serializable data object Subscriptions : Route
    @Serializable data object Periods : Route
    @Serializable data object Targets : Route
    @Serializable data object Settings : Route

    // Detail screens
    @Serializable data class AccountDetail(val id: String) : Route
    @Serializable data class CategoryDetail(val id: String) : Route
    @Serializable data class VendorDetail(val id: String) : Route
    @Serializable data class SubscriptionDetail(val id: String) : Route
}
