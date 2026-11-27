package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.repository.MemberRepository

// TODO: [fase-2] implement member exit request via Firebase notification
class RequestExitUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(memberId: String): Result<Unit> =
        Result.success(Unit)
}
