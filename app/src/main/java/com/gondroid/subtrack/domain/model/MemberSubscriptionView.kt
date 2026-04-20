package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

data class AdminPublicInfo(
    val name: String,
    val phone: String
)

data class MemberSubscriptionView(
    val subscriptionId: String,
    val serviceName: String,
    val brandColor: String,
    val cycle: BillingCycle,
    val cutoffDay: Int,
    val currency: String,
    val myMemberId: String,
    val myShareAmount: Double,
    val myCurrentStatus: PaymentStatus,
    val myJoinedAt: Long,
    val adminInfo: AdminPublicInfo,
    val pendingExitRequest: ExitRequest? = null
)
