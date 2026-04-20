package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getAllSubscriptions(): Flow<List<Subscription>>
    fun getPersonalSubscriptions(): Flow<List<Subscription>>
    fun getSharedSubscriptions(): Flow<List<Subscription>>
    fun getUpcomingSubscriptions(daysAhead: Int = 7): Flow<List<Subscription>>
    suspend fun getSubscriptionById(id: String): Subscription?
    suspend fun createSubscription(subscription: Subscription): Result<String>
    suspend fun updateSubscription(subscription: Subscription): Result<Unit>
    suspend fun deleteSubscription(id: String): Result<Unit>
    fun observeSubscription(id: String): Flow<Subscription?>
}
