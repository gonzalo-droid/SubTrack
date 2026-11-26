package com.gondroid.subtrack.feature.subscriptiondetail.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockMemberRepository
import com.gondroid.subtrack.data.mock.MockPaymentRepository
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
import javax.inject.Inject

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

@HiltViewModel
class MemberHistoryViewModel @Inject constructor(
    subscriptionId: String,
    memberId: String,
    getMembers: GetMembersForSubscriptionUseCase,
    getArchivedMembers: GetArchivedMembersUseCase,
    getHistory: GetMemberPaymentHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<MemberHistoryUiState> = combine(
        getMembers(subscriptionId),
        getArchivedMembers(subscriptionId),
        getHistory(memberId, limit = HISTORY_MONTHS)
    ) { active, archived, payments ->
        val member = (active + archived).find { it.id == memberId }
            ?: return@combine MemberHistoryUiState.NotFound
        MemberHistoryUiState.Success(member.name, payments, punctuality(payments))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MemberHistoryUiState.Loading)

    companion object {
        const val HISTORY_MONTHS = 12
    }
}

// TODO: [hilt] Remove when @HiltViewModel injection is active
class MemberHistoryViewModelFactory(
    private val subscriptionId: String,
    private val memberId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val memberRepo = MockMemberRepository()
        return MemberHistoryViewModel(
            subscriptionId = subscriptionId,
            memberId = memberId,
            getMembers = GetMembersForSubscriptionUseCase(memberRepo),
            getArchivedMembers = GetArchivedMembersUseCase(memberRepo),
            getHistory = GetMemberPaymentHistoryUseCase(MockPaymentRepository())
        ) as T
    }
}
