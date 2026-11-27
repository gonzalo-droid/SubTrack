package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class DebtorInfo(
    val memberId: String,
    val memberName: String,
    val memberPhone: String,
    val hasApp: Boolean,
    val subscriptionId: String,
    val subscriptionName: String,
    val shareAmount: Double,
    val status: PaymentStatus,
    val monthKey: String
)
