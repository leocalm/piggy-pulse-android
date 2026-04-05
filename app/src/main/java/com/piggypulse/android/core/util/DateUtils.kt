package com.piggypulse.android.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {

    private val apiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun parseApiDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, apiFormat)
    }

    fun formatApiDate(date: LocalDate): String {
        return date.format(apiFormat)
    }

    fun formatDisplay(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale))
    }

    fun formatShort(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern("d MMM", locale)
        return date.format(formatter)
    }

    fun formatDateRange(start: LocalDate, end: LocalDate, locale: Locale = Locale.getDefault()): String {
        return "${formatShort(start, locale)} – ${formatShort(end, locale)}"
    }

    fun relativeDay(date: LocalDate): String {
        val today = LocalDate.now()
        val days = ChronoUnit.DAYS.between(date, today)
        return when (days) {
            0L -> "Today"
            1L -> "Yesterday"
            -1L -> "Tomorrow"
            else -> formatDisplay(date)
        }
    }

    fun daysRemaining(endDate: LocalDate): Long {
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate).coerceAtLeast(0)
    }
}
