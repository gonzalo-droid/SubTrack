package com.gondroid.subtrack.domain.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Next date (today included) on which a subscription with [cutoffDay] is charged.
 * Days beyond the month length fall on its last day (31 → 30 in November).
 */
fun nextCutoff(cutoffDay: Int, today: LocalDate = today()): LocalDate {
    val thisMonth = clampedDay(today.year, today.month, cutoffDay)
    if (thisMonth >= today) return thisMonth
    val nextMonth = LocalDate(today.year, today.month, 1).plus(1, DateTimeUnit.MONTH)
    return clampedDay(nextMonth.year, nextMonth.month, cutoffDay)
}

/** Whole days from [today] until [nextCutoff]; 0 means the cutoff is today. */
fun daysUntilCutoff(cutoffDay: Int, today: LocalDate = today()): Int =
    today.daysUntil(nextCutoff(cutoffDay, today))

private fun clampedDay(year: Int, month: Month, day: Int): LocalDate {
    val first = LocalDate(year, month, 1)
    val lastDay = first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
    return LocalDate(year, month, day.coerceIn(1, lastDay))
}
