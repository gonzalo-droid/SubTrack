package com.gondroid.subtrack.domain.usecase.payment

import com.gondroid.subtrack.domain.model.MemberPaymentStatus
import com.gondroid.subtrack.domain.model.MonthlyPaymentSummary
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetPaymentHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val memberRepository: MemberRepository
) {
    operator fun invoke(subscriptionId: String, months: Int = 3): Flow<List<MonthlyPaymentSummary>> =
        combine(
            paymentRepository.getPaymentsForSubscription(subscriptionId),
            memberRepository.getMembersForSubscription(subscriptionId)
        ) { payments, activeMembers ->
            val targetMonthKeys = buildRecentMonthKeys(months)

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

    private fun buildRecentMonthKeys(count: Int): List<String> {
        val cal = Calendar.getInstance()
        return (0 until count).map {
            val key = "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
            cal.add(Calendar.MONTH, -1)
            key
        }.reversed()
    }
}
