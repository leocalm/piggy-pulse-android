package com.piggypulse.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.piggypulse.android.app.AppState
import com.piggypulse.android.app.LoginResult
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    appState: AppState,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToTwoFactor: (String) -> Unit,
    onLoginSuccess: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    fun doLogin() {
        if (isLoading || email.isBlank() || password.isBlank()) return
        scope.launch {
            isLoading = true
            errorMessage = null
            when (val result = appState.login(email, password)) {
                is LoginResult.Success -> onLoginSuccess()
                is LoginResult.TwoFactorRequired -> onNavigateToTwoFactor(result.token)
                is LoginResult.Error -> errorMessage = result.message
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "PiggyPulse",
            style = MaterialTheme.typography.displaySmall,
            color = PpTheme.colors.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.bodyLarge,
            color = PpTheme.colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(32.dp))

        PpTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))

        PpPasswordField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Done,
            onDone = {
                focusManager.clearFocus()
                doLogin()
            },
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = PpTheme.colors.destructive,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PpButton(
            text = if (isLoading) "Signing in..." else "Sign in",
            onClick = { doLogin() },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onNavigateToForgotPassword) {
            Text("Forgot password?", color = PpTheme.colors.textSecondary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? ", color = PpTheme.colors.textSecondary)
            Text("Sign up", color = PpTheme.colors.primary)
        }
    }
}
