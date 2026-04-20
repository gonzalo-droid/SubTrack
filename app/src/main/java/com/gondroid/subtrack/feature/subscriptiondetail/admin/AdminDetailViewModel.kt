package com.gondroid.subtrack.feature.subscriptiondetail.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.model.ContactInfo
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.NewMemberData
import com.gondroid.subtrack.domain.model.enums.DebtDecision
import com.gondroid.subtrack.domain.usecase.member.AddMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.ArchiveMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.GetArchivedMembersUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMembersForSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.member.MarkMemberAsPaidUseCase
import com.gondroid.subtrack.domain.usecase.member.RemoveMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.UpdateMemberUseCase
import com.gondroid.subtrack.domain.usecase.payment.GetPaymentHistoryUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.util.Calendar
import javax.inject.Inject

// ── Sheet navigation state ────────────────────────────────────────────────────

sealed interface ActiveSheet {
    data object None : ActiveSheet
    data object AddMemberStep1 : ActiveSheet
    data class AddMemberStep2(val selectedContact: ContactInfo) : ActiveSheet
    data class EditMember(val memberId: String) : ActiveSheet
    data class EditMemberData(val memberId: String) : ActiveSheet
    data class RemoveMember(val memberId: String) : ActiveSheet
    data class ExitRequest(val requestId: String) : ActiveSheet
    data object BulkReminder : ActiveSheet
}

// ── One-shot events to composable ─────────────────────────────────────────────

sealed interface AdminDetailEvent {
    data class MemberAdded(val name: String) : AdminDetailEvent
    data class MemberMarkedAsPaid(val name: String) : AdminDetailEvent
    data class MemberArchived(val name: String) : AdminDetailEvent
    data class MemberForgiven(val name: String) : AdminDetailEvent
    data class MemberUpdated(val name: String) : AdminDetailEvent
    data object BulkReminderStarted : AdminDetailEvent
    data class Error(val message: String) : AdminDetailEvent
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

// TODO: [hilt] When @HiltViewModel injection is active, restore SavedStateHandle injection
@HiltViewModel
class AdminDetailViewModel @Inject constructor(
    private val subscriptionId: String,
    getSubscriptionDetail: GetSubscriptionDetailUseCase,
    getMembersForSubscription: GetMembersForSubscriptionUseCase,
    getArchivedMembers: GetArchivedMembersUseCase,
    getPaymentHistory: GetPaymentHistoryUseCase,
    private val addMemberUseCase: AddMemberUseCase,
    private val updateMemberUseCase: UpdateMemberUseCase,
    private val archiveMemberUseCase: ArchiveMemberUseCase,
    private val removeMemberUseCase: RemoveMemberUseCase,
    private val markMemberAsPaidUseCase: MarkMemberAsPaidUseCase
) : ViewModel() {

    val uiState: StateFlow<AdminDetailUiState> = combine(
        getSubscriptionDetail(subscriptionId),
        getMembersForSubscription(subscriptionId),
        getArchivedMembers(subscriptionId),
        getPaymentHistory(subscriptionId, months = 3)
    ) { subscription, activeMembers, archivedMembers, history ->
        if (subscription == null) {
            AdminDetailUiState.NotFound
        } else {
            AdminDetailUiState.Success(
                buildAdminDetailData(subscription, activeMembers, archivedMembers, history)
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminDetailUiState.Loading
    )

    private val _activeSheet = MutableStateFlow<ActiveSheet>(ActiveSheet.None)
    val activeSheet: StateFlow<ActiveSheet> = _activeSheet.asStateFlow()

    private val _events = MutableSharedFlow<AdminDetailEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<AdminDetailEvent> = _events.asSharedFlow()

    var transferringFromMemberId: String? = null
        private set

    // ── Sheet navigation ──────────────────────────────────────────────────────

    fun openAddMember() { _activeSheet.value = ActiveSheet.AddMemberStep1 }

    fun onContactSelected(contact: ContactInfo) {
        _activeSheet.value = ActiveSheet.AddMemberStep2(contact)
    }

    fun openEditMember(memberId: String) { _activeSheet.value = ActiveSheet.EditMember(memberId) }

    fun openEditMemberData(memberId: String) { _activeSheet.value = ActiveSheet.EditMemberData(memberId) }

    fun openRemoveMember(memberId: String) { _activeSheet.value = ActiveSheet.RemoveMember(memberId) }

    fun openBulkReminder() { _activeSheet.value = ActiveSheet.BulkReminder }

    fun closeSheet() {
        _activeSheet.value = ActiveSheet.None
        transferringFromMemberId = null
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    fun addMember(data: NewMemberData) {
        viewModelScope.launch {
            addMemberUseCase(subscriptionId, data)
                .onSuccess { member ->
                    _events.emit(AdminDetailEvent.MemberAdded(member.name))
                    closeSheet()
                }
                .onFailure { _events.emit(AdminDetailEvent.Error(it.message ?: "Error")) }
        }
    }

    fun updateMember(member: Member) {
        viewModelScope.launch {
            updateMemberUseCase(member)
                .onSuccess {
                    _events.emit(AdminDetailEvent.MemberUpdated(member.name))
                    closeSheet()
                }
                .onFailure { _events.emit(AdminDetailEvent.Error(it.message ?: "Error")) }
        }
    }

    fun archiveMember(memberId: String, decision: DebtDecision) {
        val memberName = resolveActiveMemberName(memberId)
        viewModelScope.launch {
            when (decision) {
                DebtDecision.ARCHIVE_WITH_DEBT -> {
                    archiveMemberUseCase(memberId).onSuccess {
                        _events.emit(AdminDetailEvent.MemberArchived(memberName))
                        closeSheet()
                    }
                }
                DebtDecision.FORGIVE -> {
                    removeMemberUseCase(memberId).onSuccess {
                        _events.emit(AdminDetailEvent.MemberForgiven(memberName))
                        closeSheet()
                    }
                }
                DebtDecision.TRANSFER_TO_NEW -> {
                    archiveMemberUseCase(memberId).onSuccess {
                        transferringFromMemberId = memberId
                        _activeSheet.value = ActiveSheet.AddMemberStep1
                    }
                }
            }
        }
    }

    fun markAsPaid(memberId: String) {
        val memberName = resolveActiveMemberName(memberId)
        viewModelScope.launch {
            val monthKey = currentMonthKey()
            markMemberAsPaidUseCase(memberId, monthKey)
                .onSuccess {
                    _events.emit(AdminDetailEvent.MemberMarkedAsPaid(memberName))
                    closeSheet()
                }
                .onFailure { _events.emit(AdminDetailEvent.Error(it.message ?: "Error")) }
        }
    }

    fun onBulkReminderSent() {
        viewModelScope.launch {
            _events.emit(AdminDetailEvent.BulkReminderStarted)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun currentMonthKey(): String {
        val cal = Calendar.getInstance()
        return "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    private fun resolveActiveMemberName(memberId: String): String {
        val state = uiState.value
        return if (state is AdminDetailUiState.Success) {
            state.data.activeMembers.find { it.id == memberId }?.name ?: memberId
        } else memberId
    }
}
