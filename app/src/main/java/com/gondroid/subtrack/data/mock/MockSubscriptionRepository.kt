package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockSubscriptionRepository @Inject constructor() : SubscriptionRepository {

    private val store = MockDataStore.subscriptions

    override fun getAllSubscriptions(): Flow<List<Subscription>> = store

    override fun getPersonalSubscriptions(): Flow<List<Subscription>> =
        store.map { list -> list.filter { !it.isShared } }

    override fun getSharedSubscriptions(): Flow<List<Subscription>> =
        store.map { list -> list.filter { it.isShared } }

    override fun getUpcomingSubscriptions(daysAhead: Int): Flow<List<Subscription>> =
        store.map { list ->
            val now = Calendar.getInstance()
            val today = now.get(Calendar.DAY_OF_MONTH)
            val currentMonth = now.get(Calendar.MONTH)
            val currentYear = now.get(Calendar.YEAR)

            list.filter { sub ->
                val cutoff = sub.cutoffDay
                val daysUntilCutoff = if (cutoff >= today) {
                    cutoff - today
                } else {
                    val daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH)
                    daysInMonth - today + cutoff
                }
                daysUntilCutoff in 0..daysAhead
            }.also {
                // suppress unused variable warning
                @Suppress("UNUSED_EXPRESSION") currentMonth
                @Suppress("UNUSED_EXPRESSION") currentYear
            }
        }

    override suspend fun getSubscriptionById(id: String): Subscription? {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        return store.value.find { it.id == id }
    }

    override suspend fun createSubscription(subscription: Subscription): Result<String> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value + subscription
        return Result.success(subscription.id)
    }

    override suspend fun updateSubscription(subscription: Subscription): Result<Unit> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value.map { if (it.id == subscription.id) subscription else it }
        return Result.success(Unit)
    }

    override suspend fun deleteSubscription(id: String): Result<Unit> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value.filter { it.id != id }
        return Result.success(Unit)
    }

    override fun observeSubscription(id: String): Flow<Subscription?> =
        store.map { list -> list.find { it.id == id } }
}
