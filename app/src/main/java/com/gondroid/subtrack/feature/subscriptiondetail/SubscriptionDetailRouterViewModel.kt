package com.gondroid.subtrack.feature.subscriptiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockDataStore
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionDetailUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

sealed interface RoleState {
    data object Loading : RoleState
    data class Admin(val subscriptionId: String) : RoleState
    data class MemberOf(val subscriptionId: String, val memberId: String) : RoleState
    data object NotFound : RoleState
}

@HiltViewModel(assistedFactory = SubscriptionDetailRouterViewModel.Factory::class)
class SubscriptionDetailRouterViewModel @AssistedInject constructor(
    @Assisted private val subscriptionId: String,
    getSubscriptionDetail: GetSubscriptionDetailUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(subscriptionId: String): SubscriptionDetailRouterViewModel
    }


    val roleState = combine(
        getSubscriptionDetail(subscriptionId),
        MockDataStore.currentUser
    ) { sub, user ->
        when {
            sub == null || user == null -> RoleState.NotFound
            sub.ownerId == user.id -> RoleState.Admin(subscriptionId)
            else -> {
                val member = sub.members.find { it.userId == user.id }
                if (member != null) RoleState.MemberOf(subscriptionId, member.id)
                else RoleState.NotFound
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RoleState.Loading
    )
}
