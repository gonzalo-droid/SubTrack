package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class Member(
    val id: String,
    val userId: String?,
    val name: String,
    val phone: String,
    val profileLabel: String?,
    val shareAmount: Double,
    val isArchived: Boolean = false,
    val currentStatus: PaymentStatus,
    val joinedAt: Long
)
