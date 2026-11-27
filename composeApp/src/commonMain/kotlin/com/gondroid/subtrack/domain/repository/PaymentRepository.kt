package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getPaymentsForSubscription(subscriptionId: String): Flow<List<Payment>>
    fun getPaymentsForMember(memberId: String): Flow<List<Payment>>
    suspend fun markAsPaid(
        memberId: String,
        monthKey: String,
        proofUrl: String? = null,
        note: String? = null
    ): Result<Unit>
    suspend fun getPaymentStatus(memberId: String, monthKey: String): PaymentStatus
}
