package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.SubscriptionParticipation
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import com.gondroid.subtrack.domain.usecase.subscription.GetDebtorsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Proxy

class PeopleUseCasesTest {

    private val me = User(id = "me", name = "Gonzalo Meza", phone = "900", email = null, referralCode = "G", createdAt = 0)
    private val maria = User(id = "maria", name = "María Rodríguez", phone = "911", email = null, referralCode = "M", createdAt = 0)

    private class FakeSubs(list: List<Subscription>) : SubscriptionRepository {
        val store = MutableStateFlow(list)
        override fun getAllSubscriptions(): Flow<List<Subscription>> = store
        override fun getPersonalSubscriptions() = store.map { l -> l.filter { !it.isShared } }
        override fun getSharedSubscriptions() = store.map { l -> l.filter { it.isShared } }
        override fun getUpcomingSubscriptions(daysAhead: Int): Flow<List<Subscription>> = store
        override suspend fun getSubscriptionById(id: String) = store.value.find { it.id == id }
        override suspend fun createSubscription(subscription: Subscription) = Result.success(subscription.id)
        override suspend fun updateSubscription(subscription: Subscription) = Result.success(Unit)
        override suspend fun deleteSubscription(id: String) = Result.success(Unit)
        override fun observeSubscription(id: String) = store.map { l -> l.find { it.id == id } }
    }

    private inner class FakeUsers : UserRepository {
        override fun getCurrentUser(): Flow<User?> = flowOf(me)
        override suspend fun updateUser(user: User) = Result.success(Unit)
        override suspend fun getUserById(userId: String) = listOf(me, maria).find { it.id == userId }
    }

    private fun member(id: String, userId: String?, status: PaymentStatus, share: Double = 10.0) =
        Member(id, userId, id, "9$id", null, share, currentStatus = status, joinedAt = 0)

    private fun shared(id: String, owner: String, vararg members: Member) = Subscription(
        id = id, name = id, brandColor = "#000000", totalAmount = 30.0, cycle = BillingCycle.MONTHLY,
        cutoffDay = 1, ownerId = owner, isShared = true, category = SubscriptionCategory.OTHER,
        members = members.toList(), createdAt = 0, updatedAt = 0
    )

    private val subs = listOf(
        shared("netflix", "me", member("ana", null, PaymentStatus.OVERDUE), member("luis", null, PaymentStatus.PENDING)),
        shared("disney", "maria", member("me-in-disney", "me", PaymentStatus.LATE, share = 8.0), member("ana", null, PaymentStatus.OVERDUE))
    )

    @Test fun `debtors only come from subscriptions I administer and count overdue or late`() = runBlocking {
        val debtors = GetDebtorsUseCase(FakeSubs(subs), FakeUsers())().first()
        assertEquals(listOf("ana" to "netflix"), debtors.map { it.memberName to it.subscriptionId })
    }

    @Test fun `my debts group by admin with the overdue amount`() = runBlocking {
        val debts = GetMyDebtsUseCase(FakeSubs(subs), FakeUsers())().first()
        val owner = debts.single()
        assertEquals("María Rodríguez", owner.name)
        assertEquals(8.0, owner.totalDebt, 0.001)
        assertEquals(listOf("disney"), owner.subscriptions.map { it.subscriptionId })
    }

    @Test fun `paused subscriptions create no debts`() = runBlocking {
        val paused = subs.map { it.copy(isPaused = true) }
        assertEquals(emptyList<Any>(), GetDebtorsUseCase(FakeSubs(paused), FakeUsers())().first())
        assertEquals(emptyList<Any>(), GetMyDebtsUseCase(FakeSubs(paused), FakeUsers())().first())
    }

    @Test fun `people who owe me exclude me and subscriptions I don't own`() = runBlocking {
        fun person(userId: String?, name: String, vararg subIds: String) = PersonSummary(
            userId = userId, name = name, phone = name, hasApp = userId != null, totalDebt = 99.0,
            totalMonthlyContribution = 99.0, punctualityPercent = 100, firstJoinedAt = 0, lastActivityAt = null,
            subscriptions = subIds.map { SubscriptionParticipation(it, it, "#000000", PaymentStatus.OVERDUE, 10.0, false) }
        )
        // Only getAllPeopleWithSubscriptions is used; every other call fails loudly.
        val unused = Proxy.newProxyInstance(javaClass.classLoader, arrayOf(MemberRepository::class.java)) { _, _, _ ->
            error("unused")
        } as MemberRepository
        val memberRepo = object : MemberRepository by unused {
            override fun getAllPeopleWithSubscriptions() = flowOf(
                listOf(person(null, "Ana", "netflix", "disney"), person("me", "Gonzalo", "disney"), person(null, "Solo Disney", "disney"))
            )
        }
        val people = GetAllPeopleWithSubscriptionsUseCase(memberRepo, FakeSubs(subs), FakeUsers())().first()
        assertEquals(listOf("Ana"), people.map { it.name })
        assertEquals(10.0, people.single().totalDebt, 0.001)
        assertEquals(listOf("netflix"), people.single().subscriptions.map { it.subscriptionId })
    }
}
