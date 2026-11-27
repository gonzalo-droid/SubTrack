package com.gondroid.subtrack.domain.usecase.payment

import com.gondroid.subtrack.domain.model.MemberPaymentStatus
import com.gondroid.subtrack.domain.model.MonthlyPaymentSummary
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import com.gondroid.subtrack.domain.util.recentMonthKeys

class GetPaymentHistoryUseCase(
    private val paymentRepository: PaymentRepository,
    private val memberRepository: MemberRepository
) {
    operator fun invoke(subscriptionId: String, months: Int = 3): Flow<List<MonthlyPaymentSummary>> =
        combine(
            paymentRepository.getPaymentsForSubscription(subscriptionId),
            memberRepository.getMembersForSubscription(subscriptionId)
        ) { payments, activeMembers ->
            val targetMonthKeys = recentMonthKeys(months)

            targetMonthKeys.map { monthKey ->
                val monthPayments = payments.filter { it.monthKey == monthKey }
                val totalCollected = monthPayments
                    .filter { it.status == PaymentStatus.PAID || it.status == PaymentStatus.LATE }
                    .sumOf { it.amount }

                val memberStatuses = activeMembers.map { member ->
                    val payment = monthPayments.find { it.memberId == member.id }
                    MemberPaymentStatus(
                        memberId = member.id,
                        memberName = member.name,
                        status = payment?.status
                    )
                }
                MonthlyPaymentSummary(
                    monthKey = monthKey,
                    totalCollected = totalCollected,
                    memberStatuses = memberStatuses
                )
            }
        }
}
