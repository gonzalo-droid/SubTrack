package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.TemplateTone

data class Template(
    val id: String,
    val name: String,
    val emoji: String,
    val tone: TemplateTone,
    val messageBody: String,
    val isDefault: Boolean = false,
    val isUserCreated: Boolean = false
)
