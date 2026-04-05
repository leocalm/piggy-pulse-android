package com.piggypulse.android.feature.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.TransactionFilterOptions
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpDestructiveButton
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterSheet(
    filterOptions: TransactionFilterOptions,
    selectedAccountIds: Set<String>,
    selectedCategoryIds: Set<String>,
    selectedVendorIds: Set<String>,
    onApply: (accountIds: Set<String>, categoryIds: Set<String>, vendorIds: Set<String>) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    var localAccountIds by remember { mutableStateOf(selectedAccountIds) }
    var localCategoryIds by remember { mutableStateOf(selectedCategoryIds) }
    var localVendorIds by remember { mutableStateOf(selectedVendorIds) }

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
                "Filters",
                style = MaterialTheme.typography.titleLarge,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (filterOptions.accounts.isNotEmpty()) {
                Text(
                    "Accounts",
                    style = MaterialTheme.typography.titleSmall,
                    color = PpTheme.colors.textSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                filterOptions.accounts.forEach { account ->
                    val selected = account.id in localAccountIds
                    FilterRow(
                        label = account.name,
                        selected = selected,
                        onClick = {
                            localAccountIds = if (selected) localAccountIds - account.id
                            else localAccountIds + account.id
                        },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filterOptions.categories.isNotEmpty()) {
                Text(
                    "Categories",
                    style = MaterialTheme.typography.titleSmall,
                    color = PpTheme.colors.textSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                filterOptions.categories.forEach { category ->
                    val selected = category.id in localCategoryIds
                    FilterRow(
                        label = "${category.icon} ${category.name}",
                        selected = selected,
                        onClick = {
                            localCategoryIds = if (selected) localCategoryIds - category.id
                            else localCategoryIds + category.id
                        },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filterOptions.vendors.isNotEmpty()) {
                Text(
                    "Vendors",
                    style = MaterialTheme.typography.titleSmall,
                    color = PpTheme.colors.textSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                filterOptions.vendors.forEach { vendor ->
                    val selected = vendor.id in localVendorIds
                    FilterRow(
                        label = vendor.name,
                        selected = selected,
                        onClick = {
                            localVendorIds = if (selected) localVendorIds - vendor.id
                            else localVendorIds + vendor.id
                        },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            PpButton(
                text = "Apply filters",
                onClick = { onApply(localAccountIds, localCategoryIds, localVendorIds) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            PpDestructiveButton(
                text = "Clear all",
                onClick = onClear,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun FilterRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = PpTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = PpTheme.colors.primary,
            )
        }
    }
}
