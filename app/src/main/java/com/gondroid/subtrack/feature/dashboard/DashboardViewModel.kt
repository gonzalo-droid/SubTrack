package com.gondroid.subtrack.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.usecase.subscription.GetDebtorsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetInsightsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetUpcomingCutoffsUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
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
                    dayLabel = buildDayLabel(),
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

    private fun buildDayLabel(): String {
        val cal = Calendar.getInstance()
        val day = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> "Domingo"
        }
        val month = when (cal.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "Enero"
            Calendar.FEBRUARY -> "Febrero"
            Calendar.MARCH -> "Marzo"
            Calendar.APRIL -> "Abril"
            Calendar.MAY -> "Mayo"
            Calendar.JUNE -> "Junio"
            Calendar.JULY -> "Julio"
            Calendar.AUGUST -> "Agosto"
            Calendar.SEPTEMBER -> "Septiembre"
            Calendar.OCTOBER -> "Octubre"
            Calendar.NOVEMBER -> "Noviembre"
            else -> "Diciembre"
        }
        return "$day · $month"
    }
}
