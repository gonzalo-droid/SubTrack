package com.gondroid.subtrack.testing

import com.gondroid.subtrack.domain.model.ExitRequest
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

/** Repositories a test passes but must never call; delegate to them (`by UnusedMembers`) and override what it uses. */
object UnusedSubscriptions : SubscriptionRepository {
    override fun getAllSubscriptions(): Flow<List<Subscription>> = unused()
    override fun getPersonalSubscriptions(): Flow<List<Subscription>> = unused()
    override fun getSharedSubscriptions(): Flow<List<Subscription>> = unused()
    override fun getUpcomingSubscriptions(daysAhead: Int): Flow<List<Subscription>> = unused()
    override suspend fun getSubscriptionById(id: String): Subscription? = unused()
    override suspend fun createSubscription(subscription: Subscription): Result<String> = unused()
    override suspend fun updateSubscription(subscription: Subscription): Result<Unit> = unused()
    override suspend fun deleteSubscription(id: String): Result<Unit> = unused()
    override fun observeSubscription(id: String): Flow<Subscription?> = unused()
}

object UnusedMembers : MemberRepository {
    override fun getMembersForSubscription(subscriptionId: String): Flow<List<Member>> = unused()
    override fun getArchivedMembersForSubscription(subscriptionId: String): Flow<List<Member>> = unused()
    override suspend fun addMember(subscriptionId: String, member: Member): Result<String> = unused()
    override suspend fun updateMember(member: Member): Result<Unit> = unused()
    override suspend fun archiveMember(memberId: String): Result<Unit> = unused()
    override suspend fun removeMember(memberId: String): Result<Unit> = unused()
    override fun getAllPeopleWithSubscriptions(): Flow<List<PersonSummary>> = unused()
    override suspend fun requestExit(memberId: String, subscriptionId: String): Result<ExitRequest> = unused()
    override fun getExitRequest(memberId: String, subscriptionId: String): Flow<ExitRequest?> = unused()
}

private fun unused(): Nothing = error("unused in this test")
