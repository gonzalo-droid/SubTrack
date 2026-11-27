package com.gondroid.subtrack.domain.usecase.subscription

import java.util.Locale
import com.gondroid.subtrack.domain.model.DebtorInfo
import com.gondroid.subtrack.domain.model.enums.isOverdue
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

/** Members who owe the current user, only in subscriptions the user administers. */
class GetDebtorsUseCase(
    private val repository: SubscriptionRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<DebtorInfo>> =
        combine(repository.getSharedSubscriptions(), userRepository.getCurrentUser()) { subscriptions, user ->
            val cal = Calendar.getInstance()
            val monthKey = "${cal.get(Calendar.YEAR)}-${
                String.format(Locale.ROOT, "%02d", cal.get(Calendar.MONTH) + 1)
            }"
            subscriptions
                .filter { it.ownerId == user?.id && !it.isPaused }
                .flatMap { sub ->
                    sub.members
                        .filter { it.currentStatus.isOverdue }
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
