package com.piggypulse.android.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme
import com.piggypulse.android.feature.dashboard.widgets.CashFlowWidget
import com.piggypulse.android.feature.dashboard.widgets.CurrentPeriodWidget
import com.piggypulse.android.feature.dashboard.widgets.NetPositionWidget
import com.piggypulse.android.feature.dashboard.widgets.RecentTransactionsWidget
import com.piggypulse.android.feature.dashboard.widgets.SpendingTrendWidget
import com.piggypulse.android.feature.dashboard.widgets.SubscriptionsWidget
import com.piggypulse.android.feature.dashboard.widgets.TopVendorsWidget
import com.piggypulse.android.feature.dashboard.widgets.VariableCategoriesWidget
import com.piggypulse.android.feature.dashboard.widgets.FixedCategoriesWidget
import com.piggypulse.android.feature.dashboard.widgets.AccountCardWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    periodId: String?,
    currencyCode: String,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val data by viewModel.data.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(periodId) {
        if (periodId != null) viewModel.load(periodId)
    }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = { PpTopBar(title = "Dashboard") },
    ) { innerPadding ->
        if (isLoading && data == null) {
            PpLoadingIndicator(fullScreen = true)
            return@Scaffold
        }

        val dashboard = data
        if (dashboard == null) {
            PpEmptyState(
                title = "No data",
                message = "Select a period to view your dashboard",
                modifier = Modifier.fillMaxSize(),
            )
            return@Scaffold
        }

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { periodId?.let { viewModel.load(it) } },
            modifier = Modifier.padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Net Position
                dashboard.netPosition?.let { netPos ->
                    item(key = "net_position") {
                        NetPositionWidget(data = netPos, currencyCode = currencyCode)
                    }
                }

                // Current Period
                dashboard.currentPeriod?.let { period ->
                    item(key = "current_period") {
                        CurrentPeriodWidget(data = period, currencyCode = currencyCode)
                    }
                }

                // Cash Flow
                dashboard.cashFlow?.let { cf ->
                    item(key = "cash_flow") {
                        CashFlowWidget(data = cf, currencyCode = currencyCode)
                    }
                }

                // Recent Transactions
                if (dashboard.recentTransactions.isNotEmpty()) {
                    item(key = "recent_transactions") {
                        RecentTransactionsWidget(
                            transactions = dashboard.recentTransactions,
                            currencyCode = currencyCode,
                        )
                    }
                }

                // Subscriptions
                dashboard.subscriptions?.let { subs ->
                    item(key = "subscriptions") {
                        SubscriptionsWidget(data = subs, currencyCode = currencyCode)
                    }
                }

                // Spending Trend
                dashboard.spendingTrend?.let { trend ->
                    item(key = "spending_trend") {
                        SpendingTrendWidget(data = trend, currencyCode = currencyCode)
                    }
                }

                // Top Vendors
                if (dashboard.topVendors.isNotEmpty()) {
                    item(key = "top_vendors") {
                        TopVendorsWidget(vendors = dashboard.topVendors, currencyCode = currencyCode)
                    }
                }

                // Variable Categories
                dashboard.categoriesOverview?.let { overview ->
                    val hasVariable = overview.categories.any { it.behavior == "variable" && it.type == "expense" }
                    if (hasVariable) {
                        item(key = "variable_categories") {
                            VariableCategoriesWidget(overview = overview, currencyCode = currencyCode)
                        }
                    }
                }

                // Fixed Categories
                if (dashboard.fixedCategories.isNotEmpty()) {
                    item(key = "fixed_categories") {
                        FixedCategoriesWidget(categories = dashboard.fixedCategories, currencyCode = currencyCode)
                    }
                }

                // Individual Account Cards
                dashboard.accountSummaries.forEach { account ->
                    item(key = "account_${account.id}") {
                        AccountCardWidget(account = account, currencyCode = currencyCode)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
