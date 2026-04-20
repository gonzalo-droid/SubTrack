package com.gondroid.subtrack.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.data.mock.MockUserRepository
import com.gondroid.subtrack.domain.usecase.subscription.GetDebtorsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetInsightsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetUpcomingCutoffsUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase

// TODO: [hilt] Remove when @HiltViewModel injection is active (AGP / Hilt plugin fixed)
class DashboardViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val subRepo = MockSubscriptionRepository()
        val userRepo = MockUserRepository()
        return DashboardViewModel(
            getSubscriptionsUseCase = GetSubscriptionsUseCase(subRepo),
            getUpcomingCutoffsUseCase = GetUpcomingCutoffsUseCase(subRepo),
            getDebtorsUseCase = GetDebtorsUseCase(subRepo),
            getCurrentUserUseCase = GetCurrentUserUseCase(userRepo),
            getInsightsUseCase = GetInsightsUseCase(subRepo)
        ) as T
    }
}
