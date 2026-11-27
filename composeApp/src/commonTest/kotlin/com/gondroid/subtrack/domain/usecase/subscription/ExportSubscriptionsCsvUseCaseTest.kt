package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlin.test.assertEquals
import kotlin.test.Test
import com.gondroid.subtrack.testing.UnusedSubscriptions

class ExportSubscriptionsCsvUseCaseTest {

    private val unusedRepo = UnusedSubscriptions

    private fun sub(name: String, amount: Double, paused: Boolean = false) = Subscription(
        id = name, name = name, brandColor = "#000000", totalAmount = amount, cycle = BillingCycle.MONTHLY,
        cutoffDay = 5, ownerId = "me", isShared = false, category = SubscriptionCategory.OTHER,
        isPaused = paused, createdAt = 0, updatedAt = 0
    )

    @Test fun exportsSortedRowsWithUSDecimalsAndQuotesCommas() {
        val csv = ExportSubscriptionsCsvUseCase(unusedRepo).toCsv(
            listOf(sub("Spotify, Familiar", 26.9), sub("Netflix", 54.9, paused = true))
        )
        assertEquals(
            listOf(
                "nombre,monto,moneda,ciclo,dia_corte,compartida,miembros,pausada",
                "Netflix,54.90,PEN,MONTHLY,5,false,0,true",
                "\"Spotify, Familiar\",26.90,PEN,MONTHLY,5,false,0,false"
            ),
            csv.trim().lines()
        )
    }
}
