package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import javax.inject.Inject

class DeleteSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(id: String) =
        repository.deleteSubscription(id)
}
