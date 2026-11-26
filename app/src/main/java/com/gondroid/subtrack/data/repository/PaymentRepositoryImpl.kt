package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {

    private val store = AppStore.payments

    override fun getPaymentsForSubscription(subscriptionId: String): Flow<List<Payment>> =
        store.map { list -> list.filter { it.subscriptionId == subscriptionId } }

    override fun getPaymentsForMember(memberId: String): Flow<List<Payment>> =
        store.map { list -> list.filter { it.memberId == memberId } }

    override suspend fun markAsPaid(
        memberId: String,
        monthKey: String,
        proofUrl: String?,
        note: String?
    ): Result<Unit> {
        val now = System.currentTimeMillis()
        store.value = store.value.map { payment ->
            if (payment.memberId == memberId && payment.monthKey == monthKey) {
                payment.copy(
                    status = PaymentStatus.PAID,
                    paidAt = now,
                    proofUrl = proofUrl,
                    note = note
                )
            } else payment
        }
        // Also update currentStatus on the member inside subscriptions
        AppStore.subscriptions.value = AppStore.subscriptions.value.map { sub ->
            sub.copy(
                members = sub.members.map { member ->
                    if (member.id == memberId) member.copy(currentStatus = PaymentStatus.PAID) else member
                }
            )
        }
        return Result.success(Unit)
    }

    override suspend fun getPaymentStatus(memberId: String, monthKey: String): PaymentStatus {
        return store.value
            .find { it.memberId == memberId && it.monthKey == monthKey }
            ?.status
            ?: PaymentStatus.PENDING
    }
}
