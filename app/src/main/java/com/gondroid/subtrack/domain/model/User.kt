package com.gondroid.subtrack.domain.model

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val profileImageUrl: String?,
    val isPro: Boolean = false,
    val createdAt: Long
)
