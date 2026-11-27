package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

class GetUpcomingCutoffsUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(daysAhead: Int = 7): Flow<List<Subscription>> =
        repository.getUpcomingSubscriptions(daysAhead)
}
