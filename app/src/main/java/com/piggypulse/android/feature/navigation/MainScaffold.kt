package com.piggypulse.android.feature.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.ui.Alignment
import com.piggypulse.android.app.AppState
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpDestructiveButton
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.theme.PpTheme
import com.piggypulse.android.feature.accounts.AccountDetailScreen
import com.piggypulse.android.feature.accounts.AccountsScreen
import androidx.compose.material.icons.filled.Layers
import com.piggypulse.android.feature.categories.CategoriesScreen
import com.piggypulse.android.feature.dashboard.DashboardScreen
import com.piggypulse.android.feature.overlays.OverlaysScreen
import com.piggypulse.android.feature.periods.PeriodsScreen
import com.piggypulse.android.feature.settings.SettingsScreen
import com.piggypulse.android.feature.subscriptions.SubscriptionsScreen
import com.piggypulse.android.feature.targets.TargetsScreen
import com.piggypulse.android.feature.transactions.TransactionsScreen
import com.piggypulse.android.feature.vendors.VendorsScreen
import com.piggypulse.android.design.theme.ThemeManager

@Composable
fun MainScaffold(
    appState: AppState,
    themeManager: ThemeManager,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val periods by appState.periods.collectAsState()
    val selectedPeriodId by appState.selectedPeriodId.collectAsState()

    Scaffold(
        containerColor = PpTheme.colors.background,
        bottomBar = {
            NavigationBar(
                containerColor = PpTheme.colors.card,
            ) {
                BottomNavItem.entries.forEach { item ->
                    val selected = currentRoute?.contains(
                        item.route::class.qualifiedName ?: "",
                        ignoreCase = false,
                    ) == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                            )
                        },
                        label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PpTheme.colors.primary,
                            selectedTextColor = PpTheme.colors.primary,
                            unselectedIconColor = PpTheme.colors.textTertiary,
                            unselectedTextColor = PpTheme.colors.textTertiary,
                            indicatorColor = PpTheme.colors.primary.copy(alpha = 0.12f),
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PeriodSelectorBar(
                periods = periods,
                selectedPeriodId = selectedPeriodId,
                onSelectPeriod = { appState.selectPeriod(it) },
            )

            NavHost(
                navController = navController,
                startDestination = Route.Dashboard,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable<Route.Dashboard> {
                    val currentUser by appState.currentUser.collectAsState()
                    DashboardScreen(
                        periodId = selectedPeriodId,
                        currencyCode = currentUser?.currency ?: "EUR",
                    )
                }
                composable<Route.Transactions> {
                    val currentUser by appState.currentUser.collectAsState()
                    TransactionsScreen(
                        periodId = selectedPeriodId,
                        currencyCode = currentUser?.currency ?: "EUR",
                    )
                }
                composable<Route.Accounts> {
                    val currentUser by appState.currentUser.collectAsState()
                    AccountsScreen(
                        periodId = selectedPeriodId,
                        currencyCode = currentUser?.currency ?: "EUR",
                        onNavigateToDetail = { id -> navController.navigate(Route.AccountDetail(id)) },
                    )
                }
                composable<Route.AccountDetail> { backStackEntry ->
                    val route = backStackEntry.toRoute<Route.AccountDetail>()
                    val currentUser by appState.currentUser.collectAsState()
                    AccountDetailScreen(
                        accountId = route.id,
                        currencyCode = currentUser?.currency ?: "EUR",
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable<Route.More> {
                    MoreScreen(
                        onNavigate = { navController.navigate(it) },
                        onLogout = { appState.logout() },
                    )
                }
                composable<Route.Categories> {
                    CategoriesScreen(
                        onNavigateToDetail = { id -> navController.navigate(Route.CategoryDetail(id)) },
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable<Route.Vendors> {
                    val currentUser by appState.currentUser.collectAsState()
                    VendorsScreen(
                        periodId = selectedPeriodId,
                        currencyCode = currentUser?.currency ?: "EUR",
                        onNavigateToDetail = { id -> navController.navigate(Route.VendorDetail(id)) },
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable<Route.Subscriptions> {
                    val currentUser by appState.currentUser.collectAsState()
                    SubscriptionsScreen(
                        periodId = selectedPeriodId,
                        currencyCode = currentUser?.currency ?: "EUR",
                        onNavigateToDetail = { id -> navController.navigate(Route.SubscriptionDetail(id)) },
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable<Route.Overlays> {
                    val currentUser by appState.currentUser.collectAsState()
                    OverlaysScreen(
                        currencyCode = currentUser?.currency ?: "EUR",
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable<Route.Periods> {
                    PeriodsScreen(
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable<Route.Targets> {
                    val currentUser by appState.currentUser.collectAsState()
                    TargetsScreen(
                        periodId = selectedPeriodId,
                        currencyCode = currentUser?.currency ?: "EUR",
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable<Route.Settings> {
                    SettingsScreen(
                        appState = appState,
                        themeManager = themeManager,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    PpEmptyState(
        title = title,
        message = "Coming soon",
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun MoreScreen(
    onNavigate: (Route) -> Unit,
    onLogout: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = "More",
                style = MaterialTheme.typography.headlineMedium,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            MoreMenuItem(
                icon = Icons.Default.Category,
                label = "Categories",
                onClick = { onNavigate(Route.Categories) },
            )
        }
        item {
            MoreMenuItem(
                icon = Icons.Default.Store,
                label = "Vendors",
                onClick = { onNavigate(Route.Vendors) },
            )
        }
        item {
            MoreMenuItem(
                icon = Icons.Default.Repeat,
                label = "Subscriptions",
                onClick = { onNavigate(Route.Subscriptions) },
            )
        }
        item {
            MoreMenuItem(
                icon = Icons.Default.Layers,
                label = "Overlays",
                onClick = { onNavigate(Route.Overlays) },
            )
        }
        item {
            MoreMenuItem(
                icon = Icons.Default.CalendarMonth,
                label = "Periods",
                onClick = { onNavigate(Route.Periods) },
            )
        }
        item {
            MoreMenuItem(
                icon = Icons.Default.TrackChanges,
                label = "Targets",
                onClick = { onNavigate(Route.Targets) },
            )
        }
        item {
            MoreMenuItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                onClick = { onNavigate(Route.Settings) },
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            PpDestructiveButton(
                text = "Logout",
                onClick = onLogout,
            )
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    PpCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PpTheme.colors.primary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = PpTheme.colors.textPrimary,
            )
        }
    }
}
