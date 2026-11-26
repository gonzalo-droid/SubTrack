package com.gondroid.subtrack.domain.util

import java.util.Calendar
import kotlin.math.roundToInt

private const val DAY_MS = 24 * 60 * 60 * 1000.0

/**
 * Next date (today included) on which a subscription with [cutoffDay] is charged.
 * Days beyond the month length fall on its last day (31 → 30 in November).
 */
fun nextCutoff(cutoffDay: Int, today: Calendar = Calendar.getInstance()): Calendar {
    val start = startOfDay(today)
    val thisMonth = clampedDay(start, cutoffDay)
    if (!thisMonth.before(start)) return thisMonth
    val nextMonth = (start.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        add(Calendar.MONTH, 1)
    }
    return clampedDay(nextMonth, cutoffDay)
}

/** Whole days from [today] until [nextCutoff]; 0 means the cutoff is today. */
fun daysUntilCutoff(cutoffDay: Int, today: Calendar = Calendar.getInstance()): Int {
    val start = startOfDay(today)
    return ((nextCutoff(cutoffDay, start).timeInMillis - start.timeInMillis) / DAY_MS).roundToInt()
}

private fun startOfDay(cal: Calendar): Calendar = (cal.clone() as Calendar).apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun clampedDay(monthRef: Calendar, day: Int): Calendar = (monthRef.clone() as Calendar).apply {
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.DAY_OF_MONTH, day.coerceIn(1, getActualMaximum(Calendar.DAY_OF_MONTH)))
}
