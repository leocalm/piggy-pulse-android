package com.piggypulse.android.feature.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelectorBar(
    periods: List<BudgetPeriod>,
    selectedPeriodId: String?,
    onSelectPeriod: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentPeriod = periods.firstOrNull { it.id == selectedPeriodId }
    var showSheet by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PpTheme.colors.elevated,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = PpTheme.colors.primary,
                modifier = Modifier.size(20.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = currentPeriod?.name ?: "Select Period",
                    style = MaterialTheme.typography.titleSmall,
                    color = PpTheme.colors.textPrimary,
                )
                if (currentPeriod != null) {
                    val subtitle = buildString {
                        currentPeriod.startDate.let { append(it) }
                        currentPeriod.remainingDays?.let { append(" · $it days left") }
                    }
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                        )
                    }
                }
            }
            // Status dot
            if (currentPeriod?.status != null) {
                val dotColor = when (currentPeriod.status) {
                    "Active" -> PpTheme.colors.primary
                    "Upcoming" -> PpTheme.colors.secondary
                    else -> PpTheme.colors.textTertiary
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(dotColor, CircleShape),
                )
            }
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Select period",
                tint = PpTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = PpTheme.colors.card,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Select Period",
                    style = MaterialTheme.typography.titleLarge,
                    color = PpTheme.colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.height(400.dp),
                ) {
                    items(periods, key = { it.id }) { period ->
                        val isSelected = period.id == selectedPeriodId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectPeriod(period.id)
                                    showSheet = false
                                },
                            color = if (isSelected) PpTheme.colors.primary.copy(alpha = 0.1f)
                            else PpTheme.colors.elevated,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Status dot
                                val dotColor = when (period.status) {
                                    "Active" -> PpTheme.colors.primary
                                    "Upcoming" -> PpTheme.colors.secondary
                                    else -> PpTheme.colors.textTertiary
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(dotColor, CircleShape),
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp),
                                ) {
                                    Text(
                                        text = period.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) PpTheme.colors.primary
                                        else PpTheme.colors.textPrimary,
                                    )
                                    Text(
                                        text = buildString {
                                            append(period.startDate)
                                            period.status?.let { append(" · $it") }
                                            period.remainingDays?.let { append(" · $it days left") }
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PpTheme.colors.textSecondary,
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = PpTheme.colors.primary,
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
