package com.piggypulse.android.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.AccountSummary
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeWidgetsSheet(
    layout: DashboardLayout,
    accounts: List<AccountSummary>,
    onDismiss: () -> Unit,
) {
    val widgetOrder by layout.widgetOrder.collectAsState()
    val hiddenWidgets by layout.hiddenWidgets.collectAsState()

    val visibleIds = widgetOrder.filter { it !in hiddenWidgets }
    val hiddenStandardIds = standardWidgetDefinitions
        .map { it.id }
        .filter { it in hiddenWidgets }

    val activeAccounts = accounts.filter { it.status.equals("active", ignoreCase = true) }
    val addableAccounts = activeAccounts.filter { account ->
        val widgetId = layout.accountWidgetId(account.id)
        widgetId !in widgetOrder || widgetId in hiddenWidgets
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PpTheme.colors.card,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Customize Dashboard",
                    style = MaterialTheme.typography.titleLarge,
                    color = PpTheme.colors.textPrimary,
                )
                PpButton(text = "Done", onClick = onDismiss)
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.height(500.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // Section: Visible Widgets
                item {
                    Text(
                        "VISIBLE",
                        style = MaterialTheme.typography.labelMedium,
                        color = PpTheme.colors.textTertiary,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                itemsIndexed(visibleIds, key = { _, id -> "visible_$id" }) { _, id ->
                    val label = widgetLabel(id, accounts)
                    val description = widgetDescription(id)
                    WidgetRow(
                        label = label,
                        description = description,
                        leadingIcon = {
                            Icon(
                                Icons.Default.DragHandle,
                                contentDescription = "Drag to reorder",
                                tint = PpTheme.colors.textTertiary,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        trailingAction = {
                            IconButton(onClick = { layout.removeWidget(id) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = PpTheme.colors.textTertiary,
                                )
                            }
                        },
                    )
                    HorizontalDivider(color = PpTheme.colors.border)
                }

                // Section: Hidden Widgets
                if (hiddenStandardIds.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "HIDDEN",
                            style = MaterialTheme.typography.labelMedium,
                            color = PpTheme.colors.textTertiary,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }

                    items(hiddenStandardIds, key = { "hidden_$it" }) { id ->
                        val def = standardWidgetDefinitions.firstOrNull { it.id == id }
                        WidgetRow(
                            label = def?.name ?: id,
                            description = def?.description,
                            trailingAction = {
                                IconButton(onClick = { layout.addWidget(id) }) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = PpTheme.colors.primary,
                                    )
                                }
                            },
                        )
                        HorizontalDivider(color = PpTheme.colors.border)
                    }
                }

                // Section: Account Cards
                if (addableAccounts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "ACCOUNT CARDS",
                            style = MaterialTheme.typography.labelMedium,
                            color = PpTheme.colors.textTertiary,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }

                    items(addableAccounts, key = { "addable_${it.id}" }) { account ->
                        WidgetRow(
                            label = account.name,
                            description = "Individual account card",
                            trailingAction = {
                                IconButton(onClick = {
                                    layout.addWidget(layout.accountWidgetId(account.id))
                                }) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = PpTheme.colors.primary,
                                    )
                                }
                            },
                        )
                        HorizontalDivider(color = PpTheme.colors.border)
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun WidgetRow(
    label: String,
    description: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingAction: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.padding(start = 8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = PpTheme.colors.textPrimary,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
        }
        trailingAction()
    }
}

private fun widgetLabel(id: String, accounts: List<AccountSummary>): String {
    if (id.startsWith(ACCOUNT_WIDGET_PREFIX)) {
        val accountId = id.removePrefix(ACCOUNT_WIDGET_PREFIX)
        return accounts.firstOrNull { it.id == accountId }?.name ?: "Account"
    }
    return standardWidgetDefinitions.firstOrNull { it.id == id }?.name ?: id
}

private fun widgetDescription(id: String): String? {
    if (id.startsWith(ACCOUNT_WIDGET_PREFIX)) return "Individual account card"
    return standardWidgetDefinitions.firstOrNull { it.id == id }?.description
}
