package com.piggypulse.android.feature.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.DashboardTopVendorItem
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun TopVendorsWidget(
    vendors: List<DashboardTopVendorItem>,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    WidgetCard(title = "Top Vendors", modifier = modifier) {
        if (vendors.isEmpty()) {
            Text("No vendor data", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
        } else {
            vendors.forEachIndexed { index, vendor ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Rank ${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PpTheme.colors.textTertiary,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            text = vendor.vendorName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PpTheme.colors.textPrimary,
                        )
                    }
                    CurrencyText(
                        amountInCents = vendor.totalSpent,
                        currencyCode = currencyCode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PpTheme.colors.textPrimary,
                    )
                }
            }
        }
    }
}
