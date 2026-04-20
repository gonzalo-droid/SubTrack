package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.ExitRequest
import com.gondroid.subtrack.domain.model.ExitRequestStatus
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.SubscriptionParticipation
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.repository.MemberRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMemberRepository @Inject constructor() : MemberRepository {

    private val store = MockDataStore.subscriptions

    override fun getMembersForSubscription(subscriptionId: String): Flow<List<Member>> =
        store.map { list ->
            list.find { it.id == subscriptionId }?.members ?: emptyList()
        }

    override fun getArchivedMembersForSubscription(subscriptionId: String): Flow<List<Member>> =
        store.map { list ->
            list.find { it.id == subscriptionId }?.archivedMembers ?: emptyList()
        }

    override suspend fun addMember(subscriptionId: String, member: Member): Result<String> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value.map { sub ->
            if (sub.id == subscriptionId) sub.copy(members = sub.members + member) else sub
        }
        return Result.success(member.id)
    }

    override suspend fun updateMember(member: Member): Result<Unit> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value.map { sub ->
            val inActive = sub.members.any { it.id == member.id }
            val inArchived = sub.archivedMembers.any { it.id == member.id }
            when {
                inActive -> sub.copy(members = sub.members.map { if (it.id == member.id) member else it })
                inArchived -> sub.copy(archivedMembers = sub.archivedMembers.map { if (it.id == member.id) member else it })
                else -> sub
            }
        }
        return Result.success(Unit)
    }

    override suspend fun archiveMember(memberId: String): Result<Unit> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value.map { sub ->
            val target = sub.members.find { it.id == memberId }
            if (target != null) {
                sub.copy(
                    members = sub.members.filter { it.id != memberId },
                    archivedMembers = sub.archivedMembers + target.copy(isArchived = true)
                )
            } else sub
        }
        return Result.success(Unit)
    }

    override suspend fun removeMember(memberId: String): Result<Unit> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        store.value = store.value.map { sub ->
            sub.copy(
                members = sub.members.filter { it.id != memberId },
                archivedMembers = sub.archivedMembers.filter { it.id != memberId }
            )
        }
        return Result.success(Unit)
    }

    override suspend fun requestExit(memberId: String, subscriptionId: String): Result<ExitRequest> {
        if (MockConfig.SIMULATE_NETWORK_DELAY) delay(MockConfig.NETWORK_DELAY_MS)
        val existing = MockDataStore.exitRequests.value
            .find { it.memberId == memberId && it.subscriptionId == subscriptionId && it.status == ExitRequestStatus.PENDING }
        if (existing != null) return Result.success(existing)
        val request = ExitRequest(
            id = "exit_${UUID.randomUUID()}",
            memberId = memberId,
            subscriptionId = subscriptionId,
            requestedAt = System.currentTimeMillis()
        )
        MockDataStore.exitRequests.value = MockDataStore.exitRequests.value + request
        return Result.success(request)
    }

    override fun getExitRequest(memberId: String, subscriptionId: String): Flow<ExitRequest?> =
        MockDataStore.exitRequests.map { list ->
            list.find { it.memberId == memberId && it.subscriptionId == subscriptionId && it.status == ExitRequestStatus.PENDING }
        }

    override fun getAllPeopleWithSubscriptions(): Flow<List<PersonSummary>> =
        combine(store, MockDataStore.payments) { subscriptions, payments ->
            data class MemberAccumulator(
                val member: Member,
                val participations: MutableList<SubscriptionParticipation>,
                var earliestJoin: Long,
                var latestActivity: Long?
            )

            val memberMap = mutableMapOf<String, MemberAccumulator>()

            subscriptions.forEach { sub ->
                val allMembers = sub.members.map { it to false } + sub.archivedMembers.map { it to true }
                allMembers.forEach { (member, archived) ->
                    val participation = SubscriptionParticipation(
                        subscriptionId = sub.id,
                        subscriptionName = sub.name,
                        brandColor = sub.brandColor,
                        status = member.currentStatus,
                        shareAmount = member.shareAmount,
                        isArchived = archived
                    )
                    val existing = memberMap[member.id]
                    if (existing == null) {
                        memberMap[member.id] = MemberAccumulator(
                            member = member,
                            participations = mutableListOf(participation),
                            earliestJoin = member.joinedAt,
                            latestActivity = null
                        )
                    } else {
                        existing.participations.add(participation)
                        if (member.joinedAt < existing.earliestJoin) {
                            existing.earliestJoin = member.joinedAt
                        }
                    }
                }
            }

            // Enrich with payment history
            payments.forEach { payment ->
                val paidAt = payment.paidAt ?: return@forEach
                val acc = memberMap[payment.memberId] ?: return@forEach
                if (acc.latestActivity == null || paidAt > (acc.latestActivity ?: 0L)) {
                    acc.latestActivity = paidAt
                }
            }

            memberMap.values.map { acc ->
                val member = acc.member
                val memberPayments = payments.filter { it.memberId == member.id }
                val decidedPayments = memberPayments.filter { it.status != PaymentStatus.PENDING }
                val punctual = decidedPayments.count { it.status == PaymentStatus.PAID }
                val punctualityPercent = if (decidedPayments.isEmpty()) 100
                else (punctual * 100 / decidedPayments.size)

                val totalDebt = acc.participations
                    .filter { it.status == PaymentStatus.OVERDUE || it.status == PaymentStatus.LATE }
                    .sumOf { it.shareAmount }

                PersonSummary(
                    userId = member.userId,
                    name = member.name,
                    phone = member.phone,
                    hasApp = member.userId != null,
                    totalDebt = totalDebt,
                    totalMonthlyContribution = acc.participations
                        .filter { !it.isArchived }
                        .sumOf { it.shareAmount },
                    punctualityPercent = punctualityPercent,
                    firstJoinedAt = acc.earliestJoin,
                    lastActivityAt = acc.latestActivity,
                    subscriptions = acc.participations
                )
            }.sortedByDescending { it.totalDebt }
        }
}
