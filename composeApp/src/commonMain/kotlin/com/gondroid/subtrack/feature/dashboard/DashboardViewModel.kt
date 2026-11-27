package com.gondroid.subtrack.feature.dashboard

import com.gondroid.subtrack.domain.util.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.usecase.subscription.GetDebtorsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetInsightsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetUpcomingCutoffsUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val getUpcomingCutoffsUseCase: GetUpcomingCutoffsUseCase,
    private val getDebtorsUseCase: GetDebtorsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getInsightsUseCase: GetInsightsUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        getCurrentUserUseCase(),
        getSubscriptionsUseCase(),
        getDebtorsUseCase(),
        getInsightsUseCase()
    ) { user, summary, debtors, insight ->
        if (user == null) return@combine DashboardUiState.Empty
        val allSubs = summary.personalSubscriptions + summary.sharedSubscriptions
        if (allSubs.isEmpty()) {
            DashboardUiState.Empty
        } else {
            DashboardUiState.Success(
                DashboardData(
                    user = user,
                    today = today(),
                    totalMonthlySpend = summary.totalMonthlySpend,
                    personalAmount = summary.personalSubscriptions.sumOf { it.totalAmount },
                    sharedAmount = summary.sharedSubscriptions.sumOf { it.totalAmount },
                    personalCount = summary.personalSubscriptions.size,
                    sharedCount = summary.sharedSubscriptions.size,
                    debtorCount = debtors.size,
                    upcomingCutoffs = summary.upcomingCutoffs,
                    personalSubscriptions = summary.personalSubscriptions,
                    sharedSubscriptions = summary.sharedSubscriptions,
                    debtors = debtors,
                    insight = insight
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState.Loading
    )

    fun onReminderSent(memberId: String, subscriptionId: String) {
        // TODO: [fase-2] registrar evento en Firebase Analytics
    }

}
