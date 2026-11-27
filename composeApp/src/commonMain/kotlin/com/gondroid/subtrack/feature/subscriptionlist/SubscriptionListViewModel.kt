package com.gondroid.subtrack.feature.subscriptionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.SubscriptionStats
import com.gondroid.subtrack.domain.usecase.subscription.GetAllSubscriptionsAlphabeticalUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionStatsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.SearchSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.SubscriptionFilter
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

sealed interface SubscriptionListUiState {
    data object Loading : SubscriptionListUiState
    data class Success(
        val stats: SubscriptionStats,
        val subscriptions: List<Subscription>,
        val searchQuery: String,
        val selectedFilter: SubscriptionFilter,
        val currentUserId: String?
    ) : SubscriptionListUiState
}

@OptIn(FlowPreview::class)
class SubscriptionListViewModel(
    private val getAllSubscriptionsAlphabetical: GetAllSubscriptionsAlphabeticalUseCase,
    private val searchSubscriptions: SearchSubscriptionsUseCase,
    private val getSubscriptionStats: GetSubscriptionStatsUseCase,
    getCurrentUser: GetCurrentUserUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(SubscriptionFilter.ALL)

    val uiState: StateFlow<SubscriptionListUiState> = combine(
        getAllSubscriptionsAlphabetical(),
        searchQuery.debounce(250),
        selectedFilter,
        getSubscriptionStats(),
        getCurrentUser()
    ) { allSubs, query, filter, stats, user ->
        SubscriptionListUiState.Success(
            stats = stats,
            subscriptions = searchSubscriptions(allSubs, query, filter),
            searchQuery = query,
            selectedFilter = filter,
            currentUserId = user?.id
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubscriptionListUiState.Loading
    )

    fun updateSearchQuery(query: String) = searchQuery.update { query }

    fun selectFilter(filter: SubscriptionFilter) = selectedFilter.update { filter }
}
