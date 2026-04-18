package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMemberRepository @Inject constructor() : MemberRepository {

    private val subscriptions = MutableStateFlow(MockData.subscriptions)

    override fun getMembers(subscriptionId: String): Flow<List<Member>> =
        subscriptions.map { list ->
            list.find { it.id == subscriptionId }?.members ?: emptyList()
        }

    override suspend fun addMember(subscriptionId: String, member: Member) {
        subscriptions.value = subscriptions.value.map { sub ->
            if (sub.id == subscriptionId) sub.copy(members = sub.members + member) else sub
        }
    }

    override suspend fun updateMember(subscriptionId: String, member: Member) {
        subscriptions.value = subscriptions.value.map { sub ->
            if (sub.id == subscriptionId) {
                sub.copy(members = sub.members.map { if (it.id == member.id) member else it })
            } else sub
        }
    }

    override suspend fun archiveMember(subscriptionId: String, memberId: String) {
        subscriptions.value = subscriptions.value.map { sub ->
            if (sub.id == subscriptionId) {
                val target = sub.members.find { it.id == memberId }
                if (target != null) {
                    sub.copy(
                        members = sub.members.filter { it.id != memberId },
                        archivedMembers = sub.archivedMembers + target.copy(isArchived = true)
                    )
                } else sub
            } else sub
        }
    }

    override suspend fun requestExit(subscriptionId: String, memberId: String) {
        // TODO: [fase 2] notificar al admin vía Firebase
    }
}
