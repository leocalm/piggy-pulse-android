package com.piggypulse.android.feature.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

private val colorOptions = listOf("#007AFF", "#00B894", "#E17055", "#0984E3", "#FDCB6E", "#E84393", "#00CEC9", "#636E72")

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
    var color by remember { mutableStateOf(account?.color ?: colorOptions.first()) }
    var selectedType by remember {
        mutableStateOf(
            AccountType.entries.firstOrNull { it.apiValue == account?.type } ?: AccountType.Checking,
        )
    }
    var initialBalanceText by remember { mutableStateOf("0") }
    var spendLimitText by remember { mutableStateOf("") }
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

            // Color picker
            Text("Color", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                colorOptions.forEach { c ->
                    val parsedColor = try {
                        Color(android.graphics.Color.parseColor(c))
                    } catch (_: Exception) {
                        PpTheme.colors.primary
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .then(
                                if (color == c) Modifier.border(2.dp, Color.White, CircleShape)
                                else Modifier,
                            )
                            .clickable { color = c },
                    )
                }
            }

            // Spend limit (only for CreditCard and Allowance)
            if (selectedType == AccountType.CreditCard || selectedType == AccountType.Allowance) {
                Spacer(modifier = Modifier.height(12.dp))
                PpTextField(
                    value = spendLimitText,
                    onValueChange = { spendLimitText = it },
                    label = "Spend limit",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }

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
                    val spendLimit = spendLimitText.toDoubleOrNull()?.let {
                        CurrencyFormatter.displayToCents(it)
                    }
                    if (isEditing) {
                        onUpdate(
                            account!!.id,
                            UpdateAccountRequest(
                                name = name,
                                color = color,
                                type = selectedType.apiValue,
                                spendLimit = spendLimit,
                            ),
                        )
                    } else {
                        val balance = initialBalanceText.toDoubleOrNull() ?: 0.0
                        onSave(
                            CreateAccountRequest(
                                name = name,
                                color = color,
                                type = selectedType.apiValue,
                                initialBalance = CurrencyFormatter.displayToCents(balance),
                                spendLimit = spendLimit,
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
