package com.piggypulse.android.feature.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.piggypulse.android.app.AppState
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.theme.PpTheme
import com.piggypulse.android.feature.transactions.TransactionsScreen

@Composable
fun MainScaffold(
    appState: AppState,
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
                    PlaceholderScreen("Dashboard")
                }
                composable<Route.Transactions> {
                    val currentUser by appState.currentUser.collectAsState()
                    TransactionsScreen(
                        periodId = selectedPeriodId,
                        currencyCode = currentUser?.currency ?: "EUR",
                    )
                }
                composable<Route.Accounts> {
                    PlaceholderScreen("Accounts")
                }
                composable<Route.More> {
                    MorePlaceholder(appState)
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
private fun MorePlaceholder(appState: AppState) {
    Column(modifier = Modifier.fillMaxSize()) {
        PpEmptyState(
            title = "Settings & More",
            message = "Coming soon",
        ) {
            com.piggypulse.android.design.component.PpButton(
                text = "Logout",
                onClick = { appState.logout() },
            )
        }
    }
}
