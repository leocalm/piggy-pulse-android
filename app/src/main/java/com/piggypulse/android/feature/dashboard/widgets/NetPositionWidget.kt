package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardNetPosition
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun NetPositionWidget(
    data: DashboardNetPosition,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(
        title = "Net Position",
        subtitle = "${data.accounts.size} accounts",
        modifier = modifier,
    ) {
        CurrencyText(
            amountInCents = data.total,
            currencyCode = currencyCode,
            style = MaterialTheme.typography.headlineMedium,
            color = PpTheme.colors.textPrimary,
        )
        if (data.periodChange != null) {
            val prefix = if (data.periodChange >= 0) "+" else ""
            Text(
                text = "this period $prefix",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
            CurrencyText(
                amountInCents = data.periodChange,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PositionItem("Liquid", data.liquid, currencyCode)
            PositionItem("Protected", data.protected_, currencyCode)
            PositionItem("Debt", data.debt, currencyCode)
        }
    }
}

@Composable
private fun PositionItem(label: String, amount: Long, currencyCode: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = PpTheme.colors.textSecondary,
        )
        CurrencyText(
            amountInCents = amount,
            currencyCode = currencyCode,
            style = MaterialTheme.typography.bodyMedium,
            color = PpTheme.colors.textPrimary,
        )
    }
}
