package com.piggypulse.android.feature.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.Transaction
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.core.util.DateUtils
import com.piggypulse.android.design.component.KebabMenuItem
import com.piggypulse.android.design.component.PpKebabMenu
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun TransactionRow(
    transaction: Transaction,
    currencyCode: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(transaction.category.color))
    } catch (_: Exception) {
        PpTheme.colors.primary
    }

    val isTransfer = transaction.transactionType == "transfer"
    val isIncome = transaction.category.type == "income"
    val prefix = when {
        isTransfer -> ""
        isIncome -> "+"
        else -> "-"
    }
    val formattedAmount = prefix + CurrencyFormatter.format(
        amountInCents = transaction.amount,
        currencyCode = currencyCode,
    )

    val subtitle = buildString {
        append(transaction.category.name)
        append(" \u00B7 ")
        append(transaction.fromAccount.name)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Category color dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(categoryColor, CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))

        // Description + subtitle
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = transaction.vendor?.name ?: transaction.description,
                style = MaterialTheme.typography.bodyMedium,
                color = PpTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Amount + date
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = formattedAmount,
                style = MaterialTheme.typography.bodyMedium,
                color = PpTheme.colors.textPrimary,
            )
            Text(
                text = DateUtils.formatShort(DateUtils.parseApiDate(transaction.date)),
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textSecondary,
            )
        }

        // Kebab menu
        PpKebabMenu(
            items = listOf(
                KebabMenuItem("Edit", onClick = onEdit),
                KebabMenuItem("Delete", onClick = onDelete, isDestructive = true),
            ),
        )
    }
}
