package com.gondroid.subtrack.feature.subscriptionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.data.mock.MockUserRepository
import com.gondroid.subtrack.domain.usecase.subscription.GetAllSubscriptionsAlphabeticalUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionStatsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.SearchSubscriptionsUseCase

// TODO: [hilt] Remove when @HiltViewModel injection is active (AGP / Hilt plugin fixed)
class SubscriptionListViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val subRepo = MockSubscriptionRepository()
        val userRepo = MockUserRepository()
        return SubscriptionListViewModel(
            getAllSubscriptionsAlphabetical = GetAllSubscriptionsAlphabeticalUseCase(subRepo),
            searchSubscriptions = SearchSubscriptionsUseCase(),
            getSubscriptionStats = GetSubscriptionStatsUseCase(subRepo, userRepo)
        ) as T
    }
}
