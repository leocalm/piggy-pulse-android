package com.piggypulse.android.feature.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.core.util.DateUtils
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun PeriodSelectorBar(
    periods: List<BudgetPeriod>,
    selectedPeriodId: String?,
    onSelectPeriod: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentIndex = periods.indexOfFirst { it.id == selectedPeriodId }
    val currentPeriod = if (currentIndex >= 0) periods[currentIndex] else null

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PpTheme.colors.elevated,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    if (currentIndex < periods.size - 1) {
                        onSelectPeriod(periods[currentIndex + 1].id)
                    }
                },
                enabled = currentIndex < periods.size - 1,
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous period",
                    tint = if (currentIndex < periods.size - 1) PpTheme.colors.textSecondary
                    else PpTheme.colors.textTertiary,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = currentPeriod?.name ?: "No period selected",
                    style = MaterialTheme.typography.titleSmall,
                    color = PpTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                if (currentPeriod != null) {
                    val remaining = currentPeriod.remainingDays
                    val subtitle = if (remaining != null) "$remaining days left" else ""
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    if (currentIndex > 0) {
                        onSelectPeriod(periods[currentIndex - 1].id)
                    }
                },
                enabled = currentIndex > 0,
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next period",
                    tint = if (currentIndex > 0) PpTheme.colors.textSecondary
                    else PpTheme.colors.textTertiary,
                )
            }
        }
    }
}
