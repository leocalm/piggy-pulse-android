package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardFixedCategoryItem
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun FixedCategoriesWidget(
    categories: List<DashboardFixedCategoryItem>,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(title = "Fixed Categories", modifier = modifier) {
        categories.forEach { cat ->
            val statusIcon = when (cat.status.lowercase()) {
                "paid" -> "\u2705"
                "partial" -> "\u25D0"
                else -> "\u25CB"
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(statusIcon, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        "${cat.categoryIcon} ${cat.categoryName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PpTheme.colors.textPrimary,
                    )
                }
                Text(
                    "${CurrencyFormatter.format(cat.spent, currencyCode)} / ${CurrencyFormatter.format(cat.budgeted, currencyCode)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
        }
    }
}
