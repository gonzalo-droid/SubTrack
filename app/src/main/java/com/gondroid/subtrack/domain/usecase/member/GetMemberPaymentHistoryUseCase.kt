package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMemberPaymentHistoryUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(memberId: String, limit: Int = 6): Flow<List<Payment>> =
        repository.getPaymentsForMember(memberId).map { list ->
            list.sortedByDescending { it.monthKey }.take(limit)
        }
}
