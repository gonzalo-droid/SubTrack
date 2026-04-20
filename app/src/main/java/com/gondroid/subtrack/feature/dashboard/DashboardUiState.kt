package com.gondroid.subtrack.feature.dashboard

import com.gondroid.subtrack.domain.model.DebtorInfo
import com.gondroid.subtrack.domain.model.Insight
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.User

data class DashboardData(
    val user: User,
    val dayLabel: String,
    val totalMonthlySpend: Double,
    val personalAmount: Double,
    val sharedAmount: Double,
    val personalCount: Int,
    val sharedCount: Int,
    val debtorCount: Int,
    val upcomingCutoffs: List<Subscription>,
    val personalSubscriptions: List<Subscription>,
    val sharedSubscriptions: List<Subscription>,
    val debtors: List<DebtorInfo>,
    val insight: Insight?
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val data: DashboardData) : DashboardUiState
    data object Empty : DashboardUiState
}
