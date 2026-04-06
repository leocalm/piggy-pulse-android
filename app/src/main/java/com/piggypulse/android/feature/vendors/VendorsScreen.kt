package com.piggypulse.android.feature.vendors

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.core.model.VendorSummary
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.KebabMenuItem
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpKebabMenu
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun VendorsScreen(
    periodId: String?,
    currencyCode: String,
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: VendorsViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val vendors by viewModel.vendors.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editingVendor by viewModel.editingVendor.collectAsState()
    val filtered by remember(vendors, searchQuery) {
        derivedStateOf { viewModel.filteredVendors }
    }

    LaunchedEffect(periodId) { viewModel.load(periodId) }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Vendors",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PpTheme.colors.textPrimary,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateForm() },
                containerColor = PpTheme.colors.primary,
                contentColor = Color.White,
            ) { Icon(Icons.Default.Add, contentDescription = "Add vendor") }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PpTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearch(it) },
                placeholder = "Search vendors",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PpTheme.colors.textTertiary)
                },
            )

            if (isLoading) {
                PpLoadingIndicator(fullScreen = true)
            } else if (filtered.isEmpty()) {
                PpEmptyState(
                    title = "No vendors",
                    message = if (searchQuery.isNotBlank()) "No vendors match your search" else "Add a vendor with the + button",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    items(filtered, key = { it.id }) { vendor ->
                        VendorRow(
                            vendor = vendor,
                            currencyCode = currencyCode,
                            onClick = { viewModel.openEditForm(vendor) },
                            onEdit = { viewModel.openEditForm(vendor) },
                            onDelete = { viewModel.delete(vendor.id) },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showForm) {
        VendorFormSheet(
            vendor = editingVendor,
            onSave = { viewModel.create(it) },
            onUpdate = { id, req -> viewModel.update(id, req) },
            onDismiss = { viewModel.closeForm() },
        )
    }
}

@Composable
private fun VendorRow(
    vendor: VendorSummary,
    currencyCode: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    PpCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vendor.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                )
                val parts = mutableListOf<String>()
                vendor.numberOfTransactions?.let { parts.add("$it txns") }
                vendor.totalSpend?.let {
                    parts.add(CurrencyFormatter.format(it, currencyCode))
                }
                if (parts.isNotEmpty()) {
                    Text(
                        text = parts.joinToString(" \u00B7 "),
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                }
            }
            PpKebabMenu(
                items = listOf(
                    KebabMenuItem("Edit", onClick = onEdit),
                    KebabMenuItem("Delete", onClick = onDelete, isDestructive = true),
                ),
            )
        }
    }
}
