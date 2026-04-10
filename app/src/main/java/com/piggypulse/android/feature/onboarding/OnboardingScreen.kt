package com.piggypulse.android.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.piggypulse.android.R
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
) {
    Surface(
        color = PpTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Header with gradient and logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PpTheme.colors.gradientStart, PpTheme.colors.gradientEnd),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.piggy_logo_white),
                        contentDescription = "PiggyPulse",
                        modifier = Modifier.height(80.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PiggyPulse",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your finance pulse, at a glance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Welcome to PiggyPulse",
                    style = MaterialTheme.typography.headlineSmall,
                    color = PpTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Track your spending, manage budgets, and gain insights into your financial life.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PpTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                PpButton(
                    text = "Get Started",
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth().testTag("onboarding-complete"),
                )
            }
        }
    }
}
