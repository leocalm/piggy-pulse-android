package com.piggypulse.android.feature.subscriptions

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
import com.piggypulse.android.core.model.SubscriptionItem
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.component.KebabMenuItem
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpKebabMenu
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun SubscriptionsScreen(
    periodId: String?,
    currencyCode: String,
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val upcoming by viewModel.upcoming.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editingSubscription by viewModel.editingSubscription.collectAsState()
    val filtered by remember(subscriptions, selectedTab) {
        derivedStateOf { viewModel.filteredSubscriptions }
    }

    val tabs = listOf("active" to "Active", "paused" to "Paused", "cancelled" to "Cancelled")
    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

    LaunchedEffect(periodId) { viewModel.load(periodId) }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Subscriptions",
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
            ) { Icon(Icons.Default.Add, contentDescription = "Add subscription") }
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
                tabs.forEachIndexed { _, (status, label) ->
                    Tab(
                        selected = selectedTab == status,
                        onClick = { viewModel.setTab(status) },
                        text = {
                            Text(
                                label,
                                color = if (selectedTab == status) PpTheme.colors.primary
                                else PpTheme.colors.textSecondary,
                            )
                        },
                    )
                }
            }

            if (isLoading) {
                PpLoadingIndicator(fullScreen = true)
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Upcoming charges card (only on active tab)
                    if (selectedTab == "active" && upcoming.isNotEmpty()) {
                        item(key = "upcoming") {
                            Spacer(modifier = Modifier.height(8.dp))
                            PpCard {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Upcoming charges",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = PpTheme.colors.textSecondary,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    upcoming.forEach { charge ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                charge.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = PpTheme.colors.textPrimary,
                                            )
                                            Column(horizontalAlignment = Alignment.End) {
                                                CurrencyText(
                                                    amountInCents = charge.billingAmount,
                                                    currencyCode = currencyCode,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = PpTheme.colors.textPrimary,
                                                )
                                                Text(
                                                    charge.nextChargeDate,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = PpTheme.colors.textSecondary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (filtered.isEmpty()) {
                        item {
                            PpEmptyState(
                                title = "No ${tabs[selectedIndex].second.lowercase()} subscriptions",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    } else {
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                        items(filtered, key = { it.id }) { subscription ->
                            SubscriptionRow(
                                subscription = subscription,
                                currencyCode = currencyCode,
                                onClick = { onNavigateToDetail(subscription.id) },
                                onEdit = { viewModel.openEditForm(subscription) },
                                onCancel = { viewModel.cancel(subscription.id, null) },
                                onDelete = { viewModel.delete(subscription.id) },
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showForm) {
        SubscriptionFormSheet(
            subscription = editingSubscription,
            onSave = { viewModel.create(it) },
            onUpdate = { id, req -> viewModel.update(id, req) },
            onDismiss = { viewModel.closeForm() },
        )
    }
}

@Composable
private fun SubscriptionRow(
    subscription: SubscriptionItem,
    currencyCode: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    val cycleLabel = when (subscription.billingCycle) {
        "monthly" -> "/mo"
        "quarterly" -> "/qtr"
        "yearly" -> "/yr"
        else -> ""
    }

    PpCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CurrencyText(
                        amountInCents = subscription.billingAmount,
                        currencyCode = currencyCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                    if (cycleLabel.isNotEmpty()) {
                        Text(
                            text = cycleLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                        )
                    }
                }
            }
            if (subscription.nextChargeDate != null) {
                Text(
                    text = subscription.nextChargeDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
            val menuItems = mutableListOf(
                KebabMenuItem("Edit", onClick = onEdit),
            )
            if (subscription.status.equals("active", ignoreCase = true)) {
                menuItems.add(KebabMenuItem("Cancel", onClick = onCancel))
            }
            menuItems.add(KebabMenuItem("Delete", onClick = onDelete, isDestructive = true))
            PpKebabMenu(items = menuItems)
        }
    }
}
