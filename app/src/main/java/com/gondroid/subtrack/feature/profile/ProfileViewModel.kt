package com.gondroid.subtrack.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockReferralRepository
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.data.mock.MockTemplateRepository
import com.gondroid.subtrack.data.mock.MockUserAliasRepository
import com.gondroid.subtrack.data.mock.MockUserRepository
import com.gondroid.subtrack.domain.usecase.alias.GetUserAliasUseCase
import com.gondroid.subtrack.domain.usecase.referral.GetReferralInfoUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.template.GetAllTemplatesUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getUserAlias: GetUserAliasUseCase,
    private val getSubscriptions: GetSubscriptionsUseCase,
    private val getTemplates: GetAllTemplatesUseCase,
    private val getReferralInfo: GetReferralInfoUseCase
) : ViewModel() {

    val uiState = combine(
        getCurrentUser(),
        getUserAlias(),
        getSubscriptions(),
        getTemplates(),
        getReferralInfo()
    ) { user, alias, subs, templates, referral ->
        if (user == null) return@combine ProfileUiState.Loading
        val recovered = subs.sharedSubscriptions.sumOf { sub ->
            sub.members.sumOf { it.shareAmount }
        }
        ProfileUiState.Success(
            ProfileData(
                user = user,
                isPro = user.isPro,
                userAlias = alias,
                defaultAliasMethod = alias.defaultMethod,
                totalServices = subs.personalSubscriptions.size + subs.sharedSubscriptions.size,
                totalMonthlySpend = subs.totalMonthlySpend,
                totalMonthlyRecovered = recovered,
                templates = templates,
                activeReferrals = referral.activeReferrals,
                monthsProEarned = referral.monthsProEarned
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState.Loading)
}

class ProfileViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            getCurrentUser = GetCurrentUserUseCase(MockUserRepository()),
            getUserAlias = GetUserAliasUseCase(MockUserAliasRepository()),
            getSubscriptions = GetSubscriptionsUseCase(MockSubscriptionRepository()),
            getTemplates = GetAllTemplatesUseCase(MockTemplateRepository()),
            getReferralInfo = GetReferralInfoUseCase(MockReferralRepository())
        ) as T
    }
}
