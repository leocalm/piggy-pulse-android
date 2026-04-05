package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.piggypulse.android.core.model.DashboardCashFlow
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun CashFlowWidget(
    data: DashboardCashFlow,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(title = "Cash Flow", modifier = modifier) {
        val maxAmount = maxOf(data.inflows, data.outflows, 1)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("In", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            CurrencyText(amountInCents = data.inflows, currencyCode = currencyCode, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
        }
        LinearProgressIndicator(
            progress = { (data.inflows.toFloat() / maxAmount).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = PpTheme.colors.primary,
            trackColor = PpTheme.colors.border,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Out", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            CurrencyText(amountInCents = data.outflows, currencyCode = currencyCode, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
        }
        LinearProgressIndicator(
            progress = { (data.outflows.toFloat() / maxAmount).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = PpTheme.colors.secondary,
            trackColor = PpTheme.colors.border,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Net", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
            CurrencyText(amountInCents = data.net, currencyCode = currencyCode, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
        }
    }
}
