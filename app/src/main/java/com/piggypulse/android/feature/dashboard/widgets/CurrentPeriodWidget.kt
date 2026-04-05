package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardCurrentPeriod
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun CurrentPeriodWidget(
    data: DashboardCurrentPeriod,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    val budgetUsedPct = if (data.target > 0) (data.spent.toFloat() / data.target).coerceIn(0f, 1.5f) else 0f
    val timeElapsedPct = if (data.daysInPeriod > 0) {
        ((data.daysInPeriod - data.daysRemaining).toFloat() / data.daysInPeriod).coerceIn(0f, 1f)
    } else 0f
    val remaining = data.target - data.spent
    val perDayLeft = if (data.daysRemaining > 0) remaining / data.daysRemaining else 0

    WidgetCard(
        title = "Current Period",
        subtitle = "${data.daysRemaining} days left",
        modifier = modifier,
    ) {
        CurrencyText(
            amountInCents = data.spent,
            currencyCode = currencyCode,
            style = MaterialTheme.typography.headlineSmall,
            color = PpTheme.colors.textPrimary,
        )
        if (data.target > 0) {
            Text(
                text = "of ${CurrencyFormatter.format(data.target, currencyCode)} budgeted" +
                    if (data.incomeTarget > 0) " \u00B7 ${CurrencyFormatter.format(data.incomeTarget, currencyCode)} expected income" else "",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Time", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            Text("${(timeElapsedPct * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
        }
        LinearProgressIndicator(
            progress = { timeElapsedPct },
            modifier = Modifier.fillMaxWidth(),
            color = PpTheme.colors.secondary,
            trackColor = PpTheme.colors.border,
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (data.target > 0) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Budget", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                Text("${(budgetUsedPct * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            }
            LinearProgressIndicator(
                progress = { budgetUsedPct.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = PpTheme.colors.primary,
                trackColor = PpTheme.colors.border,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                CurrencyText(amountInCents = remaining, currencyCode = currencyCode, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
                Text("Remaining", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            }
            Column {
                CurrencyText(amountInCents = perDayLeft, currencyCode = currencyCode, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
                Text("Per Day Left", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            }
            Column {
                CurrencyText(amountInCents = data.projectedSpend, currencyCode = currencyCode, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
                Text("Projected", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            }
        }
    }
}
