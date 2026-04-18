package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.repository.MemberRepository
import javax.inject.Inject

class RequestExitUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(subscriptionId: String, memberId: String) =
        repository.requestExit(subscriptionId, memberId)
}
