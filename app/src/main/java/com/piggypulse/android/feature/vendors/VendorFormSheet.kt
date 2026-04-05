package com.piggypulse.android.feature.vendors

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.CreateVendorRequest
import com.piggypulse.android.core.model.UpdateVendorRequest
import com.piggypulse.android.core.model.VendorSummary
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorFormSheet(
    vendor: VendorSummary?,
    onSave: (CreateVendorRequest) -> Unit,
    onUpdate: (String, UpdateVendorRequest) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEditing = vendor != null
    var name by remember { mutableStateOf(vendor?.name ?: "") }
    var description by remember { mutableStateOf(vendor?.description ?: "") }

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
                text = if (isEditing) "Edit vendor" else "New vendor",
                style = MaterialTheme.typography.titleLarge,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            PpTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description (optional)",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
            )
            Spacer(modifier = Modifier.height(24.dp))

            PpButton(
                text = if (isEditing) "Save changes" else "Create vendor",
                onClick = {
                    val desc = description.ifBlank { null }
                    if (isEditing) {
                        onUpdate(vendor!!.id, UpdateVendorRequest(name, desc))
                    } else {
                        onSave(CreateVendorRequest(name, desc))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.length >= 3,
            )
        }
    }
}
