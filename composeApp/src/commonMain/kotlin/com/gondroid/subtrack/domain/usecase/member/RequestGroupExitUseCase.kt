package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.ExitRequest
import com.gondroid.subtrack.domain.repository.MemberRepository

class RequestGroupExitUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(memberId: String, subscriptionId: String): Result<ExitRequest> =
        repository.requestExit(memberId, subscriptionId)
}
