package com.gondroid.subtrack.domain.model

data class SubscriptionsSummary(
    val totalMonthlySpend: Double,
    val personalSubscriptions: List<Subscription>,
    val sharedSubscriptions: List<Subscription>,
    val upcomingCutoffs: List<Subscription>
)
