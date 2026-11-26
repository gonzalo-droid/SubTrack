package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.enums.isOverdue
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * "Te deben": people who share the subscriptions the current user administers.
 * The user is left out, and totals only count those subscriptions.
 */
class GetAllPeopleWithSubscriptionsUseCase @Inject constructor(
    private val repository: MemberRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<PersonSummary>> = combine(
        repository.getAllPeopleWithSubscriptions(),
        subscriptionRepository.getAllSubscriptions(),
        userRepository.getCurrentUser()
    ) { people, subscriptions, user ->
        val ownedIds = subscriptions.filter { it.ownerId == user?.id && !it.isPaused }.map { it.id }.toSet()
        people
            .filter { it.userId == null || it.userId != user?.id }
            .mapNotNull { person ->
                val mine = person.subscriptions.filter { it.subscriptionId in ownedIds }
                if (mine.isEmpty()) return@mapNotNull null
                person.copy(
                    subscriptions = mine,
                    totalDebt = mine.filter { it.status.isOverdue }.sumOf { it.shareAmount },
                    totalMonthlyContribution = mine.filter { !it.isArchived }.sumOf { it.shareAmount }
                )
            }
            .sortedByDescending { it.totalDebt }
    }
}
