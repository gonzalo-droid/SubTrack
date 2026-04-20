package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.ExitRequest
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.PersonSummary
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    fun getMembersForSubscription(subscriptionId: String): Flow<List<Member>>
    fun getArchivedMembersForSubscription(subscriptionId: String): Flow<List<Member>>
    suspend fun addMember(subscriptionId: String, member: Member): Result<String>
    suspend fun updateMember(member: Member): Result<Unit>
    suspend fun archiveMember(memberId: String): Result<Unit>
    suspend fun removeMember(memberId: String): Result<Unit>
    fun getAllPeopleWithSubscriptions(): Flow<List<PersonSummary>>
    suspend fun requestExit(memberId: String, subscriptionId: String): Result<ExitRequest>
    fun getExitRequest(memberId: String, subscriptionId: String): Flow<ExitRequest?>
}
