package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

class GetSubscriptionDetailUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(id: String): Flow<Subscription?> = repository.observeSubscription(id)
}
