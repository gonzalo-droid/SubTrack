package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory

data class Template(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val brandColor: String,
    val defaultAmount: Double?,
    val category: SubscriptionCategory,
    val cycle: BillingCycle
)
