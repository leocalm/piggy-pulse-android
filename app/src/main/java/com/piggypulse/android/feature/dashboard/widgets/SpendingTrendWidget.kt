package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardSpendingTrend
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.chart.PpBarChart
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
            PpBarChart(
                values = data.periods.map { it.totalSpent.toFloat() / 100f },
            )
            if (data.periodAverage > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Period average: ${CurrencyFormatter.format(data.periodAverage, currencyCode)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
        }
    }
}
