package com.piggypulse.android.feature.overlays

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
import androidx.compose.material3.LinearProgressIndicator
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
import com.piggypulse.android.core.model.CreateOverlayRequest
import com.piggypulse.android.core.model.OverlayItem
import com.piggypulse.android.core.model.UpdateOverlayRequest
import com.piggypulse.android.core.util.CurrencyFormatter
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
fun OverlaysScreen(
    currencyCode: String,
    onNavigateBack: () -> Unit,
    viewModel: OverlaysViewModel = hiltViewModel(),
) {
    val overlays by viewModel.overlays.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editingOverlay by viewModel.editingOverlay.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Overlays",
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
            ) { Icon(Icons.Default.Add, contentDescription = "Add overlay") }
        },
    ) { innerPadding ->
        if (isLoading) {
            PpLoadingIndicator(fullScreen = true, modifier = Modifier.padding(innerPadding))
        } else if (overlays.isEmpty()) {
            PpEmptyState(
                title = "No overlays",
                message = "Create an overlay to track temporary spending plans",
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
                items(overlays, key = { it.id }) { overlay ->
                    OverlayCard(
                        overlay = overlay,
                        currencyCode = currencyCode,
                        onEdit = { viewModel.openEditForm(overlay) },
                        onDelete = { viewModel.delete(overlay.id) },
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showForm) {
        OverlayFormSheet(
            overlay = editingOverlay,
            onSave = { viewModel.create(it) },
            onUpdate = { id, req -> viewModel.update(id, req) },
            onDismiss = { viewModel.closeForm() },
        )
    }
}

@Composable
private fun OverlayCard(
    overlay: OverlayItem,
    currencyCode: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    PpCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${overlay.icon ?: ""} ${overlay.name}".trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PpTheme.colors.textPrimary,
                    )
                    Text(
                        text = "${overlay.startDate} – ${overlay.endDate}",
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
            if (overlay.totalCap != null && overlay.totalCap > 0) {
                val spent = overlay.totalSpent ?: 0
                val progress = (spent.toFloat() / overlay.totalCap).coerceIn(0f, 1f)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "${CurrencyFormatter.format(spent, currencyCode)} / ${CurrencyFormatter.format(overlay.totalCap, currencyCode)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PpTheme.colors.textSecondary,
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = PpTheme.colors.primary,
                    trackColor = PpTheme.colors.border,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverlayFormSheet(
    overlay: OverlayItem?,
    onSave: (CreateOverlayRequest) -> Unit,
    onUpdate: (String, UpdateOverlayRequest) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEditing = overlay != null
    var name by remember { mutableStateOf(overlay?.name ?: "") }
    var icon by remember { mutableStateOf(overlay?.icon ?: "") }
    var startDate by remember { mutableStateOf(overlay?.startDate ?: LocalDate.now().toString()) }
    var endDate by remember { mutableStateOf(overlay?.endDate ?: LocalDate.now().plusDays(30).toString()) }
    var capText by remember { mutableStateOf(overlay?.totalCap?.let { CurrencyFormatter.centsToDisplay(it).toString() } ?: "") }

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
            Text(
                text = if (isEditing) "Edit overlay" else "New overlay",
                style = MaterialTheme.typography.titleLarge,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PpTextField(value = name, onValueChange = { name = it }, label = "Name", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            PpTextField(value = startDate, onValueChange = { startDate = it }, label = "Start date (YYYY-MM-DD)", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            PpTextField(value = endDate, onValueChange = { endDate = it }, label = "End date (YYYY-MM-DD)", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            PpTextField(value = capText, onValueChange = { capText = it }, label = "Cap (optional)", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            PpButton(
                text = if (isEditing) "Save changes" else "Create overlay",
                onClick = {
                    val cap = capText.toDoubleOrNull()?.let { CurrencyFormatter.displayToCents(it) }
                    if (isEditing) {
                        onUpdate(overlay!!.id, UpdateOverlayRequest(name, icon.ifBlank { null }, startDate, endDate, cap))
                    } else {
                        onSave(CreateOverlayRequest(name, icon.ifBlank { null }, startDate, endDate, cap))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
            )
        }
    }
}
