package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.repository.MemberRepository

class RemoveMemberUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(memberId: String) =
        repository.removeMember(memberId)
}
