package com.piggypulse.android.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.CategoryListItem
import com.piggypulse.android.core.model.CreateCategoryRequest
import com.piggypulse.android.core.model.SubscriptionItem
import com.piggypulse.android.core.model.UpdateCategoryRequest
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.KebabMenuItem
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpKebabMenu
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormSheet(
    category: CategoryListItem?,
    defaultType: String,
    currencyCode: String = "EUR",
    subscriptions: List<SubscriptionItem> = emptyList(),
    onSave: (CreateCategoryRequest) -> Unit,
    onUpdate: (String, UpdateCategoryRequest) -> Unit,
    onDismiss: () -> Unit,
    onDeleteSubscription: ((String) -> Unit)? = null,
    onCancelSubscription: ((String) -> Unit)? = null,
) {
    val isEditing = category != null
    var name by remember { mutableStateOf(category?.name ?: "") }
    var icon by remember { mutableStateOf(category?.icon ?: "\uD83D\uDCB0") }
    var color by remember { mutableStateOf(category?.color ?: "#000000") }
    var behavior by remember { mutableStateOf(category?.behavior ?: "variable") }
    var targetAmountText by remember { mutableStateOf("") }

    val behaviors = listOf("fixed" to "Fixed", "variable" to "Variable", "subscription" to "Subscription")

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
                text = if (isEditing) "Edit category" else "New category",
                style = MaterialTheme.typography.titleLarge,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            PpTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                placeholder = "e.g. Groceries",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = icon,
                onValueChange = { icon = it },
                label = "Icon (emoji)",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Behavior picker
            Text(
                "Behavior",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                behaviors.forEach { (value, label) ->
                    FilterChip(
                        selected = behavior == value,
                        onClick = {
                            behavior = value
                            if (value == "subscription") targetAmountText = ""
                        },
                        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PpTheme.colors.primary.copy(alpha = 0.15f),
                            selectedLabelColor = PpTheme.colors.primary,
                            containerColor = Color.Transparent,
                            labelColor = PpTheme.colors.textSecondary,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = PpTheme.colors.border,
                            selectedBorderColor = PpTheme.colors.primary.copy(alpha = 0.3f),
                            enabled = true,
                            selected = behavior == value,
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Target amount (hidden when subscription behavior)
            if (behavior != "subscription") {
                PpTextField(
                    value = targetAmountText,
                    onValueChange = { targetAmountText = it },
                    label = "Target amount",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Subscription section
            if (behavior == "subscription") {
                if (isEditing && subscriptions.isNotEmpty()) {
                    CategorySubscriptionSection(
                        subscriptions = subscriptions,
                        currencyCode = currencyCode,
                        onCancel = onCancelSubscription,
                        onDelete = onDeleteSubscription,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (!isEditing) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = PpTheme.colors.textTertiary,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            "After creating, you can add subscriptions to this category.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textTertiary,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            PpButton(
                text = if (isEditing) "Save changes" else "Create category",
                onClick = {
                    val target = if (behavior != "subscription") {
                        targetAmountText.toDoubleOrNull()?.let { CurrencyFormatter.displayToCents(it) }
                    } else null

                    if (isEditing) {
                        onUpdate(
                            category!!.id,
                            UpdateCategoryRequest(
                                name = name, icon = icon, color = color,
                                type = category.type, behavior = behavior, target = target,
                            ),
                        )
                    } else {
                        onSave(
                            CreateCategoryRequest(
                                name = name, icon = icon, color = color,
                                type = defaultType, behavior = behavior, target = target,
                            ),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.length >= 3,
            )
        }
    }
}

@Composable
private fun CategorySubscriptionSection(
    subscriptions: List<SubscriptionItem>,
    currencyCode: String,
    onCancel: ((String) -> Unit)?,
    onDelete: ((String) -> Unit)?,
) {
    val monthlyTotal = subscriptions
        .filter { it.status.equals("active", ignoreCase = true) }
        .sumOf { it.billingAmount }

    PpCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Monthly target (from subscriptions)",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
            Text(
                CurrencyFormatter.format(monthlyTotal, currencyCode),
                style = MaterialTheme.typography.titleMedium,
                color = PpTheme.colors.textPrimary,
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        "Subscriptions",
        style = MaterialTheme.typography.titleSmall,
        color = PpTheme.colors.textSecondary,
    )
    Spacer(modifier = Modifier.height(4.dp))

    subscriptions.forEach { sub ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sub.name, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
                Text(sub.billingCycle, style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    CurrencyFormatter.format(sub.billingAmount, currencyCode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                )
                if (!sub.status.equals("active", ignoreCase = true)) {
                    Text(sub.status, style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
                } else if (sub.nextChargeDate != null) {
                    Text(sub.nextChargeDate, style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                }
            }
            val menuItems = mutableListOf<KebabMenuItem>()
            if (sub.status.equals("active", ignoreCase = true) && onCancel != null) {
                menuItems.add(KebabMenuItem("Cancel", onClick = { onCancel(sub.id) }))
            }
            if (onDelete != null) {
                menuItems.add(KebabMenuItem("Delete", onClick = { onDelete(sub.id) }, isDestructive = true))
            }
            if (menuItems.isNotEmpty()) {
                PpKebabMenu(items = menuItems)
            }
        }
        HorizontalDivider(color = PpTheme.colors.border)
    }
}
