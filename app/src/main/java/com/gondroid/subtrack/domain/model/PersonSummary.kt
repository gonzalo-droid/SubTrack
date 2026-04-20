package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class PersonSummary(
    val userId: String?,
    val name: String,
    val phone: String,
    val hasApp: Boolean,
    val totalDebt: Double,
    val subscriptions: List<PersonSubscriptionChip>
)

data class PersonSubscriptionChip(
    val subscriptionId: String,
    val subscriptionName: String,
    val status: PaymentStatus,
    val shareAmount: Double
)
