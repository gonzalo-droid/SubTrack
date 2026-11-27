package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.repository.PaymentRepository

class MarkOwnPaymentUseCase(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(memberId: String, monthKey: String, note: String? = null): Result<Unit> =
        repository.markAsPaid(memberId, monthKey, proofUrl = null, note = note)
}
