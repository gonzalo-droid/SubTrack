package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.repository.MemberRepository
import javax.inject.Inject

// TODO: [fase-2] implement member exit request via Firebase notification
class RequestExitUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(memberId: String): Result<Unit> =
        Result.success(Unit)
}
