package com.piggypulse.android.feature.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piggypulse.android.design.component.CurrencyText
import com.piggypulse.android.design.component.PpCard
import com.piggypulse.android.design.component.PpLoadingIndicator
import com.piggypulse.android.design.component.PpTopBar
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun AccountDetailScreen(
    accountId: String,
    currencyCode: String,
    onNavigateBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel(),
) {
    val details by viewModel.accountDetails.collectAsState()
    val isLoading by viewModel.isLoadingDetails.collectAsState()

    LaunchedEffect(accountId) {
        viewModel.loadDetails(accountId)
    }

    Scaffold(
        containerColor = PpTheme.colors.background,
        topBar = {
            PpTopBar(
                title = details?.name ?: "Account",
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
        if (isLoading || details == null) {
            PpLoadingIndicator(
                fullScreen = true,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            val account = details!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Balance card
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.titleSmall,
                            color = PpTheme.colors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CurrencyText(
                            amountInCents = account.currentBalance,
                            currencyCode = currencyCode,
                            style = MaterialTheme.typography.headlineMedium,
                            color = PpTheme.colors.textPrimary,
                        )
                    }
                }

                // Period summary
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "This Period",
                            style = MaterialTheme.typography.titleSmall,
                            color = PpTheme.colors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            StatItem("Inflows", account.inflow ?: 0, currencyCode)
                            StatItem("Outflows", account.outflow ?: 0, currencyCode)
                            StatItem("Net Change", account.netChangeThisPeriod ?: 0, currencyCode)
                        }
                    }
                }

                // Transactions count
                if (account.numberOfTransactions != null) {
                    PpCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Transactions",
                                style = MaterialTheme.typography.titleSmall,
                                color = PpTheme.colors.textSecondary,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${account.numberOfTransactions} this period",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PpTheme.colors.textPrimary,
                            )
                        }
                    }
                }

                // Chart placeholder
                PpCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Balance History",
                            style = MaterialTheme.typography.titleSmall,
                            color = PpTheme.colors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chart coming in M9",
                            style = MaterialTheme.typography.bodySmall,
                            color = PpTheme.colors.textTertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    amountInCents: Long,
    currencyCode: String,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = PpTheme.colors.textSecondary,
        )
        CurrencyText(
            amountInCents = amountInCents,
            currencyCode = currencyCode,
            style = MaterialTheme.typography.bodyMedium,
            color = PpTheme.colors.textPrimary,
        )
    }
}
