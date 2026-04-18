package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.repository.MemberRepository
import javax.inject.Inject

class AddMemberUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(subscriptionId: String, member: Member) =
        repository.addMember(subscriptionId, member)
}
