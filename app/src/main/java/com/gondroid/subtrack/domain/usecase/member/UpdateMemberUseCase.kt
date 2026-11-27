package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.repository.MemberRepository

class UpdateMemberUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(member: Member) =
        repository.updateMember(member)
}
