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
            .filter { it.ownerId != user.id && !it.isPaused }
            .mapNotNull { sub ->
                val me = sub.members.find { it.userId == user.id } ?: return@mapNotNull null
                sub.ownerId to SubscriptionParticipation(
                    subscriptionId = sub.id,
                    subscriptionName = sub.name,
                    brandColor = sub.brandColor,
                    status = me.currentStatus,
                    shareAmount = me.shareAmount,
                    isArchived = false
                ) to me.joinedAt
            }
            .groupBy({ it.first.first }, { it })
            .mapNotNull { (ownerId, entries) ->
                val owner = userRepository.getUserById(ownerId) ?: return@mapNotNull null
                val participations = entries.map { it.first.second }
                PersonSummary(
                    userId = owner.id,
                    name = owner.name,
                    phone = owner.phone,
                    hasApp = true,
                    totalDebt = participations.filter { it.status.isOverdue }.sumOf { it.shareAmount },
                    totalMonthlyContribution = participations.sumOf { it.shareAmount },
                    punctualityPercent = null,
                    firstJoinedAt = entries.minOf { it.second },
                    lastActivityAt = null,
                    subscriptions = participations
                )
            }
            .sortedByDescending { it.totalDebt }
    }
}
