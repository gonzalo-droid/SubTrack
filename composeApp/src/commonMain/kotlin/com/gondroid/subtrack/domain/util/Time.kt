package com.gondroid.subtrack.domain.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** Epoch milliseconds, the unit every timestamp in the domain models uses. */
fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

/** Today in the device's time zone. */
fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

/** "2026-11": the key payments use to group a month. */
fun LocalDate.monthKey(): String = "$year-${month.ordinal.plus(1).toString().padStart(2, '0')}"

/** Keys of the last [count] months, oldest first, ending with the month of [today]. */
fun recentMonthKeys(count: Int, today: LocalDate = today()): List<String> {
    val firstOfMonth = LocalDate(today.year, today.month, 1)
    return (0 until count).map { firstOfMonth.minus(it, DateTimeUnit.MONTH).monthKey() }.reversed()
}

/** Random unique id (UUID v4 text) for new entities. */
@OptIn(ExperimentalUuidApi::class)
fun randomId(): String = Uuid.random().toString()
