package com.gondroid.subtrack.domain.usecase.referral

import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.repository.ReferralRepository
import kotlinx.coroutines.flow.Flow

class GetRecentReferralsUseCase(
    private val repository: ReferralRepository
) {
    operator fun invoke(limit: Int = 4): Flow<List<ReferralRecord>> =
        repository.getRecentReferrals(limit)
}
