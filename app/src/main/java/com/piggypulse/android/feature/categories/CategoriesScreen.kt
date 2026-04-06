package com.piggypulse.android.feature.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.piggypulse.android.core.model.CategoryListItem
import com.piggypulse.android.design.component.KebabMenuItem
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpKebabMenu
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun CategoriesScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editingCategory by viewModel.editingCategory.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val filtered by remember(categories, selectedTab) {
        derivedStateOf { viewModel.filteredCategories }
    }

    val tabs = listOf("expense" to "Expense", "income" to "Income")
    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Categories",
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
            ) { Icon(Icons.Default.Add, contentDescription = "Add category") }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(
                selectedTabIndex = selectedIndex,
                containerColor = PpTheme.colors.background,
                contentColor = PpTheme.colors.primary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = PpTheme.colors.primary,
                    )
                },
            ) {
                tabs.forEachIndexed { _, (type, label) ->
                    Tab(
                        selected = selectedTab == type,
                        onClick = { viewModel.setTab(type) },
                        text = {
                            Text(
                                label,
                                color = if (selectedTab == type) PpTheme.colors.primary
                                else PpTheme.colors.textSecondary,
                            )
                        },
                    )
                }
            }

            if (isLoading) {
                PpLoadingIndicator(fullScreen = true)
            } else if (filtered.isEmpty()) {
                PpEmptyState(
                    title = "No ${tabs[selectedIndex].second.lowercase()} categories",
                    message = "Create one with the + button",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    items(filtered, key = { it.id }) { category ->
                        CategoryRow(
                            category = category,
                            onClick = { viewModel.openEditForm(category) },
                            onEdit = { viewModel.openEditForm(category) },
                            onArchive = { viewModel.archive(category.id) },
                            onDelete = { viewModel.delete(category.id) },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showForm) {
        val catSubscriptions by viewModel.categorySubscriptions.collectAsState()
        CategoryFormSheet(
            category = editingCategory,
            defaultType = selectedTab,
            subscriptions = catSubscriptions,
            onSave = { viewModel.create(it) },
            onUpdate = { id, req -> viewModel.update(id, req) },
            onDismiss = { viewModel.closeForm() },
            onCancelSubscription = { viewModel.cancelSubscription(it) },
            onDeleteSubscription = { viewModel.deleteSubscription(it) },
            onAddSubscription = { viewModel.openAddSubscription() },
        )
    }

    val showAddSubscription by viewModel.showAddSubscription.collectAsState()
    if (showAddSubscription && editingCategory != null) {
        com.piggypulse.android.feature.subscriptions.SubscriptionFormSheet(
            subscription = null,
            fixedCategoryId = editingCategory!!.id,
            fixedCategoryName = "${editingCategory!!.icon} ${editingCategory!!.name}",
            onSave = { viewModel.createSubscription(it) },
            onUpdate = { _, _ -> },
            onDismiss = { viewModel.closeAddSubscription() },
        )
    }
}

@Composable
private fun CategoryRow(
    category: CategoryListItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
) {
    PpCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                )
                if (category.numberOfTransactions != null) {
                    Text(
                        text = "${category.numberOfTransactions} transactions",
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                }
            }
            PpKebabMenu(
                items = listOf(
                    KebabMenuItem("Edit", onClick = onEdit),
                    KebabMenuItem("Archive", onClick = onArchive),
                    KebabMenuItem("Delete", onClick = onDelete, isDestructive = true),
                ),
            )
        }
    }
}
