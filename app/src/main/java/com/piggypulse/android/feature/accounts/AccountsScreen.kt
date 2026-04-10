package com.piggypulse.android.feature.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.core.model.AccountSummary
import com.piggypulse.android.core.model.AccountType
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.component.KebabMenuItem
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpKebabMenu
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun AccountsScreen(
    periodId: String?,
    currencyCode: String,
    onNavigateToDetail: (String) -> Unit,
    viewModel: AccountsViewModel = hiltViewModel(),
) {
    val accounts by viewModel.accounts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editingAccount by viewModel.editingAccount.collectAsState()
    val netPosition by remember(accounts) {
        derivedStateOf { viewModel.netPosition }
    }
    val groupedAccounts by remember(accounts) {
        derivedStateOf { viewModel.groupedAccounts }
    }

    LaunchedEffect(periodId) {
        if (periodId != null) {
            viewModel.load(periodId)
        }
    }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = { PpTopBar(title = "Accounts") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateForm() },
                containerColor = PpTheme.colors.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("accounts-add-button"),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add account")
            }
        },
    ) { innerPadding ->
        if (isLoading) {
            PpLoadingIndicator(
                fullScreen = true,
                modifier = Modifier.padding(innerPadding),
            )
        } else if (accounts.isEmpty()) {
            PpEmptyState(
                title = "No accounts",
                message = "Add your first account to start tracking",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Net position card
                item(key = "net_position") {
                    Spacer(modifier = Modifier.height(8.dp))
                    NetPositionCard(
                        netPosition = netPosition,
                        currencyCode = currencyCode,
                    )
                }

                // Grouped by type
                val typeOrder = listOf("Checking", "Savings", "CreditCard", "Wallet", "Allowance")
                typeOrder.forEach { type ->
                    val typeAccounts = groupedAccounts[type] ?: return@forEach
                    val typeLabel = AccountType.entries.firstOrNull { it.apiValue == type }?.label ?: type

                    item(key = "header_$type") {
                        Text(
                            text = typeLabel,
                            style = MaterialTheme.typography.titleSmall,
                            color = PpTheme.colors.textSecondary,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }

                    items(typeAccounts, key = { it.id }) { account ->
                        AccountCard(
                            account = account,
                            currencyCode = currencyCode,
                            onClick = { viewModel.openEditForm(account) },
                            onEdit = { viewModel.openEditForm(account) },
                            onArchive = { viewModel.archiveAccount(account.id) },
                            onDelete = { viewModel.deleteAccount(account.id) },
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showForm) {
        AccountFormSheet(
            account = editingAccount,
            onSave = { viewModel.createAccount(it) },
            onUpdate = { id, req -> viewModel.updateAccount(id, req) },
            onDismiss = { viewModel.closeForm() },
        )
    }
}

@Composable
private fun NetPositionCard(
    netPosition: Long,
    currencyCode: String,
) {
    PpCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Net Position",
                style = MaterialTheme.typography.titleSmall,
                color = PpTheme.colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            CurrencyText(
                amountInCents = netPosition,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.headlineMedium,
                color = PpTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun AccountCard(
    account: AccountSummary,
    currencyCode: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
) {
    val accountColor = try {
        Color(android.graphics.Color.parseColor(account.color))
    } catch (_: Exception) {
        PpTheme.colors.primary
    }

    PpCard(
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(accountColor, CircleShape),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                )
                Text(
                    text = AccountType.entries.firstOrNull { it.apiValue == account.type }?.label
                        ?: account.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
            CurrencyText(
                amountInCents = account.currentBalance,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.bodyMedium,
                color = PpTheme.colors.textPrimary,
            )
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
