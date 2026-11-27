package com.gondroid.subtrack.domain.model

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val profileImageUrl: String? = null,
    val isPro: Boolean = false,
    val referralCode: String,
    val createdAt: Long
)
