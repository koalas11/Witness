package org.wdsl.witness.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Utility functions for formatting timestamps.
 *
 * @param timestamp The timestamp in milliseconds since epoch.
 * @return A formatted string representing the timestamp in "DD-MM-YYYY HH:MM:SS" format.
 */
@OptIn(ExperimentalTime::class)
fun getFormattedTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = dateTime.year.toString()
    val month = dateTime.month.number.toString().padStart(2, '0')
    val day = dateTime.day.toString().padStart(2, '0')
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    val second = dateTime.second.toString().padStart(2, '0')
    return "${day}-${month}-${year} ${hour}:${minute}:${second}"
}

/**
 * Utility function to generate a filename-friendly timestamp.
 *
 * @param timestamp The timestamp in milliseconds since epoch.
 * @return A formatted string representing the timestamp in "DDd_MMm_YYYYy_HHh_MMm_SSs" format.
 */
@OptIn(ExperimentalTime::class)
fun getFilenameTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = dateTime.year.toString()
    val month = dateTime.month.number.toString().padStart(2, '0')
    val day = dateTime.day.toString().padStart(2, '0')
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    val second = dateTime.second.toString().padStart(2, '0')
    return "${day}d${month}m${year}y-${hour}h${minute}m${second}s"
}
