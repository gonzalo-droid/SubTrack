package com.gondroid.subtrack.feature.subscriptiondetail.admin

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.MonthlyPaymentSummary
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class AdminDetailData(
    val subscription: Subscription,
    val activeMembers: List<Member>,
    val archivedMembers: List<Member>,
    val monthlyHistory: List<MonthlyPaymentSummary>,
    val pendingPaymentsCount: Int,
    val pendingAmount: Double
)

sealed interface AdminDetailUiState {
    data object Loading : AdminDetailUiState
    data class Success(val data: AdminDetailData) : AdminDetailUiState
    data object NotFound : AdminDetailUiState
}

private fun isPending(status: PaymentStatus) =
    status == PaymentStatus.PENDING || status == PaymentStatus.OVERDUE

fun buildAdminDetailData(
    subscription: Subscription,
    activeMembers: List<Member>,
    archivedMembers: List<Member>,
    monthlyHistory: List<MonthlyPaymentSummary>
): AdminDetailData {
    val pendingMembers = activeMembers.filter { isPending(it.currentStatus) }
    return AdminDetailData(
        subscription = subscription,
        activeMembers = activeMembers,
        archivedMembers = archivedMembers,
        monthlyHistory = monthlyHistory,
        pendingPaymentsCount = pendingMembers.size,
        pendingAmount = pendingMembers.sumOf { it.shareAmount }
    )
}
