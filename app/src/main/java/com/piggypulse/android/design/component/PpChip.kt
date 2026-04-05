package com.piggypulse.android.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun PpChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        } else {
            null
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PpTheme.colors.primary.copy(alpha = 0.15f),
            selectedLabelColor = PpTheme.colors.primary,
            selectedLeadingIconColor = PpTheme.colors.primary,
            containerColor = Color.Transparent,
            labelColor = PpTheme.colors.textSecondary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = PpTheme.colors.border,
            selectedBorderColor = PpTheme.colors.primary.copy(alpha = 0.3f),
            enabled = true,
            selected = selected,
        ),
    )
}
