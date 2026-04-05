package com.piggypulse.android.design.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.piggypulse.android.design.theme.PpTheme

data class KebabMenuItem(
    val label: String,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
)

@Composable
fun PpKebabMenu(
    items: List<KebabMenuItem>,
    modifier: Modifier = Modifier,
    contentDescription: String = "More actions",
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded = true },
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = contentDescription,
            tint = PpTheme.colors.textSecondary,
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = item.label,
                        color = if (item.isDestructive) PpTheme.colors.destructive else Color.Unspecified,
                    )
                },
                onClick = {
                    expanded = false
                    item.onClick()
                },
            )
        }
    }
}
