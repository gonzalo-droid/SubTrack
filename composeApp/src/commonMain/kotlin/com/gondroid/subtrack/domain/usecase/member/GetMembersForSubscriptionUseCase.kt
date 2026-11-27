package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow

class GetMembersForSubscriptionUseCase(
    private val repository: MemberRepository
) {
    operator fun invoke(subscriptionId: String): Flow<List<Member>> =
        repository.getMembersForSubscription(subscriptionId)
}
