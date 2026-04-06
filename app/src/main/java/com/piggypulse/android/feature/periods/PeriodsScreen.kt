package com.piggypulse.android.feature.periods

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.core.model.BudgetPeriod
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.core.util.DateUtils
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
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PeriodsViewModel = hiltViewModel(),
) {
    val periods by viewModel.periods.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showCreateForm by viewModel.showCreateForm.collectAsState()
    val schedule by viewModel.schedule.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Periods",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PpTheme.colors.textPrimary)
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
                message = "Create your first budget period to start tracking",
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        } else {
            // Group by year
            val grouped = periods
                .sortedByDescending { it.startDate }
                .groupBy {
                    try { it.startDate.substring(0, 4) } catch (_: Exception) { "Unknown" }
                }
                .toSortedMap(compareByDescending { it })

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Schedule status pill
                item(key = "schedule_status") {
                    val hasSchedule = schedule != null && schedule?.schedule != "manual"
                    ScheduleStatusPill(hasSchedule = hasSchedule)
                }

                grouped.forEach { (year, yearPeriods) ->
                    item(key = "year_$year") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                year,
                                style = MaterialTheme.typography.labelMedium,
                                color = PpTheme.colors.textSecondary,
                            )
                            Surface(
                                color = PpTheme.colors.card,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    "${yearPeriods.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PpTheme.colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }

                    items(yearPeriods, key = { it.id }) { period ->
                        PeriodCard(
                            period = period,
                            onDelete = { viewModel.delete(period.id) },
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showCreateForm) {
        CreatePeriodSheet(
            onSave = { name, startDate, endDate ->
                viewModel.createManualEndDate(name, startDate, endDate)
            },
            onDismiss = { viewModel.closeForm() },
        )
    }
}

@Composable
private fun ScheduleStatusPill(hasSchedule: Boolean) {
    val color = if (hasSchedule) PpTheme.colors.tertiary else PpTheme.colors.secondary
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasSchedule) "Auto-generation active" else "Auto-generation inactive",
                style = MaterialTheme.typography.labelSmall,
                color = color,
            )
        }
    }
}

@Composable
private fun PeriodCard(
    period: BudgetPeriod,
    onDelete: () -> Unit,
) {
    val isActive = period.status.equals("active", ignoreCase = true)
    val statusColor = when (period.status?.lowercase()) {
        "active" -> PpTheme.colors.tertiary
        "upcoming" -> PpTheme.colors.secondary
        else -> PpTheme.colors.textTertiary
    }

    PpCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        period.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = PpTheme.colors.textPrimary,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            period.startDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                        )
                        if (period.status != null) {
                            Text(" · ", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
                            Text(
                                period.status,
                                style = MaterialTheme.typography.bodySmall,
                                color = statusColor,
                            )
                        }
                        if (period.remainingDays != null && isActive) {
                            Text(" · ", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
                            Text(
                                "${period.remainingDays} days left",
                                style = MaterialTheme.typography.bodySmall,
                                color = PpTheme.colors.textSecondary,
                            )
                        }
                    }
                }
                if (!isActive) {
                    PpKebabMenu(
                        items = listOf(
                            KebabMenuItem("Delete", onClick = onDelete, isDestructive = true),
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${period.numberOfTransactions ?: 0} transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = PpTheme.colors.textSecondary,
                )
                period.percentageOfTargetUsed?.let {
                    Text(
                        "${it.toInt()}% of budget used",
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePeriodSheet(
    onSave: (name: String, startDate: String, endDate: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endRuleMode by remember { mutableStateOf("duration") } // "duration" or "manual"
    var duration by remember { mutableIntStateOf(1) }
    var durationUnit by remember { mutableStateOf("months") }
    var manualEndDate by remember { mutableStateOf(LocalDate.now().plusMonths(1)) }

    val calculatedEndDate = when (durationUnit) {
        "days" -> startDate.plusDays(duration.toLong())
        "weeks" -> startDate.plusWeeks(duration.toLong())
        "months" -> startDate.plusMonths(duration.toLong())
        else -> startDate.plusMonths(duration.toLong())
    }
    val effectiveEndDate = if (endRuleMode == "duration") calculatedEndDate else manualEndDate
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PpTheme.colors.card,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Create Period", style = MaterialTheme.typography.titleLarge, color = PpTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            // Section: Period Setup
            PpCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Period Setup", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textPrimary)
                    Text("Define when this period starts and how long it lasts.", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    PpTextField(
                        value = startDate.format(fmt),
                        onValueChange = {
                            try { startDate = LocalDate.parse(it, fmt) } catch (_: Exception) {}
                        },
                        label = "Start date",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (endRuleMode == "duration") {
                        Spacer(modifier = Modifier.height(12.dp))
                        PpTextField(
                            value = duration.toString(),
                            onValueChange = { duration = it.toIntOrNull() ?: 1 },
                            label = "Duration",
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Unit", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("days", "weeks", "months").forEach { unit ->
                                FilterChip(
                                    selected = durationUnit == unit,
                                    onClick = { durationUnit = unit },
                                    label = { Text(unit.replaceFirstChar { it.uppercase() }) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PpTheme.colors.primary.copy(alpha = 0.15f),
                                        selectedLabelColor = PpTheme.colors.primary,
                                        containerColor = Color.Transparent,
                                        labelColor = PpTheme.colors.textSecondary,
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = PpTheme.colors.border,
                                        selectedBorderColor = PpTheme.colors.primary.copy(alpha = 0.3f),
                                        enabled = true,
                                        selected = durationUnit == unit,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Section: End Rule
            PpCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("End Rule", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = endRuleMode == "duration",
                            onClick = { endRuleMode = "duration" },
                            label = { Text("By Duration") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PpTheme.colors.primary.copy(alpha = 0.15f),
                                selectedLabelColor = PpTheme.colors.primary,
                            ),
                        )
                        FilterChip(
                            selected = endRuleMode == "manual",
                            onClick = { endRuleMode = "manual" },
                            label = { Text("Set Manually") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PpTheme.colors.primary.copy(alpha = 0.15f),
                                selectedLabelColor = PpTheme.colors.primary,
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (endRuleMode == "duration") {
                        Text("Calculated end date", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                        Text(
                            calculatedEndDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                            style = MaterialTheme.typography.titleSmall,
                            color = PpTheme.colors.textPrimary,
                        )
                    } else {
                        PpTextField(
                            value = manualEndDate.format(fmt),
                            onValueChange = {
                                try { manualEndDate = LocalDate.parse(it, fmt) } catch (_: Exception) {}
                            },
                            label = "End date",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Section: Naming
            PpCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Naming", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    PpTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Period name",
                        placeholder = "e.g. March 2026",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            PpButton(
                text = "Create period",
                onClick = {
                    onSave(
                        name.trim(),
                        startDate.format(fmt),
                        effectiveEndDate.format(fmt),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.trim().length >= 3 && effectiveEndDate.isAfter(startDate),
            )
        }
    }
}
