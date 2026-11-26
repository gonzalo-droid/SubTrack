package com.gondroid.subtrack.domain.usecase.subscription

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.model.enums.SplitType
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionUseCasesTest {

    private class FakeRepo(initial: List<Subscription>) : SubscriptionRepository {
        val store = MutableStateFlow(initial)
        override fun getAllSubscriptions(): Flow<List<Subscription>> = store
        override fun getPersonalSubscriptions() = store.map { l -> l.filter { !it.isShared } }
        override fun getSharedSubscriptions() = store.map { l -> l.filter { it.isShared } }
        override fun getUpcomingSubscriptions(daysAhead: Int): Flow<List<Subscription>> = store
        override suspend fun getSubscriptionById(id: String) = store.value.find { it.id == id }
        override suspend fun createSubscription(subscription: Subscription) = Result.success(subscription.id)
        override suspend fun updateSubscription(subscription: Subscription): Result<Unit> {
            store.value = store.value.map { if (it.id == subscription.id) subscription else it }
            return Result.success(Unit)
        }
        override suspend fun deleteSubscription(id: String) = Result.success(Unit)
        override fun observeSubscription(id: String) = store.map { l -> l.find { it.id == id } }
    }

    private fun member(id: String, share: Double) =
        Member(id, null, id, "999", null, share, currentStatus = PaymentStatus.PENDING, joinedAt = 0)

    private fun sub(
        id: String,
        total: Double,
        shared: Boolean = false,
        split: SplitType = SplitType.EQUAL,
        members: List<Member> = emptyList(),
        paused: Boolean = false
    ) = Subscription(
        id = id, name = id, brandColor = "#000000", totalAmount = total, cycle = BillingCycle.MONTHLY,
        cutoffDay = 5, ownerId = "me", isShared = shared, splitType = split, category = SubscriptionCategory.OTHER,
        members = members, isPaused = paused, createdAt = 0, updatedAt = 0
    )

    @Test fun `paused subscriptions are left out of totals and upcoming`() = runBlocking {
        val repo = FakeRepo(listOf(sub("a", 10.0), sub("b", 30.0, paused = true), sub("c", 20.0, shared = true)))
        val summary = GetSubscriptionsUseCase(repo)().first()
        assertEquals(30.0, summary.totalMonthlySpend, 0.001)
        assertEquals(listOf("a"), summary.personalSubscriptions.map { it.id })
        assertTrue(summary.upcomingCutoffs.none { it.isPaused })
    }

    @Test fun `pause and resume toggle the flag`() = runBlocking {
        val repo = FakeRepo(listOf(sub("a", 10.0)))
        PauseSubscriptionUseCase(repo)("a", paused = true)
        assertTrue(repo.store.value.single().isPaused)
        PauseSubscriptionUseCase(repo)("a", paused = false)
        assertEquals(false, repo.store.value.single().isPaused)
    }

    @Test fun `equal split shares follow the new total`() = runBlocking {
        val repo = FakeRepo(listOf(sub("s", 30.0, shared = true, members = listOf(member("m1", 10.0), member("m2", 10.0)))))
        UpdateSubscriptionDetailsUseCase(repo)("s", 45.0, "PEN", BillingCycle.MONTHLY, 10)
        val updated = repo.store.value.single()
        assertEquals(listOf(15.0, 15.0), updated.members.map { it.shareAmount })
        assertEquals(10, updated.cutoffDay)
    }

    @Test fun `percentage shares scale and fixed shares stay`() = runBlocking {
        val repo = FakeRepo(listOf(
            sub("p", 100.0, shared = true, split = SplitType.PERCENTAGE, members = listOf(member("m", 40.0))),
            sub("f", 100.0, shared = true, split = SplitType.FIXED, members = listOf(member("m", 25.0)))
        ))
        val update = UpdateSubscriptionDetailsUseCase(repo)
        update("p", 50.0, "PEN", BillingCycle.MONTHLY, 5)
        update("f", 50.0, "PEN", BillingCycle.MONTHLY, 5)
        assertEquals(20.0, repo.store.value[0].members.single().shareAmount, 0.001)
        assertEquals(25.0, repo.store.value[1].members.single().shareAmount, 0.001)
    }
}
