package com.piggypulse.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.app.AppState
import com.piggypulse.android.design.theme.PiggyPulseTheme
import com.piggypulse.android.design.theme.PpTheme
import com.piggypulse.android.design.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PiggyPulseTheme(themeManager = themeManager) {
                PiggyPulseRoot()
            }
        }
    }
}

@Composable
fun PiggyPulseRoot(
    appState: AppState = hiltViewModel(),
) {
    val isInitializing by appState.isInitializing.collectAsState()
    val isAuthenticated by appState.isAuthenticated.collectAsState()
    val currentUser by appState.currentUser.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PpTheme.colors.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isInitializing -> {
                    CircularProgressIndicator(color = PpTheme.colors.primary)
                }
                isAuthenticated -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Welcome, ${currentUser?.name ?: "User"}!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PpTheme.colors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentUser?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PpTheme.colors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dashboard coming in M3",
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textTertiary,
                        )
                    }
                }
                else -> {
                    Text(
                        text = "Login screen coming in M3",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PpTheme.colors.textPrimary,
                    )
                }
            }
        }
    }
}
