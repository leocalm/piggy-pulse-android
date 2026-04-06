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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.AccountSummary
import com.piggypulse.android.core.model.DashboardNetPosition
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.theme.PpTheme
import kotlin.math.abs

@Composable
fun NetPositionWidget(
    data: DashboardNetPosition,
    accounts: List<AccountSummary>,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(
        title = "Net Position",
        subtitle = "${data.numberOfAccounts} accounts",
        modifier = modifier,
    ) {
        // Total
        CurrencyText(
            amountInCents = data.total,
            currencyCode = currencyCode,
            style = MaterialTheme.typography.headlineMedium,
            color = PpTheme.colors.textPrimary,
        )

        // Period change
        val prefix = if (data.differenceThisPeriod >= 0) "+" else ""
        Text(
            text = "${prefix}${CurrencyFormatter.format(data.differenceThisPeriod, currencyCode)} this period",
            style = MaterialTheme.typography.bodySmall,
            color = PpTheme.colors.textSecondary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Breakdown bar
        val absLiquid = abs(data.liquidAmount)
        val absProtected = abs(data.protectedAmount)
        val absDebt = abs(data.debtAmount)
        val barTotal = absLiquid + absProtected + absDebt
        if (barTotal > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (absLiquid > 0) {
                    Box(
                        modifier = Modifier
                            .weight(absLiquid.toFloat() / barTotal)
                            .height(6.dp)
                            .background(PpTheme.colors.tertiary, RoundedCornerShape(3.dp)),
                    )
                }
                if (absProtected > 0) {
                    Box(
                        modifier = Modifier
                            .weight(absProtected.toFloat() / barTotal)
                            .height(6.dp)
                            .background(PpTheme.colors.primary, RoundedCornerShape(3.dp)),
                    )
                }
                if (absDebt > 0) {
                    Box(
                        modifier = Modifier
                            .weight(absDebt.toFloat() / barTotal)
                            .height(6.dp)
                            .background(PpTheme.colors.secondary, RoundedCornerShape(3.dp)),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Breakdown boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BreakdownBox(color = PpTheme.colors.tertiary, label = "LIQUID", amount = data.liquidAmount, currencyCode = currencyCode, modifier = Modifier.weight(1f))
            BreakdownBox(color = PpTheme.colors.primary, label = "PROTECTED", amount = data.protectedAmount, currencyCode = currencyCode, modifier = Modifier.weight(1f))
            BreakdownBox(color = PpTheme.colors.secondary, label = "DEBT", amount = data.debtAmount, currencyCode = currencyCode, modifier = Modifier.weight(1f))
        }

        // Account list
        val activeAccounts = accounts.filter { it.status.equals("active", ignoreCase = true) }
        if (activeAccounts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            activeAccounts.forEach { account ->
                val accountColor = accountTypeColor(account.type)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(accountColor, CircleShape),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        account.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PpTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        account.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textTertiary,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(
                        CurrencyFormatter.format(account.currentBalance, currencyCode),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PpTheme.colors.textPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakdownBox(
    color: Color,
    label: String,
    amount: Long,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = PpTheme.colors.elevated,
        shape = RoundedCornerShape(6.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = PpTheme.colors.textTertiary)
            }
            Text(
                CurrencyFormatter.format(amount, currencyCode),
                style = MaterialTheme.typography.bodySmall,
                color = PpTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun accountTypeColor(type: String): Color {
    return when (type) {
        "CreditCard" -> PpTheme.colors.secondary
        "Savings" -> PpTheme.colors.primary
        "Allowance" -> PpTheme.colors.secondary
        else -> PpTheme.colors.tertiary
    }
}
