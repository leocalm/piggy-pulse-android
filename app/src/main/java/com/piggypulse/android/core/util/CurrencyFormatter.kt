package com.piggypulse.android.core.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

object CurrencyFormatter {

    fun format(
        amountInCents: Long,
        currencyCode: String = "EUR",
        locale: Locale = Locale.getDefault(),
        showSymbol: Boolean = true,
        compact: Boolean = false,
    ): String {
        val currency = try {
            Currency.getInstance(currencyCode)
        } catch (_: IllegalArgumentException) {
            Currency.getInstance("EUR")
        }

        val decimalPlaces = currency.defaultFractionDigits
        val amount = amountInCents.toDouble() / 10.0.pow(decimalPlaces)

        if (compact) {
            return formatCompact(amount, currency, locale, showSymbol)
        }

        val formatter = if (showSymbol) {
            NumberFormat.getCurrencyInstance(locale).apply {
                this.currency = currency
                minimumFractionDigits = decimalPlaces
                maximumFractionDigits = decimalPlaces
            }
        } else {
            NumberFormat.getNumberInstance(locale).apply {
                minimumFractionDigits = decimalPlaces
                maximumFractionDigits = decimalPlaces
            }
        }

        return formatter.format(amount)
    }

    fun centsToDisplay(amountInCents: Long, decimalPlaces: Int = 2): Double {
        return amountInCents.toDouble() / 10.0.pow(decimalPlaces)
    }

    fun displayToCents(displayValue: Double, decimalPlaces: Int = 2): Long {
        return (displayValue * 10.0.pow(decimalPlaces)).roundToLong()
    }

    private fun formatCompact(
        amount: Double,
        currency: Currency,
        locale: Locale,
        showSymbol: Boolean,
    ): String {
        val absAmount = abs(amount)
        val (scaled, suffix) = when {
            absAmount >= 1_000_000 -> (amount / 1_000_000) to "M"
            absAmount >= 1_000 -> (amount / 1_000) to "K"
            else -> amount to ""
        }

        val formatted = if (suffix.isNotEmpty()) {
            val formatter = NumberFormat.getNumberInstance(locale).apply {
                minimumFractionDigits = 1
                maximumFractionDigits = 1
            }
            formatter.format(scaled) + suffix
        } else {
            NumberFormat.getNumberInstance(locale).apply {
                minimumFractionDigits = currency.defaultFractionDigits
                maximumFractionDigits = currency.defaultFractionDigits
            }.format(amount)
        }

        if (!showSymbol) return formatted

        // Determine whether the locale places the currency symbol before or after the number.
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = currency
        }
        val sample = currencyFormatter.format(1.0)
        val symbol = currency.getSymbol(locale)
        return if (sample.startsWith(symbol) || sample.startsWith(currency.currencyCode)) {
            "$symbol$formatted"
        } else {
            "$formatted$symbol"
        }
    }
}
