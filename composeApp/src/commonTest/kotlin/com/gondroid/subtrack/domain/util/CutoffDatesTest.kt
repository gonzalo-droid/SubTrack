package com.gondroid.subtrack.domain.util

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CutoffDatesTest {

    @Test fun cutoffLaterThisMonthStaysInMonth() {
        assertEquals(LocalDate(2026, 11, 28), nextCutoff(28, LocalDate(2026, 11, 25)))
        assertEquals(3, daysUntilCutoff(28, LocalDate(2026, 11, 25)))
    }

    @Test fun cutoffTodayCountsAsToday() {
        assertEquals(LocalDate(2026, 11, 25), nextCutoff(25, LocalDate(2026, 11, 25)))
        assertEquals(0, daysUntilCutoff(25, LocalDate(2026, 11, 25)))
    }

    @Test fun pastCutoffMovesToNextMonth() {
        assertEquals(LocalDate(2026, 12, 5), nextCutoff(5, LocalDate(2026, 11, 25)))
        assertEquals(10, daysUntilCutoff(5, LocalDate(2026, 11, 25)))
    }

    @Test fun day31ClampsToLastDayOfA30DayMonth() {
        assertEquals(LocalDate(2026, 11, 30), nextCutoff(31, LocalDate(2026, 11, 25)))
    }

    @Test fun decemberRollsOverToJanuary() {
        assertEquals(LocalDate(2027, 1, 1), nextCutoff(1, LocalDate(2026, 12, 15)))
        assertEquals(17, daysUntilCutoff(1, LocalDate(2026, 12, 15)))
    }

    @Test fun nextMonthClampsFebruaryInLeapAndCommonYears() {
        assertEquals(LocalDate(2028, 2, 29), nextCutoff(31, LocalDate(2028, 2, 1)))
        assertEquals(LocalDate(2027, 2, 28), nextCutoff(30, LocalDate(2027, 2, 1)))
    }

    @Test fun recentMonthKeysEndInTheCurrentMonthAndCrossTheYear() {
        assertEquals(listOf("2026-11", "2026-12", "2027-01"), recentMonthKeys(3, LocalDate(2027, 1, 20)))
    }

    @Test fun twoDecimalsRoundsAndKeepsTheSign() {
        assertEquals("12.50", formatTwoDecimals(12.5))
        assertEquals("0.07", formatTwoDecimals(0.065000001))
        assertEquals("-3.10", formatTwoDecimals(-3.1))
        assertEquals("0.00", formatTwoDecimals(-0.001))
    }
}
