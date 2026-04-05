package com.piggypulse.android.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggypulse.android.design.theme.PpTheme

@Composable
fun PpLoadingIndicator(
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false,
) {
    if (fullScreen) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = PpTheme.colors.primary,
            )
        }
    } else {
        CircularProgressIndicator(
            modifier = modifier.size(24.dp),
            color = PpTheme.colors.primary,
            strokeWidth = 2.dp,
        )
    }
}
