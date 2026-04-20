package com.gondroid.subtrack.domain.usecase.member

import com.gondroid.subtrack.domain.model.enums.DebtDecision
import javax.inject.Inject

class ApproveExitRequestUseCase @Inject constructor() {
    // TODO: [fase-2] Connect to backend when auth + notifications are implemented
    suspend operator fun invoke(requestId: String, debtDecision: DebtDecision): Result<Unit> =
        Result.success(Unit)
}
