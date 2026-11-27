package com.gondroid.subtrack.feature.subscriptiondetail.member

import com.gondroid.subtrack.domain.util.monthKey

import com.gondroid.subtrack.domain.util.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.model.MemberSubscriptionView
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.usecase.member.GetMemberPaymentHistoryUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMemberSubscriptionDetailUseCase
import com.gondroid.subtrack.domain.usecase.member.MarkOwnPaymentUseCase
import com.gondroid.subtrack.domain.usecase.member.RequestGroupExitUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

sealed interface MemberDetailUiState {
    data object Loading : MemberDetailUiState
    data class Success(
        val view: MemberSubscriptionView,
        val recentPayments: List<Payment>
    ) : MemberDetailUiState
    data object NotFound : MemberDetailUiState
}

// ── Sheet state ───────────────────────────────────────────────────────────────

sealed interface MemberActionSheet {
    data object None : MemberActionSheet
    data object MarkAsPaid : MemberActionSheet
    data object RequestExit : MemberActionSheet
}

// ── Events ────────────────────────────────────────────────────────────────────

sealed interface MemberDetailEvent {
    data object PaymentMarked : MemberDetailEvent
    data object ExitRequested : MemberDetailEvent
    /** Generic failure; the UI shows common_error_generic, never the exception text. */
    data object Error : MemberDetailEvent
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class MemberDetailViewModel(
    private val memberId: String,
    private val subscriptionId: String,
    getMemberSubscriptionDetail: GetMemberSubscriptionDetailUseCase,
    getMemberPaymentHistory: GetMemberPaymentHistoryUseCase,
    private val markOwnPaymentUseCase: MarkOwnPaymentUseCase,
    private val requestGroupExitUseCase: RequestGroupExitUseCase
) : ViewModel() {



    val uiState: StateFlow<MemberDetailUiState> = combine(
        getMemberSubscriptionDetail(subscriptionId, memberId),
        getMemberPaymentHistory(memberId, limit = 6)
    ) { view, payments ->
        if (view == null) MemberDetailUiState.NotFound
        else MemberDetailUiState.Success(view, payments)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberDetailUiState.Loading
    )

    private val _activeSheet = MutableStateFlow<MemberActionSheet>(MemberActionSheet.None)
    val activeSheet: StateFlow<MemberActionSheet> = _activeSheet.asStateFlow()

    private val _events = MutableSharedFlow<MemberDetailEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<MemberDetailEvent> = _events.asSharedFlow()

    fun openMarkAsPaid() { _activeSheet.value = MemberActionSheet.MarkAsPaid }
    fun openRequestExit() { _activeSheet.value = MemberActionSheet.RequestExit }
    fun closeSheet() { _activeSheet.value = MemberActionSheet.None }

    fun markAsPaid(note: String?) {
        viewModelScope.launch {
            val monthKey = currentMonthKey()
            markOwnPaymentUseCase(memberId, monthKey, note)
                .onSuccess {
                    _events.emit(MemberDetailEvent.PaymentMarked)
                    closeSheet()
                }
                .onFailure { _events.emit(MemberDetailEvent.Error) }
        }
    }

    fun requestExit() {
        viewModelScope.launch {
            requestGroupExitUseCase(memberId, subscriptionId)
                .onSuccess {
                    _events.emit(MemberDetailEvent.ExitRequested)
                    closeSheet()
                }
                .onFailure { _events.emit(MemberDetailEvent.Error) }
        }
    }

    fun currentMonthKey(): String = today().monthKey()
}
