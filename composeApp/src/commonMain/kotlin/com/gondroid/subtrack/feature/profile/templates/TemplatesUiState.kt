package com.gondroid.subtrack.feature.profile.templates

import com.gondroid.subtrack.domain.model.Template

sealed interface TemplatesUiState {
    data object Loading : TemplatesUiState
    data class Success(val templates: List<Template>) : TemplatesUiState
}

sealed interface TemplateActionSheet {
    data object None : TemplateActionSheet
    data class Actions(val template: Template) : TemplateActionSheet
    data class DeleteConfirmation(val template: Template) : TemplateActionSheet
}
