package com.piggypulse.android.feature.periods

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.CreateScheduleRequest
import com.piggypulse.android.core.model.PeriodScheduleResponse
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpDestructiveButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCreationSheet(
    schedule: PeriodScheduleResponse?,
    onSave: (CreateScheduleRequest) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isExisting = schedule != null && schedule.scheduleType == "automatic"

    var recurrenceMethod by remember { mutableStateOf(schedule?.recurrenceMethod ?: "dayOfMonth") }
    var startDay by remember { mutableIntStateOf(schedule?.startDayOfTheMonth ?: 1) }
    var durationValue by remember { mutableIntStateOf(schedule?.periodDuration ?: 1) }
    var durationUnit by remember { mutableStateOf(schedule?.durationUnit ?: "months") }
    var saturdayPolicy by remember { mutableStateOf(schedule?.saturdayPolicy ?: "keep") }
    var sundayPolicy by remember { mutableStateOf(schedule?.sundayPolicy ?: "keep") }
    var namePattern by remember { mutableStateOf(schedule?.namePattern ?: "{MONTH} {YEAR}") }
    var generateAhead by remember { mutableIntStateOf(schedule?.generateAhead ?: 3) }

    // Preview
    val now = LocalDate.now()
    val preview = namePattern
        .replace("{MONTH}", now.month.getDisplayName(TextStyle.FULL, Locale.getDefault()))
        .replace("{MONTH_SHORT}", now.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
        .replace("{YEAR}", now.year.toString())
        .replace("{YEAR_SHORT}", now.year.toString().takeLast(2))
        .replace("{PERIOD_NUMBER}", "1")
        .ifEmpty { "—" }

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
            Text("Auto-creation", style = MaterialTheme.typography.titleLarge, color = PpTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            // Recurrence Method
            PpCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("RECURRENCE METHOD", style = MaterialTheme.typography.labelMedium, color = PpTheme.colors.textSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    RecurrenceOption("dayOfMonth", "Day of Month", "Period starts on a fixed calendar day", recurrenceMethod) { recurrenceMethod = it }
                    Spacer(modifier = Modifier.height(4.dp))
                    RecurrenceOption("businessDay", "Business Day", "Period starts on Nth business day", recurrenceMethod) { recurrenceMethod = it }
                    Spacer(modifier = Modifier.height(4.dp))
                    RecurrenceOption("dayOfWeek", "Day of Week", "Period starts on a specific weekday", recurrenceMethod) { recurrenceMethod = it }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Period Setup
            PpCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Period Setup", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (recurrenceMethod != "dayOfWeek") {
                        PpTextField(
                            value = startDay.toString(),
                            onValueChange = { startDay = it.toIntOrNull()?.coerceIn(1, 31) ?: 1 },
                            label = if (recurrenceMethod == "businessDay") "Start business day" else "Start day of month",
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    PpTextField(
                        value = durationValue.toString(),
                        onValueChange = { durationValue = it.toIntOrNull()?.coerceAtLeast(1) ?: 1 },
                        label = "Duration",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    PpTextField(
                        value = generateAhead.toString(),
                        onValueChange = { generateAhead = it.toIntOrNull()?.coerceIn(0, 12) ?: 3 },
                        label = "Generate ahead (periods)",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Weekend Adjustments
            PpCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Weekend Adjustments", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    WeekendRow("Saturday", saturdayPolicy) { saturdayPolicy = it }
                    Spacer(modifier = Modifier.height(8.dp))
                    WeekendRow("Sunday", sundayPolicy) { sundayPolicy = it }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Naming
            PpCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Naming", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    PpTextField(
                        value = namePattern,
                        onValueChange = { namePattern = it },
                        label = "Name pattern",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "{MONTH} {MONTH_SHORT} {YEAR} {YEAR_SHORT} {PERIOD_NUMBER}",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = PpTheme.colors.textTertiary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PREVIEW", style = MaterialTheme.typography.labelSmall, color = PpTheme.colors.textSecondary)
                    Text(preview, style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textPrimary)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            PpButton(
                text = if (isExisting) "Save changes" else "Enable auto-creation",
                onClick = {
                    onSave(
                        CreateScheduleRequest(
                            recurrenceMethod = recurrenceMethod,
                            startDayOfTheMonth = startDay,
                            periodDuration = durationValue,
                            durationUnit = durationUnit,
                            saturdayPolicy = saturdayPolicy,
                            sundayPolicy = sundayPolicy,
                            namePattern = namePattern.trim(),
                            generateAhead = generateAhead,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = namePattern.isNotBlank(),
            )

            if (isExisting) {
                Spacer(modifier = Modifier.height(8.dp))
                PpDestructiveButton(
                    text = "Disable auto-creation",
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun RecurrenceOption(
    value: String,
    title: String,
    description: String,
    selected: String,
    onSelect: (String) -> Unit,
) {
    val isSelected = selected == value
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) },
        color = if (isSelected) PpTheme.colors.primary.copy(alpha = 0.08f) else PpTheme.colors.elevated,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) PpTheme.colors.primary else PpTheme.colors.border,
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onSelect(value) },
                colors = RadioButtonDefaults.colors(selectedColor = PpTheme.colors.primary),
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = if (isSelected) PpTheme.colors.textPrimary else PpTheme.colors.textSecondary)
                Text(description, style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textTertiary)
            }
        }
    }
}

@Composable
private fun WeekendRow(
    day: String,
    policy: String,
    onPolicyChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("If $day:", style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("keep" to "Keep", "friday" to "→ Fri", "monday" to "→ Mon").forEach { (value, label) ->
                FilterChip(
                    selected = policy == value,
                    onClick = { onPolicyChange(value) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PpTheme.colors.primary.copy(alpha = 0.15f),
                        selectedLabelColor = PpTheme.colors.primary,
                        containerColor = Color.Transparent,
                        labelColor = PpTheme.colors.textSecondary,
                    ),
                )
            }
        }
    }
}
