package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.SubscriptionsSummary
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<SubscriptionsSummary> =
        combine(
            repository.getAllSubscriptions(),
            repository.getUpcomingSubscriptions()
        ) { all, upcoming ->
            SubscriptionsSummary(
                totalMonthlySpend = all.sumOf { it.totalAmount },
                personalSubscriptions = all.filter { !it.isShared },
                sharedSubscriptions = all.filter { it.isShared },
                upcomingCutoffs = upcoming
            )
        }
}
