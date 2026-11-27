package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class Payment(
    val id: String,
    val subscriptionId: String,
    val memberId: String,
    val monthKey: String,
    val amount: Double,
    val status: PaymentStatus,
    val paidAt: Long?,
    val proofUrl: String?,
    val note: String?
)
