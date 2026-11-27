package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository

class CreateSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription) =
        repository.createSubscription(subscription)
}
