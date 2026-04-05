package com.piggypulse.android.feature.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.model.TransactionDirection
import com.piggypulse.android.core.util.DateUtils
import com.piggypulse.android.design.component.PpChip
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionsScreen(
    periodId: String?,
    currencyCode: String,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val selectedDirection by viewModel.selectedDirection.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val selectedAccountIds by viewModel.selectedAccountIds.collectAsState()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsState()
    val selectedVendorIds by viewModel.selectedVendorIds.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editingTransaction by viewModel.editingTransaction.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    LaunchedEffect(periodId) {
        if (periodId != null) {
            viewModel.load(periodId)
            viewModel.loadFilterOptions()
        }
    }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Transactions",
                actions = {
                    val filterCount = viewModel.activeFilterCount
                    IconButton(onClick = { showFilters = true }) {
                        if (filterCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = PpTheme.colors.primary) {
                                        Text(filterCount.toString(), color = Color.White)
                                    }
                                },
                            ) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Filters",
                                    tint = PpTheme.colors.textSecondary,
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = PpTheme.colors.textSecondary,
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateForm() },
                containerColor = PpTheme.colors.primary,
                contentColor = Color.White,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Direction filter chips
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TransactionDirection.entries.forEach { direction ->
                    PpChip(
                        label = direction.name,
                        selected = selectedDirection == direction,
                        onClick = { viewModel.setDirection(direction) },
                    )
                }
            }

            if (isLoading) {
                PpLoadingIndicator(fullScreen = true)
            } else if (transactions.isEmpty()) {
                PpEmptyState(
                    title = "No transactions",
                    message = "Add your first transaction with the + button",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                TransactionList(
                    transactions = transactions,
                    currencyCode = currencyCode,
                    isLoadingMore = isLoadingMore,
                    hasMore = viewModel.hasMore,
                    onLoadMore = { viewModel.loadMore() },
                    onEdit = { viewModel.openEditForm(it) },
                    onDelete = { viewModel.deleteTransaction(it.id) },
                )
            }
        }
    }

    // Filter sheet
    if (showFilters) {
        TransactionFilterSheet(
            filterOptions = filterOptions,
            selectedAccountIds = selectedAccountIds,
            selectedCategoryIds = selectedCategoryIds,
            selectedVendorIds = selectedVendorIds,
            onApply = { a, c, v ->
                viewModel.applyFilters(a, c, v)
                showFilters = false
            },
            onClear = {
                viewModel.clearFilters()
                showFilters = false
            },
            onDismiss = { showFilters = false },
        )
    }

    // Form sheet
    if (showForm) {
        TransactionFormSheet(
            transaction = editingTransaction,
            filterOptions = filterOptions,
            onSave = { viewModel.createTransaction(it) },
            onUpdate = { id, req -> viewModel.updateTransaction(id, req) },
            onDelete = if (editingTransaction != null) {
                { id -> viewModel.deleteTransaction(id); viewModel.closeForm() }
            } else {
                null
            },
            onDismiss = { viewModel.closeForm() },
        )
    }
}

@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    currencyCode: String,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
) {
    val listState = rememberLazyListState()

    // Detect reaching end of list for infinite scroll
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3 && hasMore && !isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    // Group transactions by date
    val grouped = remember(transactions) {
        transactions.groupBy { it.date }
            .toSortedMap(compareByDescending { it })
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        grouped.forEach { (date, txns) ->
            item(key = "header_$date") {
                val parsedDate = DateUtils.parseApiDate(date)
                Text(
                    text = DateUtils.relativeDay(parsedDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = PpTheme.colors.textSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(txns, key = { it.id }) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    currencyCode = currencyCode,
                    onEdit = { onEdit(transaction) },
                    onDelete = { onDelete(transaction) },
                )
                HorizontalDivider(
                    color = PpTheme.colors.border,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PpLoadingIndicator()
                }
            }
        }
    }
}
