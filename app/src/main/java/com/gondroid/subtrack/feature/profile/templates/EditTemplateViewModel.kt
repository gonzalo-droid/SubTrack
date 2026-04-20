package com.gondroid.subtrack.feature.profile.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockTemplateRepository
import com.gondroid.subtrack.domain.model.enums.TemplateTone
import com.gondroid.subtrack.domain.model.TemplateVariable
import com.gondroid.subtrack.domain.usecase.template.DeleteTemplateUseCase
import com.gondroid.subtrack.domain.usecase.template.GetTemplateByIdUseCase
import com.gondroid.subtrack.domain.usecase.template.SaveTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
    private val getTemplateById: GetTemplateByIdUseCase,
    private val saveTemplate: SaveTemplateUseCase,
    private val deleteTemplate: DeleteTemplateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditTemplateUiState>(EditTemplateUiState.Loading)
    val uiState: StateFlow<EditTemplateUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditTemplateEvent>()
    val events = _events.asSharedFlow()

    private var originalForm: TemplateFormState? = null

    val hasUnsavedChanges: StateFlow<Boolean> get() = MutableStateFlow(
        (_uiState.value as? EditTemplateUiState.Editing)?.form != originalForm
    )

    fun init(templateId: String?) {
        if (_uiState.value !is EditTemplateUiState.Loading) return
        viewModelScope.launch {
            if (templateId == null) {
                val newForm = TemplateFormState(
                    id = "tpl_new_${System.currentTimeMillis()}",
                    name = "",
                    emoji = "💬",
                    tone = TemplateTone.FRIENDLY,
                    messageBody = "",
                    isDefault = false,
                    isUserCreated = true
                )
                originalForm = newForm
                _uiState.value = EditTemplateUiState.Editing(newForm)
            } else {
                val template = getTemplateById(templateId)
                if (template != null) {
                    val form = TemplateFormState(
                        id = template.id,
                        name = template.name,
                        emoji = template.emoji,
                        tone = template.tone,
                        messageBody = template.messageBody,
                        isDefault = template.isDefault,
                        isUserCreated = template.isUserCreated
                    )
                    originalForm = form
                    _uiState.value = EditTemplateUiState.Editing(form)
                }
            }
        }
    }

    fun updateName(name: String) = updateForm { copy(name = name) }
    fun updateEmoji(emoji: String) = updateForm { copy(emoji = emoji) }
    fun updateTone(tone: TemplateTone) = updateForm { copy(tone = tone) }
    fun updateMessageBody(body: String) = updateForm { copy(messageBody = body) }
    fun updateCursor(position: Int) = updateForm { copy(cursorPosition = position) }
    fun togglePreview() = updateForm { copy(previewExpanded = !previewExpanded) }

    fun insertVariable(variable: TemplateVariable) {
        val editing = _uiState.value as? EditTemplateUiState.Editing ?: return
        val form = editing.form
        val pos = form.cursorPosition.coerceIn(0, form.messageBody.length)
        val newBody = form.messageBody.substring(0, pos) + variable.token + form.messageBody.substring(pos)
        val newCursor = pos + variable.token.length
        updateForm { copy(messageBody = newBody, cursorPosition = newCursor) }
    }

    fun save() {
        val editing = _uiState.value as? EditTemplateUiState.Editing ?: return
        _uiState.value = EditTemplateUiState.Saving
        viewModelScope.launch {
            saveTemplate(editing.form.toTemplate())
                .onSuccess { _events.emit(EditTemplateEvent.Saved) }
                .onFailure { _events.emit(EditTemplateEvent.Error(it.message ?: "Error al guardar")) }
            _uiState.value = editing
        }
    }

    fun delete() {
        val editing = _uiState.value as? EditTemplateUiState.Editing ?: return
        if (editing.form.isDefault) {
            viewModelScope.launch { _events.emit(EditTemplateEvent.Error("No se pueden eliminar plantillas por defecto")) }
            return
        }
        viewModelScope.launch {
            deleteTemplate(editing.form.id)
                .onSuccess { _events.emit(EditTemplateEvent.Deleted) }
                .onFailure { _events.emit(EditTemplateEvent.Error(it.message ?: "Error")) }
        }
    }

    private fun updateForm(block: TemplateFormState.() -> TemplateFormState) {
        _uiState.update { state ->
            if (state is EditTemplateUiState.Editing) EditTemplateUiState.Editing(state.form.block())
            else state
        }
    }
}

class EditTemplateViewModelFactory(private val templateId: String?) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = MockTemplateRepository()
        return EditTemplateViewModel(
            getTemplateById = GetTemplateByIdUseCase(repo),
            saveTemplate = SaveTemplateUseCase(repo),
            deleteTemplate = DeleteTemplateUseCase(repo)
        ).also { it.init(templateId) } as T
    }
}
