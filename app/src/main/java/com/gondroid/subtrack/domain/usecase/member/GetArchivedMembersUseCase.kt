package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArchivedMembersUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    operator fun invoke(subscriptionId: String): Flow<List<Member>> =
        repository.getArchivedMembersForSubscription(subscriptionId)
}
