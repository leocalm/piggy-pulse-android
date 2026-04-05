package com.piggypulse.android.feature.targets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.core.model.CategoryTargetItem
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun TargetsScreen(
    periodId: String?,
    currencyCode: String,
    viewModel: TargetsViewModel = hiltViewModel(),
) {
    val targets by viewModel.targets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(periodId) {
        if (periodId != null) viewModel.load(periodId)
    }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = { PpTopBar(title = "Targets") },
    ) { innerPadding ->
        if (isLoading) {
            PpLoadingIndicator(fullScreen = true, modifier = Modifier.padding(innerPadding))
        } else if (targets.isEmpty()) {
            PpEmptyState(
                title = "No targets",
                message = "Budget targets will appear here when configured",
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        } else {
            val totalBudgeted = targets.sumOf { it.amount }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    PpCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total Budgeted", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textSecondary)
                            CurrencyText(
                                amountInCents = totalBudgeted,
                                currencyCode = currencyCode,
                                style = MaterialTheme.typography.headlineSmall,
                                color = PpTheme.colors.textPrimary,
                            )
                        }
                    }
                }

                items(targets, key = { it.id }) { target ->
                    TargetRow(target = target, currencyCode = currencyCode)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun TargetRow(
    target: CategoryTargetItem,
    currencyCode: String,
) {
    PpCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = target.categoryIcon ?: "",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = target.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                )
                Text(
                    text = target.categoryType,
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
            CurrencyText(
                amountInCents = target.amount,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.bodyMedium,
                color = PpTheme.colors.textPrimary,
            )
        }
    }
}
