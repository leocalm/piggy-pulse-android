package com.piggypulse.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.app.AppState
import com.piggypulse.android.core.network.ApiClient
import com.piggypulse.android.design.theme.PiggyPulseTheme
import com.piggypulse.android.design.theme.ThemeManager
import com.piggypulse.android.feature.navigation.RootNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PiggyPulseTheme(themeManager = themeManager) {
                val appState: AppState = hiltViewModel()
                RootNavHost(
                    appState = appState,
                    apiClient = apiClient,
                )
            }
        }
    }
}
