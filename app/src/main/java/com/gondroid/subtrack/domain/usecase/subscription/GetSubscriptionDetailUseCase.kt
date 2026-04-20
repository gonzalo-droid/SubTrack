package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscriptionDetailUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(id: String): Flow<Subscription?> = repository.observeSubscription(id)
}
