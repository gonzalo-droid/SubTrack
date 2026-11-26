package com.gondroid.subtrack.feature.subscriptiondetail.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.usecase.member.GetArchivedMembersUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMemberPaymentHistoryUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMembersForSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface MemberHistoryUiState {
    data object Loading : MemberHistoryUiState
    data class Success(
        val memberName: String,
        val payments: List<Payment>,
        /** Share of decided months paid on time; null without decided months. */
        val punctualityPercent: Int?
    ) : MemberHistoryUiState
    data object NotFound : MemberHistoryUiState
}

/** Months a member has paid: PAID counts as on time, LATE/OVERDUE don't, PENDING isn't decided yet. */
internal fun punctuality(payments: List<Payment>): Int? {
    val decided = payments.filter { it.status != PaymentStatus.PENDING }
    if (decided.isEmpty()) return null
    return decided.count { it.status == PaymentStatus.PAID } * 100 / decided.size
}

@HiltViewModel(assistedFactory = MemberHistoryViewModel.Factory::class)
class MemberHistoryViewModel @AssistedInject constructor(
    @Assisted("subscriptionId") subscriptionId: String,
    @Assisted("memberId") memberId: String,
    getMembers: GetMembersForSubscriptionUseCase,
    getArchivedMembers: GetArchivedMembersUseCase,
    getHistory: GetMemberPaymentHistoryUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("subscriptionId") subscriptionId: String,
            @Assisted("memberId") memberId: String
        ): MemberHistoryViewModel
    }


    val uiState: StateFlow<MemberHistoryUiState> = combine(
        getMembers(subscriptionId),
        getArchivedMembers(subscriptionId),
        getHistory(memberId, limit = HISTORY_MONTHS)
    ) { active, archived, payments ->
        val member = (active + archived).find { it.id == memberId }
            ?: return@combine MemberHistoryUiState.NotFound
        // Member ids are only unique within a subscription: keep this subscription's payments
        val own = payments.filter { it.subscriptionId == subscriptionId }
        MemberHistoryUiState.Success(member.name, own, punctuality(own))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MemberHistoryUiState.Loading)

    companion object {
        const val HISTORY_MONTHS = 12
    }
}
