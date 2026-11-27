package com.gondroid.subtrack.feature.subscriptiondetail.history

import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class PunctualityTest {
    private fun p(status: PaymentStatus) = Payment("id", "s", "m", "2026-01", 1.0, status, null, null, null)

    @Test fun pendingMonthsAreNotDecidedYet() {
        assertNull(punctuality(listOf(p(PaymentStatus.PENDING))))
    }

    @Test fun lateAndOverdueCountAgainstPunctuality() {
        assertEquals(50, punctuality(listOf(p(PaymentStatus.PAID), p(PaymentStatus.LATE), p(PaymentStatus.PENDING))))
        assertEquals(0, punctuality(listOf(p(PaymentStatus.OVERDUE))))
    }
}
