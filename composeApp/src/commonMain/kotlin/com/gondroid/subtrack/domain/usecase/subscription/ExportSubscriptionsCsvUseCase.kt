package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.first
import com.gondroid.subtrack.domain.util.formatTwoDecimals

/** "Exportar mis datos": every subscription as CSV (one row per subscription). */
class ExportSubscriptionsCsvUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(): String = toCsv(repository.getAllSubscriptions().first())

    internal fun toCsv(subscriptions: List<Subscription>): String = buildString {
        appendLine("nombre,monto,moneda,ciclo,dia_corte,compartida,miembros,pausada")
        subscriptions.sortedBy { it.name.lowercase() }.forEach { s ->
            appendLine(
                listOf(
                    csvField(s.name),
                    formatTwoDecimals(s.totalAmount),
                    s.currency,
                    s.cycle.name,
                    s.cutoffDay,
                    s.isShared,
                    s.members.size,
                    s.isPaused
                ).joinToString(",")
            )
        }
    }

    /** Quotes a field when it contains a comma, quote or newline (RFC 4180). */
    private fun csvField(value: String): String =
        if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"" + value.replace("\"", "\"\"") + "\"" else value
}
