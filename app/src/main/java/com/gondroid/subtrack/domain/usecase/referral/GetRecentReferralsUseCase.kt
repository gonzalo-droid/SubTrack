package com.gondroid.subtrack.domain.usecase.referral

import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.repository.ReferralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentReferralsUseCase @Inject constructor(
    private val repository: ReferralRepository
) {
    operator fun invoke(limit: Int = 4): Flow<List<ReferralRecord>> =
        repository.getRecentReferrals(limit)
}
