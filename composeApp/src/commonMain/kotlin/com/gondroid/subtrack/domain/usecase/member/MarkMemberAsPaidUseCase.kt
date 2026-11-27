package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.repository.PaymentRepository

class MarkMemberAsPaidUseCase(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(memberId: String, monthKey: String, proofUrl: String? = null) =
        repository.markAsPaid(memberId, monthKey, proofUrl)
}
