package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.ExitRequest
import com.gondroid.subtrack.domain.repository.MemberRepository
import javax.inject.Inject

class RequestGroupExitUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(memberId: String, subscriptionId: String): Result<ExitRequest> =
        repository.requestExit(memberId, subscriptionId)
}
