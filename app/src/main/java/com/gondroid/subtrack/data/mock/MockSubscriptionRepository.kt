package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockSubscriptionRepository @Inject constructor() : SubscriptionRepository {

    private val subscriptions = MutableStateFlow(MockData.subscriptions)

    override fun getSubscriptions(userId: String): Flow<List<Subscription>> =
        subscriptions.map { list -> list.filter { it.ownerId == userId } }

    override suspend fun getSubscriptionById(id: String): Subscription? =
        subscriptions.value.find { it.id == id }

    override suspend fun createSubscription(subscription: Subscription) {
        subscriptions.value = subscriptions.value + subscription
    }

    override suspend fun deleteSubscription(id: String) {
        subscriptions.value = subscriptions.value.filter { it.id != id }
    }
}
