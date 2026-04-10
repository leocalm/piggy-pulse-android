package com.piggypulse.android.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.piggypulse.android.app.AppState
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpDestructiveButton
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.AppearanceMode
import com.piggypulse.android.design.theme.ColorTheme
import com.piggypulse.android.design.theme.PpTheme
import com.piggypulse.android.design.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appState: AppState,
    themeManager: ThemeManager,
    onNavigateBack: () -> Unit,
) {
    val currentUser by appState.currentUser.collectAsState()
    val colorTheme by themeManager.colorTheme.collectAsState()
    val appearanceMode by themeManager.appearanceMode.collectAsState()

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PpTheme.colors.textPrimary)
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Profile
            item {
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("PROFILE")
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsRow("Name", currentUser?.name ?: "User")
                        SettingsRow("Email", currentUser?.email ?: "—")
                        SettingsRow("Currency", currentUser?.currency ?: "—")
                        HorizontalDivider(color = PpTheme.colors.border, modifier = Modifier.padding(vertical = 8.dp))
                        ActionRow(
                            icon = Icons.Default.Edit,
                            label = "Edit profile",
                            color = PpTheme.colors.primary,
                            onClick = { /* TODO: edit profile sheet */ },
                        )
                    }
                }
            }

            // Security
            item {
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("SECURITY")
                        Spacer(modifier = Modifier.height(8.dp))
                        ActionRow(
                            icon = Icons.Default.Key,
                            label = "Change password",
                            color = PpTheme.colors.textPrimary,
                            onClick = { /* TODO: change password sheet */ },
                        )
                    }
                }
            }

            // Preferences
            item {
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("PREFERENCES")
                        Spacer(modifier = Modifier.height(12.dp))

                        // Color theme
                        Text("Color Theme", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            maxItemsInEachRow = 3,
                        ) {
                            ColorTheme.entries.forEach { theme ->
                                ThemeChip(
                                    theme = theme,
                                    isSelected = theme == colorTheme,
                                    onClick = { themeManager.setColorTheme(theme) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        HorizontalDivider(color = PpTheme.colors.border, modifier = Modifier.padding(vertical = 12.dp))

                        // Appearance mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Appearance", style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textSecondary)
                            Spacer(modifier = Modifier.weight(1f))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                AppearanceMode.entries.forEach { mode ->
                                    val selected = mode == appearanceMode
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { themeManager.setAppearanceMode(mode) },
                                        color = if (selected) PpTheme.colors.primary.copy(alpha = 0.15f) else PpTheme.colors.elevated,
                                        shape = RoundedCornerShape(8.dp),
                                    ) {
                                        Text(
                                            mode.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (selected) PpTheme.colors.primary else PpTheme.colors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // App Info
            item {
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("APP")
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsRow("Version", "1.0")
                        SettingsRow("Build", "1")
                    }
                }
            }

            // Danger Zone
            item {
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "DANGER ZONE",
                            style = MaterialTheme.typography.labelMedium,
                            color = PpTheme.colors.destructive,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActionRow(
                            icon = Icons.Default.DeleteForever,
                            label = "Delete account",
                            color = PpTheme.colors.destructive,
                            onClick = { /* TODO: delete account sheet */ },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "This will permanently delete your account and all associated data. This action cannot be undone.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                        )
                    }
                }
            }

            // Logout
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PpDestructiveButton(
                    text = "Logout",
                    onClick = { appState.logout() },
                    modifier = Modifier.testTag("settings-logout"),
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium,
        color = PpTheme.colors.textSecondary,
    )
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = PpTheme.colors.textPrimary)
    }
}

@Composable
private fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = color, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PpTheme.colors.textTertiary, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun ThemeChip(
    theme: ColorTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) theme.primary.copy(alpha = 0.15f) else PpTheme.colors.elevated,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Three overlapping color circles like iOS
            Row {
                Surface(modifier = Modifier.size(20.dp), color = theme.primary, shape = RoundedCornerShape(10.dp)) {}
                Surface(modifier = Modifier.size(20.dp).padding(start = 0.dp), color = theme.secondary, shape = RoundedCornerShape(10.dp)) {}
                Surface(modifier = Modifier.size(20.dp).padding(start = 0.dp), color = theme.tertiary, shape = RoundedCornerShape(10.dp)) {}
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                theme.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) PpTheme.colors.textPrimary else PpTheme.colors.textSecondary,
                maxLines = 1,
            )
        }
    }
}
