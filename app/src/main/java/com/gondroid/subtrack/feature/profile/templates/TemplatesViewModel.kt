package com.gondroid.subtrack.feature.profile.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockTemplateRepository
import com.gondroid.subtrack.domain.usecase.template.DeleteTemplateUseCase
import com.gondroid.subtrack.domain.usecase.template.DuplicateTemplateUseCase
import com.gondroid.subtrack.domain.usecase.template.GetAllTemplatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TemplatesEvent {
    data class Duplicated(val name: String) : TemplatesEvent
    data class Deleted(val name: String) : TemplatesEvent
    data class Error(val message: String) : TemplatesEvent
}

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val getAllTemplates: GetAllTemplatesUseCase,
    private val duplicateTemplate: DuplicateTemplateUseCase,
    private val deleteTemplate: DeleteTemplateUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<TemplatesEvent>()
    val events = _events.asSharedFlow()

    val uiState = getAllTemplates().map { templates ->
        TemplatesUiState.Success(templates)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TemplatesUiState.Loading)

    private val _actionSheet = MutableStateFlow<TemplateActionSheet>(TemplateActionSheet.None)
    val actionSheet = _actionSheet

    fun openActions(templateId: String) {
        val template = (uiState.value as? TemplatesUiState.Success)
            ?.templates?.find { it.id == templateId } ?: return
        _actionSheet.value = TemplateActionSheet.Actions(template)
    }

    fun closeActions() {
        _actionSheet.value = TemplateActionSheet.None
    }

    fun requestDelete(templateId: String) {
        val template = (uiState.value as? TemplatesUiState.Success)
            ?.templates?.find { it.id == templateId } ?: return
        _actionSheet.value = TemplateActionSheet.DeleteConfirmation(template)
    }

    fun duplicate(templateId: String) {
        viewModelScope.launch {
            val name = (uiState.value as? TemplatesUiState.Success)
                ?.templates?.find { it.id == templateId }?.name ?: ""
            duplicateTemplate(templateId)
                .onSuccess { _events.emit(TemplatesEvent.Duplicated(name)) }
                .onFailure { _events.emit(TemplatesEvent.Error(it.message ?: "Error")) }
            closeActions()
        }
    }

    fun delete(templateId: String) {
        viewModelScope.launch {
            val name = (uiState.value as? TemplatesUiState.Success)
                ?.templates?.find { it.id == templateId }?.name ?: ""
            deleteTemplate(templateId)
                .onSuccess { _events.emit(TemplatesEvent.Deleted(name)) }
                .onFailure { _events.emit(TemplatesEvent.Error(it.message ?: "Error")) }
            closeActions()
        }
    }
}

class TemplatesViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = MockTemplateRepository()
        return TemplatesViewModel(
            getAllTemplates = GetAllTemplatesUseCase(repo),
            duplicateTemplate = DuplicateTemplateUseCase(repo),
            deleteTemplate = DeleteTemplateUseCase(repo)
        ) as T
    }
}
