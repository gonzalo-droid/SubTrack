package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.util.currentTimeMillis

import com.gondroid.subtrack.domain.repository.SubscriptionRepository

class PauseSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(id: String, paused: Boolean): Result<Unit> {
        val subscription = repository.getSubscriptionById(id)
            ?: return Result.failure(NoSuchElementException("Subscription $id not found"))
        return repository.updateSubscription(
            subscription.copy(isPaused = paused, updatedAt = currentTimeMillis())
        )
    }
}
