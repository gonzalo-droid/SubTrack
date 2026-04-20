package com.gondroid.subtrack.domain.model

data class NewMemberData(
    val name: String,
    val phone: String,
    val shareAmount: Double,
    val profileLabel: String? = null,
    val transferringFromMemberId: String? = null
)
