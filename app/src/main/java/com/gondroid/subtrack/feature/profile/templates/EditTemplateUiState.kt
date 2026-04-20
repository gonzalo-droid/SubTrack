package com.gondroid.subtrack.feature.profile.templates

import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.model.enums.TemplateTone

data class TemplateFormState(
    val id: String,
    val name: String,
    val emoji: String,
    val tone: TemplateTone,
    val messageBody: String,
    val isDefault: Boolean,
    val isUserCreated: Boolean,
    val cursorPosition: Int = 0,
    val previewExpanded: Boolean = false
) {
    val charCount: Int get() = messageBody.length
    val isValid: Boolean get() = name.isNotBlank() && messageBody.isNotBlank()

    fun toTemplate() = Template(
        id = id,
        name = name,
        emoji = emoji,
        tone = tone,
        messageBody = messageBody,
        isDefault = isDefault,
        isUserCreated = isUserCreated
    )
}

sealed interface EditTemplateUiState {
    data object Loading : EditTemplateUiState
    data class Editing(val form: TemplateFormState) : EditTemplateUiState
    data object Saving : EditTemplateUiState
}

sealed interface EditTemplateEvent {
    data object Saved : EditTemplateEvent
    data object Deleted : EditTemplateEvent
    data class Error(val message: String) : EditTemplateEvent
}
