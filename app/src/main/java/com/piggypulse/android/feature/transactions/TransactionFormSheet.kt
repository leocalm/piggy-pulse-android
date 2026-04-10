package com.piggypulse.android.feature.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.CreateTransactionRequest
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.model.TransactionFilterOptions
import com.piggypulse.android.core.model.UpdateTransactionRequest
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpDestructiveButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormSheet(
    transaction: Transaction?,
    filterOptions: TransactionFilterOptions,
    onSave: (CreateTransactionRequest) -> Unit,
    onUpdate: (String, UpdateTransactionRequest) -> Unit,
    onDelete: ((String) -> Unit)?,
    onDismiss: () -> Unit,
) {
    val isEditing = transaction != null

    // Separate transfer category from visible categories
    val transferCategory = filterOptions.categories.firstOrNull {
        it.name.equals("Transfer", ignoreCase = true)
    }
    val visibleCategories = filterOptions.categories.filter {
        !it.name.equals("Transfer", ignoreCase = true)
    }

    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var amountText by remember {
        mutableStateOf(
            if (transaction != null) CurrencyFormatter.centsToDisplay(transaction.amount).toString()
            else "",
        )
    }
    var date by remember { mutableStateOf(transaction?.date ?: LocalDate.now().toString()) }
    var selectedCategoryId by remember { mutableStateOf(transaction?.category?.id) }
    var selectedFromAccountId by remember { mutableStateOf(transaction?.fromAccount?.id) }
    var selectedToAccountId by remember { mutableStateOf(transaction?.toAccount?.id) }
    var selectedVendorId by remember { mutableStateOf(transaction?.vendor?.id) }
    var isTransfer by remember { mutableStateOf(transaction?.transactionType == "transfer") }

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
                text = if (isEditing) "Edit transaction" else "New transaction",
                style = MaterialTheme.typography.titleLarge,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Transfer toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Transfer between accounts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = isTransfer,
                    onCheckedChange = { transfer ->
                        isTransfer = transfer
                        if (transfer) {
                            // Auto-select transfer category, clear vendor
                            selectedCategoryId = transferCategory?.id
                            selectedVendorId = null
                        } else {
                            // Clear to-account, clear transfer category
                            selectedToAccountId = null
                            if (selectedCategoryId == transferCategory?.id) {
                                selectedCategoryId = null
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = PpTheme.colors.primary),
                    modifier = Modifier.testTag("transaction-transfer-toggle"),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Amount
            PpTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = "Amount",
                modifier = Modifier.fillMaxWidth().testTag("transaction-amount-input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Description
            PpTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                modifier = Modifier.fillMaxWidth().testTag("transaction-description-input"),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Date
            PpTextField(
                value = date,
                onValueChange = { date = it },
                label = "Date (YYYY-MM-DD)",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // From account
            OptionDropdown(
                label = if (isTransfer) "From account" else "Account",
                options = filterOptions.accounts.map { it.id to it.name },
                selectedId = selectedFromAccountId,
                onSelect = { selectedFromAccountId = it },
                modifier = Modifier.testTag("transaction-account-select"),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (isTransfer) {
                // To account (transfer only)
                OptionDropdown(
                    label = "To account",
                    options = listOf("" to "Select account") +
                        filterOptions.accounts
                            .filter { it.id != selectedFromAccountId }
                            .map { it.id to it.name },
                    selectedId = selectedToAccountId ?: "",
                    onSelect = { selectedToAccountId = it.ifEmpty { null } },
                    modifier = Modifier.testTag("transaction-to-account-select"),
                )
            } else {
                // Category (non-transfer only, excludes "Transfer")
                OptionDropdown(
                    label = "Category",
                    options = visibleCategories.map { it.id to "${it.icon} ${it.name}" },
                    selectedId = selectedCategoryId,
                    onSelect = { selectedCategoryId = it },
                    modifier = Modifier.testTag("transaction-category-select"),
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Vendor (non-transfer only)
                OptionDropdown(
                    label = "Vendor (optional)",
                    options = listOf("" to "None") + filterOptions.vendors.map { it.id to it.name },
                    selectedId = selectedVendorId ?: "",
                    onSelect = { selectedVendorId = it.ifEmpty { null } },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val amount = amountText.toDoubleOrNull()
            val canSave = amount != null && amount > 0 &&
                description.isNotBlank() &&
                selectedFromAccountId != null &&
                if (isTransfer) {
                    selectedToAccountId != null && selectedCategoryId != null
                } else {
                    selectedCategoryId != null
                }

            PpButton(
                text = if (isEditing) "Save changes" else "Create transaction",
                onClick = {
                    val cents = CurrencyFormatter.displayToCents(amount!!)
                    val type = if (isTransfer) "transfer" else "regular"
                    if (isEditing) {
                        onUpdate(
                            transaction!!.id,
                            UpdateTransactionRequest(
                                date = date,
                                description = description,
                                amount = cents,
                                fromAccountId = selectedFromAccountId!!,
                                categoryId = selectedCategoryId!!,
                                transactionType = type,
                                toAccountId = selectedToAccountId,
                                vendorId = if (isTransfer) null else selectedVendorId,
                            ),
                        )
                    } else {
                        onSave(
                            CreateTransactionRequest(
                                date = date,
                                description = description,
                                amount = cents,
                                fromAccountId = selectedFromAccountId!!,
                                categoryId = selectedCategoryId!!,
                                transactionType = type,
                                toAccountId = selectedToAccountId,
                                vendorId = if (isTransfer) null else selectedVendorId,
                            ),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("transaction-form-submit"),
                enabled = canSave,
            )

            if (isEditing && onDelete != null) {
                Spacer(modifier = Modifier.height(8.dp))
                PpDestructiveButton(
                    text = "Delete transaction",
                    onClick = { onDelete(transaction!!.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionDropdown(
    label: String,
    options: List<Pair<String, String>>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedId }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        PpTextField(
            value = selectedLabel,
            onValueChange = {},
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelect(id)
                        expanded = false
                    },
                )
            }
        }
    }
}
