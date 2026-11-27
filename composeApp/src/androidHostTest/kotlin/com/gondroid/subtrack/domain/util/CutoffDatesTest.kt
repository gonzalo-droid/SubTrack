package com.gondroid.subtrack.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class CutoffDatesTest {

    private fun date(y: Int, m: Int, d: Int, hour: Int = 15): Calendar =
        Calendar.getInstance().apply { clear(); set(y, m - 1, d, hour, 30) }

    private fun Calendar.ymd() = Triple(get(Calendar.YEAR), get(Calendar.MONTH) + 1, get(Calendar.DAY_OF_MONTH))

    @Test fun `cutoff later this month stays in month`() {
        assertEquals(Triple(2026, 11, 28), nextCutoff(28, date(2026, 11, 25)).ymd())
        assertEquals(3, daysUntilCutoff(28, date(2026, 11, 25)))
    }

    @Test fun `cutoff today counts as today`() {
        assertEquals(Triple(2026, 11, 25), nextCutoff(25, date(2026, 11, 25)).ymd())
        assertEquals(0, daysUntilCutoff(25, date(2026, 11, 25)))
    }

    @Test fun `past cutoff moves to next month`() {
        assertEquals(Triple(2026, 12, 5), nextCutoff(5, date(2026, 11, 25)).ymd())
        assertEquals(10, daysUntilCutoff(5, date(2026, 11, 25)))
    }

    @Test fun `day 31 clamps to last day of a 30-day month`() {
        assertEquals(Triple(2026, 11, 30), nextCutoff(31, date(2026, 11, 25)).ymd())
    }

    @Test fun `december rolls over to january`() {
        assertEquals(Triple(2027, 1, 1), nextCutoff(1, date(2026, 12, 15)).ymd())
        assertEquals(17, daysUntilCutoff(1, date(2026, 12, 15)))
    }

    @Test fun `next month clamps february in leap and common years`() {
        assertEquals(Triple(2028, 2, 29), nextCutoff(31, date(2028, 2, 1)).ymd())
        assertEquals(Triple(2027, 2, 28), nextCutoff(30, date(2027, 2, 1)).ymd())
    }
}
