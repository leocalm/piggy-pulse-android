package com.piggypulse.android.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.piggypulse.android.app.AppState
import com.piggypulse.android.core.network.ApiClient
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.theme.ThemeManager
import com.piggypulse.android.feature.auth.ForgotPasswordScreen
import com.piggypulse.android.feature.auth.LoginScreen
import com.piggypulse.android.feature.auth.RegisterScreen
import com.piggypulse.android.feature.auth.TwoFactorScreen

@Composable
fun RootNavHost(
    appState: AppState,
    apiClient: ApiClient,
    themeManager: ThemeManager,
) {
    val isInitializing by appState.isInitializing.collectAsState()
    val isAuthenticated by appState.isAuthenticated.collectAsState()

    if (isInitializing) {
        PpLoadingIndicator(fullScreen = true)
        return
    }

    if (isAuthenticated) {
        MainScaffold(appState = appState, themeManager = themeManager)
    } else {
        AuthNavHost(appState = appState, apiClient = apiClient)
    }
}

@Composable
private fun AuthNavHost(
    appState: AppState,
    apiClient: ApiClient,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Login,
    ) {
        composable<Route.Login> {
            LoginScreen(
                appState = appState,
                onNavigateToRegister = { navController.navigate(Route.Register) },
                onNavigateToForgotPassword = { navController.navigate(Route.ForgotPassword) },
                onNavigateToTwoFactor = { token -> navController.navigate(Route.TwoFactor(token)) },
                onLoginSuccess = { /* Auth state change triggers recomposition to MainScaffold */ },
            )
        }
        composable<Route.Register> {
            RegisterScreen(
                appState = appState,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { /* Auth state change triggers recomposition */ },
            )
        }
        composable<Route.ForgotPassword> {
            ForgotPasswordScreen(
                apiClient = apiClient,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Route.TwoFactor> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.TwoFactor>()
            TwoFactorScreen(
                appState = appState,
                twoFactorToken = route.token,
                onSuccess = { /* Auth state change triggers recomposition */ },
                onNavigateBack = {
                    navController.popBackStack(Route.Login, inclusive = false)
                },
            )
        }
    }
}
