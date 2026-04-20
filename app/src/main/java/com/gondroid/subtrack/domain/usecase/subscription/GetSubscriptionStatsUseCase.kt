package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.SubscriptionStats
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSubscriptionStatsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<SubscriptionStats> =
        combine(
            subscriptionRepository.getAllSubscriptions(),
            userRepository.getCurrentUser()
        ) { subs, user ->
            val personal = subs.filter { !it.isShared }
            val shared = subs.filter { it.isShared }
            val monthly = subs.sumOf { sub ->
                when (sub.cycle) {
                    BillingCycle.YEARLY -> sub.totalAmount / 12.0
                    else -> sub.totalAmount
                }
            }
            val pendingMembers = shared
                .filter { it.ownerId == user?.id }
                .flatMap { it.members }
                .count {
                    it.currentStatus == PaymentStatus.PENDING ||
                    it.currentStatus == PaymentStatus.OVERDUE
                }
            SubscriptionStats(
                totalCount = subs.size,
                personalCount = personal.size,
                sharedCount = shared.size,
                totalMonthlyAmount = monthly,
                pendingMembersCount = pendingMembers
            )
        }
}
