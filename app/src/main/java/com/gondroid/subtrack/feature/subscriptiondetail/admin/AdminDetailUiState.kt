package com.gondroid.subtrack.feature.subscriptiondetail.admin

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.MonthlyPaymentSummary
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.isUnpaid

data class AdminDetailData(
    val subscription: Subscription,
    val activeMembers: List<Member>,
    val archivedMembers: List<Member>,
    val monthlyHistory: List<MonthlyPaymentSummary>,
    /** Members the admin can collect from (none while the subscription is paused). */
    val pendingMembers: List<Member>,
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

fun buildAdminDetailData(
    subscription: Subscription,
    activeMembers: List<Member>,
    archivedMembers: List<Member>,
    monthlyHistory: List<MonthlyPaymentSummary>,
    adminName: String
): AdminDetailData {
    val pendingMembers = if (subscription.isPaused) emptyList() else activeMembers.filter { it.currentStatus.isUnpaid }
    return AdminDetailData(
        subscription = subscription,
        activeMembers = activeMembers,
        archivedMembers = archivedMembers,
        monthlyHistory = monthlyHistory,
        pendingMembers = pendingMembers,
        pendingPaymentsCount = pendingMembers.size,
        pendingAmount = pendingMembers.sumOf { it.shareAmount },
        adminName = adminName,
        singlePendingName = pendingMembers.singleOrNull()?.name?.substringBefore(" ")
    )
}
