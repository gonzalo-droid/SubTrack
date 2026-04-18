package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory

data class Subscription(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val brandColor: String,
    val totalAmount: Double,
    val currency: String = "PEN",
    val cycle: BillingCycle,
    val cutoffDay: Int,
    val ownerId: String,
    val isShared: Boolean,
    val category: SubscriptionCategory,
    val members: List<Member> = emptyList(),
    val archivedMembers: List<Member> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long
)
