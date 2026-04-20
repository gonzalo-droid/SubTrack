package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SplitType
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory

data class Subscription(
    val id: String,
    val name: String,
    val logoUrl: String? = null,
    val brandColor: String,
    val serviceTemplateId: String? = null,
    val totalAmount: Double,
    val currency: String = "PEN",
    val cycle: BillingCycle,
    val cutoffDay: Int,
    val ownerId: String,
    val isShared: Boolean,
    val splitType: SplitType = SplitType.EQUAL,
    val category: SubscriptionCategory,
    val members: List<Member> = emptyList(),
    val archivedMembers: List<Member> = emptyList(),
    val lastActivityAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)
