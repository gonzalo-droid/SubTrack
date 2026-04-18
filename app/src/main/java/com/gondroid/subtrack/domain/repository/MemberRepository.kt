package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    fun getMembers(subscriptionId: String): Flow<List<Member>>
    suspend fun addMember(subscriptionId: String, member: Member)
    suspend fun updateMember(subscriptionId: String, member: Member)
    suspend fun archiveMember(subscriptionId: String, memberId: String)
    suspend fun requestExit(subscriptionId: String, memberId: String)
}
