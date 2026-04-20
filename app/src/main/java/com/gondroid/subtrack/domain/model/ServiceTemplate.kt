package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory

data class ServiceTemplate(
    val id: String,
    val name: String,
    val brandColor: String,
    val defaultLogoChar: String,
    val suggestedMonthly: Double? = null,
    val category: SubscriptionCategory,
    val isPopular: Boolean = true
)
