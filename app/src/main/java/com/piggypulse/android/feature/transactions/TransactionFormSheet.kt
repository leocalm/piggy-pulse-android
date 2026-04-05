package com.piggypulse.android.feature.transactions

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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import com.piggypulse.android.core.model.AccountOption
import com.piggypulse.android.core.model.CategoryOption
import com.piggypulse.android.core.model.CreateTransactionRequest
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.model.TransactionFilterOptions
import com.piggypulse.android.core.model.UpdateTransactionRequest
import com.piggypulse.android.core.model.VendorOption
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

            PpTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = "Amount",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = date,
                onValueChange = { date = it },
                label = "Date (YYYY-MM-DD)",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Category dropdown
            OptionDropdown(
                label = "Category",
                options = filterOptions.categories.map { it.id to "${it.icon} ${it.name}" },
                selectedId = selectedCategoryId,
                onSelect = { selectedCategoryId = it },
            )
            Spacer(modifier = Modifier.height(12.dp))

            // From account dropdown
            OptionDropdown(
                label = "Account",
                options = filterOptions.accounts.map { it.id to it.name },
                selectedId = selectedFromAccountId,
                onSelect = { selectedFromAccountId = it },
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Vendor dropdown (optional)
            OptionDropdown(
                label = "Vendor (optional)",
                options = listOf("" to "None") + filterOptions.vendors.map { it.id to it.name },
                selectedId = selectedVendorId ?: "",
                onSelect = { selectedVendorId = it.ifEmpty { null } },
            )

            Spacer(modifier = Modifier.height(24.dp))

            val amount = amountText.toDoubleOrNull()
            val canSave = amount != null && amount > 0 &&
                description.isNotBlank() &&
                selectedCategoryId != null &&
                selectedFromAccountId != null

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
                                vendorId = selectedVendorId,
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
                                vendorId = selectedVendorId,
                            ),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
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
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedId }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
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
