package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class PersonSummary(
    val userId: String?,
    val name: String,
    val phone: String,
    val hasApp: Boolean,
    val totalDebt: Double,
    val totalMonthlyContribution: Double,
    val punctualityPercent: Int,
    val firstJoinedAt: Long,
    val lastActivityAt: Long?,
    val subscriptions: List<SubscriptionParticipation>
)

data class SubscriptionParticipation(
    val subscriptionId: String,
    val subscriptionName: String,
    val brandColor: String,
    val status: PaymentStatus,
    val shareAmount: Double,
    val isArchived: Boolean
)
