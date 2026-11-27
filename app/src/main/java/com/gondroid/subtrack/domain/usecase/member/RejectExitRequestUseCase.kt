package com.gondroid.subtrack.domain.usecase.member


class RejectExitRequestUseCase() {
    // TODO: [fase-2] Connect to backend when auth + notifications are implemented
    suspend operator fun invoke(requestId: String, reason: String? = null): Result<Unit> =
        Result.success(Unit)
}
