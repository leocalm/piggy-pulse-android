package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardSpendingTrend
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun SpendingTrendWidget(
    data: DashboardSpendingTrend,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(
        title = "Spending Trend",
        subtitle = "Last ${data.periods.size} periods",
        modifier = modifier,
    ) {
        if (data.periods.isEmpty()) {
            Text("No data yet", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
        } else {
            val maxSpend = data.periods.maxOf { it.totalSpent }.coerceAtLeast(1)
            data.periods.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(item.periodName, style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                    Text(
                        CurrencyFormatter.format(item.totalSpent, currencyCode, compact = true),
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textPrimary,
                    )
                }
                LinearProgressIndicator(
                    progress = { (item.totalSpent.toFloat() / maxSpend).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = PpTheme.colors.primary,
                    trackColor = PpTheme.colors.border,
                )
            }
            if (data.average != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Average: ${CurrencyFormatter.format(data.average, currencyCode)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
        }
    }
}
