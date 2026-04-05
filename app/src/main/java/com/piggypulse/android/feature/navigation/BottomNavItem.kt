package com.piggypulse.android.feature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Route,
) {
    DashboardTab(
        label = "Dashboard",
        icon = Icons.Default.Dashboard,
        route = Route.Dashboard,
    ),
    TransactionsTab(
        label = "Transactions",
        icon = Icons.Default.Receipt,
        route = Route.Transactions,
    ),
    AccountsTab(
        label = "Accounts",
        icon = Icons.Default.AccountBalance,
        route = Route.Accounts,
    ),
    MoreTab(
        label = "More",
        icon = Icons.Default.MoreHoriz,
        route = Route.More,
    ),
}
