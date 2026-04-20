package com.gondroid.subtrack.feature.profile.referral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockReferralRepository
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

class ReferralViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = MockReferralRepository()
        return ReferralViewModel(
            getReferralInfo = GetReferralInfoUseCase(repo),
            getRecentReferrals = GetRecentReferralsUseCase(repo)
        ) as T
    }
}
