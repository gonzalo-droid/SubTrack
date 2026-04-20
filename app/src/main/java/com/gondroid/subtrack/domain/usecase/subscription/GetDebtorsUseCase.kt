package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.DebtorInfo
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetDebtorsUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<List<DebtorInfo>> =
        repository.getSharedSubscriptions().map { subscriptions ->
            val cal = Calendar.getInstance()
            val monthKey = "${cal.get(Calendar.YEAR)}-${
                String.format("%02d", cal.get(Calendar.MONTH) + 1)
            }"
            subscriptions.flatMap { sub ->
                sub.members
                    .filter {
                        it.currentStatus == PaymentStatus.OVERDUE ||
                        it.currentStatus == PaymentStatus.LATE
                    }
                    .map { member ->
                        DebtorInfo(
                            memberId = member.id,
                            memberName = member.name,
                            memberPhone = member.phone,
                            hasApp = member.userId != null,
                            subscriptionId = sub.id,
                            subscriptionName = sub.name,
                            shareAmount = member.shareAmount,
                            status = member.currentStatus,
                            monthKey = monthKey
                        )
                    }
            }
        }
}
