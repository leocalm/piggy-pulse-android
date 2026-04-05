package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
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
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun CurrentPeriodWidget(
    data: DashboardCurrentPeriod,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    val subtitle = buildString {
        data.periodName?.let { append(it) }
        data.remainingDays?.let { append(" \u00B7 $it days left") }
    }

    WidgetCard(
        title = "Current Period",
        subtitle = subtitle.ifBlank { null },
        modifier = modifier,
    ) {
        CurrencyText(
            amountInCents = data.totalSpent ?: 0,
            currencyCode = currencyCode,
            style = MaterialTheme.typography.headlineSmall,
            color = PpTheme.colors.textPrimary,
        )
        if (data.totalBudgeted != null && data.totalBudgeted > 0) {
            Text(
                text = "of ${com.piggypulse.android.core.util.CurrencyFormatter.format(data.totalBudgeted, currencyCode)} budgeted",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Time progress
        if (data.percentTimeElapsed != null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Time", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                Text("${(data.percentTimeElapsed * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            }
            LinearProgressIndicator(
                progress = { data.percentTimeElapsed.toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = PpTheme.colors.secondary,
                trackColor = PpTheme.colors.border,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Budget progress
        if (data.percentBudgetUsed != null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Budget", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                Text("${(data.percentBudgetUsed * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            }
            LinearProgressIndicator(
                progress = { data.percentBudgetUsed.toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = PpTheme.colors.primary,
                trackColor = PpTheme.colors.border,
            )
        }
    }
}
