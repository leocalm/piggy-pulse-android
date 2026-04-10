package com.piggypulse.android.feature.auth

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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
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
fun RegisterScreen(
    appState: AppState,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Surface(
        color = PpTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            AuthHeader(tagline = "Create your account")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                PpTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = "Name",
                    modifier = Modifier.fillMaxWidth().testTag("register-name"),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                )
                Spacer(modifier = Modifier.height(12.dp))

                PpTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth().testTag("register-email"),
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
                    modifier = Modifier.fillMaxWidth().testTag("register-password"),
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
                    text = if (isLoading) "Creating account..." else "Create account",
                    onClick = {
                        scope.launch {
                            isLoading = true
                            when (val result = appState.register(name, email, password)) {
                                is LoginResult.Success -> onRegisterSuccess()
                                is LoginResult.TwoFactorRequired -> { }
                                is LoginResult.Error -> errorMessage = result.message
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("register-submit"),
                    enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 8 && !isLoading,
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text("Already have an account? ", color = PpTheme.colors.textSecondary)
                    Text("Sign in", color = PpTheme.colors.primary)
                }
            }
        }
    }
}
