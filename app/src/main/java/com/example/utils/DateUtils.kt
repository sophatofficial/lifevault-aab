package com.example.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for formatting dates and timestamps into human-readable formats.
 */
object DateUtils {
    /**
     * Formats an epoch timestamp in milliseconds into a human-readable date string.
     *
     * @param timestamp Milliseconds since Epoch.
     * @param pattern Date format pattern, defaults to 'dd MMM yyyy' (e.g., '31 Jul 2026').
     * @param locale Locale to use for formatting, defaults to [Locale.getDefault].
     * @return Formatted date string (e.g., "31 Jul 2026").
     */
    fun formatTimestamp(
        timestamp: Long,
        pattern: String = "dd MMM yyyy",
        locale: Locale = Locale.getDefault()
    ): String {
        return try {
            val formatter = SimpleDateFormat(pattern, locale)
            formatter.format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
}

/**
 * Extension function on [Long] timestamp to format into human-readable date string.
 *
 * Example:
 * ```
 * note.timestamp.toFormattedDate() // "31 Jul 2026"
 * ```
 */
fun Long.toFormattedDate(pattern: String = "dd MMM yyyy"): String {
    return DateUtils.formatTimestamp(this, pattern)
}
