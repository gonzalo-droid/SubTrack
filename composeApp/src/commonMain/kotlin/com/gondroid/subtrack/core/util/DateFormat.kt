package com.gondroid.subtrack.core.util

import androidx.compose.runtime.Composable
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.month_names_short
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import kotlin.time.Instant

/** Calendar date of an epoch-millis timestamp in the device's time zone. */
fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).date

/** "nov 2026", or "nov 26" with [shortYear]; month names come from the app language. */
@Composable
fun monthYear(millis: Long, shortYear: Boolean = false): String {
    val date = millis.toLocalDate()
    val year = if (shortYear) (date.year % 100).toString().padStart(2, '0') else date.year.toString()
    return "${stringArrayResource(Res.array.month_names_short)[date.month.ordinal]} $year"
}

/** "25 nov 2026". */
@Composable
fun dayMonthYear(millis: Long): String {
    val date = millis.toLocalDate()
    return "${date.day} ${stringArrayResource(Res.array.month_names_short)[date.month.ordinal]} ${date.year}"
}
