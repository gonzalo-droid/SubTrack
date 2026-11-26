package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class PersonSummary(
    val userId: String?,
    val name: String,
    val phone: String,
    val hasApp: Boolean,
    val totalDebt: Double,
    val totalMonthlyContribution: Double,
    /** Null when there is no payment history to measure it (e.g. admins you pay). */
    val punctualityPercent: Int?,
    val firstJoinedAt: Long,
    val lastActivityAt: Long?,
    val subscriptions: List<SubscriptionParticipation>
) {
    /** Every shared subscription with this person is paused: listed dimmed, nothing to collect. */
    val isAllPaused: Boolean get() = subscriptions.isNotEmpty() && subscriptions.all { it.isPaused }
}

data class SubscriptionParticipation(
    val subscriptionId: String,
    val subscriptionName: String,
    val brandColor: String,
    val status: PaymentStatus,
    val shareAmount: Double,
    val isArchived: Boolean,
    /** Paused subscriptions stay visible but don't add to debt or monthly totals. */
    val isPaused: Boolean = false
)
