package com.piggypulse.android.design.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.piggypulse.android.core.util.CurrencyFormatter
import java.util.Locale

@Composable
fun CurrencyText(
    amountInCents: Long,
    currencyCode: String = "EUR",
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    showSymbol: Boolean = true,
    compact: Boolean = false,
    locale: Locale = Locale.getDefault(),
) {
    val formatted = CurrencyFormatter.format(
        amountInCents = amountInCents,
        currencyCode = currencyCode,
        locale = locale,
        showSymbol = showSymbol,
        compact = compact,
    )
    Text(
        text = formatted,
        modifier = modifier,
        style = style,
        color = color,
    )
}
