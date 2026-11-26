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
        val owned = subscriptions.filter { it.ownerId == user?.id }
        val ownedIds = owned.map { it.id }.toSet()
        val pausedIds = owned.filter { it.isPaused }.map { it.id }.toSet()
        people
            .filter { it.userId == null || it.userId != user?.id }
            .mapNotNull { person ->
                val mine = person.subscriptions
                    .filter { it.subscriptionId in ownedIds }
                    .map { it.copy(isPaused = it.subscriptionId in pausedIds) }
                if (mine.isEmpty()) return@mapNotNull null
                val counting = mine.filter { !it.isPaused }
                person.copy(
                    subscriptions = mine,
                    totalDebt = counting.filter { it.status.isOverdue }.sumOf { it.shareAmount },
                    totalMonthlyContribution = counting.filter { !it.isArchived }.sumOf { it.shareAmount }
                )
            }
            .sortedWith(compareBy<PersonSummary> { it.isAllPaused }.thenByDescending { it.totalDebt })
    }
}
