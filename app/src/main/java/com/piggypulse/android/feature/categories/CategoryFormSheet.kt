package com.piggypulse.android.feature.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.piggypulse.android.core.model.CategoryListItem
import com.piggypulse.android.core.model.CreateCategoryRequest
import com.piggypulse.android.core.model.UpdateCategoryRequest
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormSheet(
    category: CategoryListItem?,
    defaultType: String,
    onSave: (CreateCategoryRequest) -> Unit,
    onUpdate: (String, UpdateCategoryRequest) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEditing = category != null
    var name by remember { mutableStateOf(category?.name ?: "") }
    var icon by remember { mutableStateOf(category?.icon ?: "\uD83D\uDCB0") }
    var color by remember { mutableStateOf(category?.color ?: "#8B7EC8") }

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
            Text(
                text = if (isEditing) "Edit category" else "New category",
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
                value = icon,
                onValueChange = { icon = it },
                label = "Icon (emoji)",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = color,
                onValueChange = { color = it },
                label = "Color (hex)",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))

            PpButton(
                text = if (isEditing) "Save changes" else "Create category",
                onClick = {
                    if (isEditing) {
                        onUpdate(category!!.id, UpdateCategoryRequest(name, icon, color, category.type))
                    } else {
                        onSave(CreateCategoryRequest(name, icon, color, defaultType))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.length >= 3,
            )
        }
    }
}
