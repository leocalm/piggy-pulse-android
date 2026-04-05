package com.piggypulse.android.feature.periods

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.design.component.KebabMenuItem
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpEmptyState
import com.piggypulse.android.design.component.PpKebabMenu
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PeriodsViewModel = hiltViewModel(),
) {
    val periods by viewModel.periods.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showCreateForm by viewModel.showCreateForm.collectAsState()
    val editingPeriod by viewModel.editingPeriod.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Periods",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PpTheme.colors.textPrimary,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateForm() },
                containerColor = PpTheme.colors.primary,
                contentColor = Color.White,
            ) { Icon(Icons.Default.Add, contentDescription = "Add period") }
        },
    ) { innerPadding ->
        if (isLoading) {
            PpLoadingIndicator(fullScreen = true, modifier = Modifier.padding(innerPadding))
        } else if (periods.isEmpty()) {
            PpEmptyState(
                title = "No periods",
                message = "Create your first budget period",
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(periods, key = { it.id }) { period ->
                    PeriodCard(
                        period = period,
                        onEdit = { viewModel.openEditForm(period) },
                        onDelete = { viewModel.delete(period.id) },
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showCreateForm) {
        PeriodFormSheet(
            title = "New period",
            initialName = "",
            initialStartDate = LocalDate.now().toString(),
            initialDurationUnits = "1",
            initialDurationUnit = "months",
            onSave = { name, start, units, unit ->
                viewModel.create(name, start, units, unit)
            },
            onDismiss = { viewModel.closeForm() },
        )
    }

    editingPeriod?.let { period ->
        PeriodFormSheet(
            title = "Edit period",
            initialName = period.name,
            initialStartDate = period.startDate,
            initialDurationUnits = "1",
            initialDurationUnit = "months",
            onSave = { name, start, units, unit ->
                viewModel.update(period.id, name, start, units, unit)
            },
            onDismiss = { viewModel.closeForm() },
        )
    }
}

@Composable
private fun PeriodCard(
    period: BudgetPeriod,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    PpCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = period.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textPrimary,
                )
                Text(
                    text = buildString {
                        append(period.startDate)
                        period.status?.let { append(" \u00B7 $it") }
                        period.remainingDays?.let { append(" \u00B7 $it days left") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
            }
            PpKebabMenu(
                items = listOf(
                    KebabMenuItem("Edit", onClick = onEdit),
                    KebabMenuItem("Delete", onClick = onDelete, isDestructive = true),
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodFormSheet(
    title: String,
    initialName: String,
    initialStartDate: String,
    initialDurationUnits: String,
    initialDurationUnit: String,
    onSave: (name: String, startDate: String, durationUnits: Int, durationUnit: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var startDate by remember { mutableStateOf(initialStartDate) }
    var durationUnits by remember { mutableStateOf(initialDurationUnits) }
    var durationUnit by remember { mutableStateOf(initialDurationUnit) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PpTheme.colors.card,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = PpTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            PpTextField(value = name, onValueChange = { name = it }, label = "Name", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            PpTextField(value = startDate, onValueChange = { startDate = it }, label = "Start date (YYYY-MM-DD)", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            PpTextField(value = durationUnits, onValueChange = { durationUnits = it }, label = "Duration", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            PpTextField(value = durationUnit, onValueChange = { durationUnit = it }, label = "Unit (days/weeks/months)", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            PpButton(
                text = if (title == "New period") "Create period" else "Save changes",
                onClick = { durationUnits.toIntOrNull()?.let { onSave(name, startDate, it, durationUnit) } },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && durationUnits.toIntOrNull() != null,
            )
        }
    }
}
