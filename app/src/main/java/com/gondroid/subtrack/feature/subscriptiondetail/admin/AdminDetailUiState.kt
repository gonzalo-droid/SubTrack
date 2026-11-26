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
    val pendingAmount: Double,
    val adminName: String,
    /** Name of the only member who owes, so the CTA can say "Cobrar a Ana". */
    val singlePendingName: String? = null
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
    monthlyHistory: List<MonthlyPaymentSummary>,
    adminName: String
): AdminDetailData {
    val pendingMembers = activeMembers.filter { isPending(it.currentStatus) }
    return AdminDetailData(
        subscription = subscription,
        activeMembers = activeMembers,
        archivedMembers = archivedMembers,
        monthlyHistory = monthlyHistory,
        pendingPaymentsCount = pendingMembers.size,
        pendingAmount = pendingMembers.sumOf { it.shareAmount },
        adminName = adminName,
        singlePendingName = pendingMembers.singleOrNull()?.name?.substringBefore(" ")
    )
}
