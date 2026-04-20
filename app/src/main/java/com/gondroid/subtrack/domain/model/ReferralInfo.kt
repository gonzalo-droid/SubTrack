package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.ReferralStatus

data class ReferralInfo(
    val code: String,
    val totalInvited: Int,
    val activeReferrals: Int,
    val pendingReferrals: Int,
    val monthsProEarned: Int,
    val nextMilestone: Int,
    val nextMilestoneReward: String
)

data class ReferralRecord(
    val id: String,
    val referredName: String,
    val referredAt: Long,
    val status: ReferralStatus
)
