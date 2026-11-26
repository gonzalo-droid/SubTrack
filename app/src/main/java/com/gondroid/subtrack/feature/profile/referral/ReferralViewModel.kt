package com.gondroid.subtrack.feature.profile.referral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.usecase.referral.GetRecentReferralsUseCase
import com.gondroid.subtrack.domain.usecase.referral.GetReferralInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val getReferralInfo: GetReferralInfoUseCase,
    private val getRecentReferrals: GetRecentReferralsUseCase
) : ViewModel() {

    val uiState = combine(
        getReferralInfo(),
        getRecentReferrals(4)
    ) { info, records ->
        ReferralUiState.Success(ReferralData(info = info, recentReferrals = records))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReferralUiState.Loading)
}
