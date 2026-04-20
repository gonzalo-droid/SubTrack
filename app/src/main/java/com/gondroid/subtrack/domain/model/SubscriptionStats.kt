package com.gondroid.subtrack.domain.model

data class SubscriptionStats(
    val totalCount: Int,
    val personalCount: Int,
    val sharedCount: Int,
    val totalMonthlyAmount: Double,
    val pendingMembersCount: Int
)
