package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardVariableCategories
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun VariableCategoriesWidget(
    data: DashboardVariableCategories,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(
        title = "Variable Categories",
        subtitle = "${data.categories.size} ${if (data.categories.size == 1) "category" else "categories"}",
        modifier = modifier,
    ) {
        if (data.categories.isEmpty()) {
            Text("No variable categories", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
        } else {
            data.categories.forEach { cat ->
                val progress = if (cat.budgeted > 0) (cat.spent.toFloat() / cat.budgeted).coerceIn(0f, 1.5f) else 0f
                val pct = if (cat.budgeted > 0) "${(cat.spent * 100 / cat.budgeted)}%" else ""
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${cat.icon ?: ""} ${cat.name}", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textPrimary)
                    Text(
                        "${CurrencyFormatter.format(cat.spent, currencyCode)} / ${CurrencyFormatter.format(cat.budgeted, currencyCode)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                }
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = PpTheme.colors.tertiary,
                    trackColor = PpTheme.colors.border,
                )
            }
        }
    }
}
