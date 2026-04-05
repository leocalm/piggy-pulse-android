package com.piggypulse.android.feature.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.AccountSummary
import com.piggypulse.android.core.model.AccountType
import com.piggypulse.android.core.model.CreateAccountRequest
import com.piggypulse.android.core.model.UpdateAccountRequest
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFormSheet(
    account: AccountSummary?,
    onSave: (CreateAccountRequest) -> Unit,
    onUpdate: (String, UpdateAccountRequest) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEditing = account != null

    var name by remember { mutableStateOf(account?.name ?: "") }
    var color by remember { mutableStateOf(account?.color ?: "#8B7EC8") }
    var selectedType by remember {
        mutableStateOf(
            AccountType.entries.firstOrNull { it.apiValue == account?.type } ?: AccountType.Checking,
        )
    }
    var initialBalanceText by remember { mutableStateOf("0") }
    var typeExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PpTheme.colors.card,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = if (isEditing) "Edit account" else "New account",
                style = MaterialTheme.typography.titleLarge,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            PpTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Account type dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it },
            ) {
                PpTextField(
                    value = selectedType.label,
                    onValueChange = {},
                    label = "Type",
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false },
                ) {
                    AccountType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.label) },
                            onClick = {
                                selectedType = type
                                typeExpanded = false
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = color,
                onValueChange = { color = it },
                label = "Color (hex)",
                modifier = Modifier.fillMaxWidth(),
            )

            if (!isEditing) {
                Spacer(modifier = Modifier.height(12.dp))
                PpTextField(
                    value = initialBalanceText,
                    onValueChange = { initialBalanceText = it },
                    label = "Initial balance",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PpButton(
                text = if (isEditing) "Save changes" else "Create account",
                onClick = {
                    if (isEditing) {
                        onUpdate(
                            account!!.id,
                            UpdateAccountRequest(
                                name = name,
                                color = color,
                                accountType = selectedType.apiValue,
                            ),
                        )
                    } else {
                        val balance = initialBalanceText.toDoubleOrNull() ?: 0.0
                        onSave(
                            CreateAccountRequest(
                                name = name,
                                color = color,
                                accountType = selectedType.apiValue,
                                initialBalance = CurrencyFormatter.displayToCents(balance),
                            ),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && name.length >= 3,
            )
        }
    }
}
