package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.repository.SubscriptionRepository

class DeleteSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(id: String) =
        repository.deleteSubscription(id)
}
