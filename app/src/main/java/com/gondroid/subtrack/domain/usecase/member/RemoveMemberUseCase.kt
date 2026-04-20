package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.repository.MemberRepository
import javax.inject.Inject

class RemoveMemberUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(memberId: String) =
        repository.removeMember(memberId)
}
