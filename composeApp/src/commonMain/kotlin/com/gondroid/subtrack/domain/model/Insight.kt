package com.gondroid.subtrack.domain.model

enum class InsightType { UNUSED_SUBSCRIPTION }

data class Insight(
    val type: InsightType,
    val subscriptionId: String,
    val subscriptionName: String,
    val amount: Double,
    val daysSinceActivity: Int
)
