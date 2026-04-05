package com.piggypulse.android.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.core.repository.DashboardData
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme
import com.piggypulse.android.feature.dashboard.widgets.AccountCardWidget
import com.piggypulse.android.feature.dashboard.widgets.CashFlowWidget
import com.piggypulse.android.feature.dashboard.widgets.CurrentPeriodWidget
import com.piggypulse.android.feature.dashboard.widgets.FixedCategoriesWidget
import com.piggypulse.android.feature.dashboard.widgets.NetPositionWidget
import com.piggypulse.android.feature.dashboard.widgets.RecentTransactionsWidget
import com.piggypulse.android.feature.dashboard.widgets.SpendingTrendWidget
import com.piggypulse.android.feature.dashboard.widgets.SubscriptionsWidget
import com.piggypulse.android.feature.dashboard.widgets.TopVendorsWidget
import com.piggypulse.android.feature.dashboard.widgets.VariableCategoriesWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    periodId: String?,
    currencyCode: String,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val data by viewModel.data.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showCustomize by viewModel.showCustomize.collectAsState()
    val widgetOrder by viewModel.layout.widgetOrder.collectAsState()
    val hiddenWidgets by viewModel.layout.hiddenWidgets.collectAsState()

    LaunchedEffect(periodId) {
        if (periodId != null) viewModel.load(periodId)
    }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Dashboard",
                subtitle = "Your finance pulse, at a glance",
                actions = {
                    IconButton(onClick = { viewModel.openCustomize() }) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Customize",
                            tint = PpTheme.colors.textSecondary,
                        )
                    }
                },
            )
        },
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

        val visibleWidgets = widgetOrder.filter { it !in hiddenWidgets }

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

                items(visibleWidgets, key = { it }) { widgetId ->
                    RenderWidget(
                        widgetId = widgetId,
                        dashboard = dashboard,
                        currencyCode = currencyCode,
                        layout = viewModel.layout,
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    if (showCustomize) {
        CustomizeWidgetsSheet(
            layout = viewModel.layout,
            accounts = data?.accountSummaries ?: emptyList(),
            onDismiss = { viewModel.closeCustomize() },
        )
    }
}

@Composable
private fun RenderWidget(
    widgetId: String,
    dashboard: DashboardData,
    currencyCode: String,
    layout: DashboardLayout,
) {
    when (widgetId) {
        "net_position" -> dashboard.netPosition?.let {
            NetPositionWidget(data = it, currencyCode = currencyCode)
        }
        "current_period" -> dashboard.currentPeriod?.let {
            CurrentPeriodWidget(data = it, currencyCode = currencyCode)
        }
        "cash_flow" -> dashboard.cashFlow?.let {
            CashFlowWidget(data = it, currencyCode = currencyCode)
        }
        "recent_transactions" -> {
            if (dashboard.recentTransactions.isNotEmpty()) {
                RecentTransactionsWidget(
                    transactions = dashboard.recentTransactions,
                    currencyCode = currencyCode,
                )
            }
        }
        "subscriptions" -> dashboard.subscriptions?.let {
            SubscriptionsWidget(data = it, currencyCode = currencyCode)
        }
        "spending_trend" -> dashboard.spendingTrend?.let {
            SpendingTrendWidget(data = it, currencyCode = currencyCode)
        }
        "top_vendors" -> {
            if (dashboard.topVendors.isNotEmpty()) {
                TopVendorsWidget(vendors = dashboard.topVendors, currencyCode = currencyCode)
            }
        }
        "variable_categories" -> dashboard.categoriesOverview?.let { overview ->
            if (overview.categories.any { it.behavior == "variable" && it.type == "expense" }) {
                VariableCategoriesWidget(overview = overview, currencyCode = currencyCode)
            }
        }
        "fixed_categories" -> {
            if (dashboard.fixedCategories.isNotEmpty()) {
                FixedCategoriesWidget(categories = dashboard.fixedCategories, currencyCode = currencyCode)
            }
        }
        else -> {
            // Account card: "account:{uuid}"
            val accountId = layout.accountIdFromWidget(widgetId)
            if (accountId != null) {
                val account = dashboard.accountSummaries.firstOrNull { it.id == accountId }
                if (account != null) {
                    AccountCardWidget(account = account, currencyCode = currencyCode)
                }
            }
        }
    }
}
