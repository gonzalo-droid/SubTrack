package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.util.daysUntilCutoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionRepositoryImpl : SubscriptionRepository {

    private val store = AppStore.subscriptions

    override fun getAllSubscriptions(): Flow<List<Subscription>> = store

    override fun getPersonalSubscriptions(): Flow<List<Subscription>> =
        store.map { list -> list.filter { !it.isShared } }

    override fun getSharedSubscriptions(): Flow<List<Subscription>> =
        store.map { list -> list.filter { it.isShared } }

    override fun getUpcomingSubscriptions(daysAhead: Int): Flow<List<Subscription>> =
        store.map { list -> list.filter { daysUntilCutoff(it.cutoffDay) in 0..daysAhead } }

    override suspend fun getSubscriptionById(id: String): Subscription? {
        return store.value.find { it.id == id }
    }

    override suspend fun createSubscription(subscription: Subscription): Result<String> {
        store.value = store.value + subscription
        return Result.success(subscription.id)
    }

    override suspend fun updateSubscription(subscription: Subscription): Result<Unit> {
        store.value = store.value.map { if (it.id == subscription.id) subscription else it }
        return Result.success(Unit)
    }

    override suspend fun deleteSubscription(id: String): Result<Unit> {
        store.value = store.value.filter { it.id != id }
        return Result.success(Unit)
    }

    override fun observeSubscription(id: String): Flow<Subscription?> =
        store.map { list -> list.find { it.id == id } }
}
