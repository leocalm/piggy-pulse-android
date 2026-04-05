package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.core.util.DateUtils
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun RecentTransactionsWidget(
    transactions: List<Transaction>,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(title = "Recent Transactions", modifier = modifier) {
        if (transactions.isEmpty()) {
            Text(
                "No transactions this period",
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textTertiary,
            )
        } else {
            transactions.forEachIndexed { index, txn ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = txn.vendor?.name ?: txn.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PpTheme.colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${txn.category.name} \u00B7 ${txn.fromAccount.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        val prefix = when {
                            txn.transactionType == "transfer" -> ""
                            txn.category.type == "income" -> "+"
                            else -> "-"
                        }
                        Text(
                            text = prefix + CurrencyFormatter.format(txn.amount, currencyCode),
                            style = MaterialTheme.typography.bodyMedium,
                            color = PpTheme.colors.textPrimary,
                        )
                        Text(
                            text = DateUtils.formatShort(DateUtils.parseApiDate(txn.date)),
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                        )
                    }
                }
                if (index < transactions.lastIndex) {
                    HorizontalDivider(color = PpTheme.colors.border)
                }
            }
        }
    }
}
