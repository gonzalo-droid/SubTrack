package com.gondroid.subtrack.domain.usecase.member

import javax.inject.Inject

class RejectExitRequestUseCase @Inject constructor() {
    // TODO: [fase-2] Connect to backend when auth + notifications are implemented
    suspend operator fun invoke(requestId: String, reason: String? = null): Result<Unit> =
        Result.success(Unit)
}
