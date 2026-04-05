package com.piggypulse.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.ForgotPasswordRequest
import com.piggypulse.android.core.network.ApiClient
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    apiClient: ApiClient,
    onNavigateBack: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Reset password",
            style = MaterialTheme.typography.headlineMedium,
            color = PpTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isSent) "Check your email for a reset link."
            else "Enter your email and we'll send you a reset link.",
            style = MaterialTheme.typography.bodyMedium,
            color = PpTheme.colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (!isSent) {
            PpTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            Spacer(modifier = Modifier.height(24.dp))

            PpButton(
                text = if (isLoading) "Sending..." else "Send reset link",
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val result = apiClient.request {
                            apiClient.service.forgotPassword(
                                request = ForgotPasswordRequest(email.trim().lowercase()),
                            )
                        }
                        result.fold(
                            onSuccess = { isSent = true },
                            onFailure = { errorMessage = "Failed to send reset link. Please try again." },
                        )
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && !isLoading,
            )
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = PpTheme.colors.destructive,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Back to sign in", color = PpTheme.colors.primary)
        }
    }
}
