package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.AccountSummary
import com.piggypulse.android.core.model.AccountType
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun AccountCardWidget(
    account: AccountSummary,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    val accountColor = try {
        Color(android.graphics.Color.parseColor(account.color))
    } catch (_: Exception) {
        PpTheme.colors.primary
    }
    val typeLabel = AccountType.entries.firstOrNull { it.apiValue == account.type }?.label ?: account.type
    val prefix = if (account.netChangeThisPeriod != null && account.netChangeThisPeriod >= 0) "+" else ""

    PpCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(accountColor, CircleShape),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = PpTheme.colors.textPrimary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = PpTheme.colors.textTertiary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            CurrencyText(
                amountInCents = account.currentBalance,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.headlineSmall,
                color = PpTheme.colors.textPrimary,
            )

            if (account.netChangeThisPeriod != null && account.netChangeThisPeriod != 0L) {
                Text(
                    text = "this period ${prefix}${CurrencyFormatter.format(account.netChangeThisPeriod, currencyCode)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (account.numberOfTransactions != null) {
                    Text(
                        "Transactions",
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textTertiary,
                    )
                    Text(
                        "${account.numberOfTransactions}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
