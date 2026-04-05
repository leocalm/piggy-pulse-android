package com.piggypulse.android.feature.targets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.piggypulse.android.core.model.TargetItem
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
    onNavigateBack: () -> Unit,
    viewModel: TargetsViewModel = hiltViewModel(),
) {
    val targets by viewModel.targets.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(periodId) {
        if (periodId != null) viewModel.load(periodId)
    }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Targets",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PpTheme.colors.textPrimary)
                    }
                },
            )
        },
    ) { innerPadding ->
        if (isLoading) {
            PpLoadingIndicator(fullScreen = true, modifier = Modifier.padding(innerPadding))
        } else if (targets.isEmpty()) {
            PpEmptyState(title = "No targets", message = "Budget targets will appear here when configured", modifier = Modifier.fillMaxSize().padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                summary?.let { s ->
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        PpCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(s.periodName, style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                CurrencyText(amountInCents = s.currentPosition, currencyCode = currencyCode, style = MaterialTheme.typography.headlineSmall, color = PpTheme.colors.textPrimary)
                                if (s.incomeTarget > 0) {
                                    Text("Income target: ${CurrencyFormatter.format(s.incomeTarget, currencyCode)}", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                                }
                                Text("${s.categoriesWithTargets.withTargets}/${s.categoriesWithTargets.total} categories with targets", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                            }
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
private fun TargetRow(target: TargetItem, currencyCode: String) {
    val targetAmount = target.currentTarget ?: 0
    val progress = if (targetAmount > 0) (target.spentInPeriod.toFloat() / targetAmount).coerceIn(0f, 1.5f) else 0f

    PpCard {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(target.name, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
                Text(target.status, style = MaterialTheme.typography.labelSmall, color = PpTheme.colors.textTertiary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (targetAmount > 0) {
                Text("${CurrencyFormatter.format(target.spentInPeriod, currencyCode)} / ${CurrencyFormatter.format(targetAmount, currencyCode)}", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth(), color = PpTheme.colors.primary, trackColor = PpTheme.colors.border)
            } else {
                Text("No target set", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
            }
        }
    }
}
