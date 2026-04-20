package com.gondroid.subtrack.feature.profile.referral

import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.model.ReferralRecord

data class ReferralData(
    val info: ReferralInfo,
    val recentReferrals: List<ReferralRecord>
)

sealed interface ReferralUiState {
    data object Loading : ReferralUiState
    data class Success(val data: ReferralData) : ReferralUiState
}
