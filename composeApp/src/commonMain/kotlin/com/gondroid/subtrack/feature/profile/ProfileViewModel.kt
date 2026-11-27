package com.gondroid.subtrack.feature.profile

import com.gondroid.subtrack.core.result.onFailure

import com.gondroid.subtrack.core.result.DataError

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.usecase.alias.GetUserAliasUseCase
import com.gondroid.subtrack.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.gondroid.subtrack.domain.usecase.referral.GetReferralInfoUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.template.GetAllTemplatesUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getUserAlias: GetUserAliasUseCase,
    private val getSubscriptions: GetSubscriptionsUseCase,
    private val getTemplates: GetAllTemplatesUseCase,
    private val getReferralInfo: GetReferralInfoUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _signOutFailed = Channel<DataError>(Channel.BUFFERED)
    /** Emits when the sign-out failed; on success the app goes back to the onboarding by itself. */
    val signOutFailed = _signOutFailed.receiveAsFlow()

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase().onFailure { _signOutFailed.send(it) }
        }
    }

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
