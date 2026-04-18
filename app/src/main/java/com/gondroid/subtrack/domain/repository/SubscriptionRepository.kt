package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getSubscriptions(userId: String): Flow<List<Subscription>>
    suspend fun getSubscriptionById(id: String): Subscription?
    suspend fun createSubscription(subscription: Subscription)
    suspend fun deleteSubscription(id: String)
}
