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
import com.piggypulse.android.core.model.CategoryOverviewResponse
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun VariableCategoriesWidget(
    overview: CategoryOverviewResponse,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    val variableCategories = overview.categories.filter {
        it.behavior == "variable" && it.type == "expense"
    }

    if (variableCategories.isEmpty()) return

    val totalSpent = variableCategories.sumOf { it.actual }
    val totalBudgeted = variableCategories.sumOf { it.budgeted ?: 0L }

    WidgetCard(
        title = "Variable Categories",
        subtitle = "${variableCategories.size} ${if (variableCategories.size == 1) "category" else "categories"}",
        modifier = modifier,
    ) {
        if (totalBudgeted > 0) {
            Text(
                "${CurrencyFormatter.format(totalSpent, currencyCode)} of ${CurrencyFormatter.format(totalBudgeted, currencyCode)} budgeted",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
            val overallProgress = (totalSpent.toFloat() / totalBudgeted).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier.fillMaxWidth(),
                color = PpTheme.colors.primary,
                trackColor = PpTheme.colors.border,
            )
        }

        variableCategories.forEach { cat ->
            val budget = cat.budgeted ?: 0
            val progress = if (budget > 0) (cat.actual.toFloat() / budget).coerceIn(0f, 1.5f) else 0f
            val pct = if (budget > 0) "${cat.actual * 100 / budget}%" else ""

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${cat.icon} ${cat.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textPrimary,
                )
                Text(
                    "${CurrencyFormatter.format(cat.actual, currencyCode)} / ${CurrencyFormatter.format(budget, currencyCode)}  $pct",
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
