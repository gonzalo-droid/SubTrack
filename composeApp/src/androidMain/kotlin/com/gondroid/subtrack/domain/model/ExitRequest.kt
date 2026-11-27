package com.gondroid.subtrack.domain.model

enum class ExitRequestStatus { PENDING, APPROVED, REJECTED }

data class ExitRequest(
    val id: String,
    val memberId: String,
    val subscriptionId: String,
    val requestedAt: Long,
    val status: ExitRequestStatus = ExitRequestStatus.PENDING
)
