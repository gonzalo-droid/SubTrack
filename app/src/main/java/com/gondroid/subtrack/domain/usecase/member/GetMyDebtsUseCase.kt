package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.SubscriptionParticipation
import com.gondroid.subtrack.domain.model.enums.isOverdue
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * "Debes": the admins of subscriptions where the current user is a member, one entry per
 * admin, with what the user pays them and how much is overdue.
 */
class GetMyDebtsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<PersonSummary>> = combine(
        subscriptionRepository.getSharedSubscriptions(),
        userRepository.getCurrentUser()
    ) { subscriptions, user ->
        if (user == null) return@combine emptyList()
        subscriptions
            .filter { it.ownerId != user.id }
            .mapNotNull { sub ->
                val me = sub.members.find { it.userId == user.id } ?: return@mapNotNull null
                sub.ownerId to SubscriptionParticipation(
                    subscriptionId = sub.id,
                    subscriptionName = sub.name,
                    brandColor = sub.brandColor,
                    status = me.currentStatus,
                    shareAmount = me.shareAmount,
                    isArchived = false,
                    isPaused = sub.isPaused
                ) to me.joinedAt
            }
            .groupBy({ it.first.first }, { it })
            .map { (ownerId, entries) ->
                // An admin we can't resolve still counts; the UI shows a fallback name for a blank one.
                val owner = userRepository.getUserById(ownerId)
                val participations = entries.map { it.first.second }
                PersonSummary(
                    userId = ownerId,
                    name = owner?.name.orEmpty(),
                    phone = owner?.phone.orEmpty(),
                    hasApp = true,
                    totalDebt = participations.filter { !it.isPaused && it.status.isOverdue }.sumOf { it.shareAmount },
                    totalMonthlyContribution = participations.filter { !it.isPaused }.sumOf { it.shareAmount },
                    punctualityPercent = null,
                    firstJoinedAt = entries.minOf { it.second },
                    lastActivityAt = null,
                    subscriptions = participations
                )
            }
            .sortedWith(compareBy<PersonSummary> { it.isAllPaused }.thenByDescending { it.totalDebt })
    }
}
