package com.gondroid.subtrack.domain.usecase.referral

import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.repository.ReferralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReferralInfoUseCase @Inject constructor(
    private val repository: ReferralRepository
) {
    operator fun invoke(): Flow<ReferralInfo> = repository.getReferralInfo()
}
