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
import androidx.compose.material3.ExperimentalMaterial3Api
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PpTheme.colors.textPrimary,
                        )
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

            // Profile section
            item {
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Profile", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentUser?.name ?: "User",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PpTheme.colors.textPrimary,
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textSecondary,
                        )
                    }
                }
            }

            // Appearance section
            item {
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Appearance", style = MaterialTheme.typography.titleSmall, color = PpTheme.colors.textSecondary)
                        Spacer(modifier = Modifier.height(12.dp))

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

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Mode", style = MaterialTheme.typography.bodySmall, color = PpTheme.colors.textSecondary)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            AppearanceMode.entries.forEach { mode ->
                                val selected = mode == appearanceMode
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { themeManager.setAppearanceMode(mode) },
                                    color = if (selected) PpTheme.colors.primary.copy(alpha = 0.15f)
                                    else PpTheme.colors.elevated,
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text(
                                        text = mode.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (selected) PpTheme.colors.primary else PpTheme.colors.textSecondary,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Logout
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PpDestructiveButton(
                    text = "Logout",
                    onClick = { appState.logout() },
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
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
        color = if (isSelected) theme.primary.copy(alpha = 0.2f) else PpTheme.colors.elevated,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.size(24.dp),
                color = theme.primary,
                shape = RoundedCornerShape(12.dp),
            ) {}
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = theme.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) PpTheme.colors.primary else PpTheme.colors.textSecondary,
                maxLines = 1,
            )
        }
    }
}
