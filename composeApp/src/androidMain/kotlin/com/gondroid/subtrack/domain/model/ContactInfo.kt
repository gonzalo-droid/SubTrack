package com.gondroid.subtrack.domain.model

data class ContactInfo(
    val id: String,
    val name: String,
    val phone: String,
    val hasApp: Boolean = false
)
