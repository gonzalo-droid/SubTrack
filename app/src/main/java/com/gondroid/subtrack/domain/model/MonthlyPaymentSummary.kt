package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class MonthlyPaymentSummary(
    val monthKey: String,
    val totalCollected: Double,
    val memberStatuses: List<MemberPaymentStatus>
)

data class MemberPaymentStatus(
    val memberId: String,
    val memberName: String,
    val status: PaymentStatus?
)
