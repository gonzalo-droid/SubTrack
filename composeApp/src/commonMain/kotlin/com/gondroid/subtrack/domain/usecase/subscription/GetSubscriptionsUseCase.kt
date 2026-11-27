package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.SubscriptionsSummary
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSubscriptionsUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<SubscriptionsSummary> =
        combine(
            repository.getAllSubscriptions(),
            repository.getUpcomingSubscriptions()
        ) { all, upcoming ->
            val active = all.filterNot { it.isPaused }
            SubscriptionsSummary(
                totalMonthlySpend = active.sumOf { it.totalAmount },
                personalSubscriptions = active.filter { !it.isShared },
                sharedSubscriptions = active.filter { it.isShared },
                upcomingCutoffs = upcoming.filterNot { it.isPaused }
            )
        }
}
