package com.gondroid.subtrack.feature.subscriptiondetail

import kotlinx.coroutines.flow.StateFlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionDetailUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface SubscriptionDetailUiState {
    data object Loading : SubscriptionDetailUiState
    data class Admin(val subscriptionId: String) : SubscriptionDetailUiState
    data class MemberOf(val subscriptionId: String, val memberId: String) : SubscriptionDetailUiState
    data object NotFound : SubscriptionDetailUiState
}

class SubscriptionDetailRouterViewModel(
    private val subscriptionId: String,
    getSubscriptionDetail: GetSubscriptionDetailUseCase,
    getCurrentUser: GetCurrentUserUseCase
) : ViewModel() {


    val uiState: StateFlow<SubscriptionDetailUiState> = combine(
        getSubscriptionDetail(subscriptionId),
        getCurrentUser()
    ) { sub, user ->
        when {
            sub == null || user == null -> SubscriptionDetailUiState.NotFound
            sub.ownerId == user.id -> SubscriptionDetailUiState.Admin(subscriptionId)
            else -> {
                val member = sub.members.find { it.userId == user.id }
                if (member != null) SubscriptionDetailUiState.MemberOf(subscriptionId, member.id)
                else SubscriptionDetailUiState.NotFound
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubscriptionDetailUiState.Loading
    )
}
