package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardSubscriptions
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun SubscriptionsWidget(
    data: DashboardSubscriptions,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    val subtitle = "${data.activeCount} active"

    WidgetCard(title = "Subscriptions", subtitle = subtitle, modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CurrencyText(
                amountInCents = data.monthlyTotal,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.headlineSmall,
                color = PpTheme.colors.textPrimary,
            )
            Text(
                text = "/mo",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (data.upcoming.isNotEmpty()) {
            Text(
                "Upcoming",
                style = MaterialTheme.typography.labelSmall,
                color = PpTheme.colors.textTertiary,
                modifier = Modifier.padding(top = 8.dp),
            )
            data.upcoming.take(3).forEach { charge ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(charge.name, style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textPrimary)
                    Text(
                        CurrencyFormatter.format(charge.billingAmount, currencyCode),
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
