package com.gondroid.subtrack.feature.profile.alias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockUserAliasRepository
import com.gondroid.subtrack.domain.model.enums.PaymentAliasType
import com.gondroid.subtrack.domain.usecase.alias.GetUserAliasUseCase
import com.gondroid.subtrack.domain.usecase.alias.UpdateUserAliasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AliasEditEvent {
    data object Saved : AliasEditEvent
}

@HiltViewModel
class AliasEditViewModel @Inject constructor(
    private val getUserAlias: GetUserAliasUseCase,
    private val updateUserAlias: UpdateUserAliasUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<AliasEditEvent>()
    val events = _events.asSharedFlow()

    private val _draft = MutableStateFlow<AliasEditData?>(null)

    val uiState = getUserAlias().map { alias ->
        val current = _draft.value
        if (current != null) return@map AliasEditUiState.Success(current)
        AliasEditUiState.Success(
            AliasEditData(
                yape = alias.yape ?: "",
                plin = alias.plin ?: "",
                cciBank = alias.cciBank ?: "",
                cciNumber = alias.cciNumber ?: "",
                defaultMethod = alias.defaultMethod
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AliasEditUiState.Loading)

    fun onYapeChange(v: String) = updateDraft { copy(yape = v, hasChanges = true, yapeError = null) }
    fun onPlinChange(v: String) = updateDraft { copy(plin = v, hasChanges = true, plinError = null) }
    fun onCciBankChange(v: String) = updateDraft { copy(cciBank = v, hasChanges = true) }
    fun onCciNumberChange(v: String) = updateDraft { copy(cciNumber = v, hasChanges = true, cciError = null) }
    fun onDefaultMethodChange(method: PaymentAliasType) = updateDraft { copy(defaultMethod = method, hasChanges = true) }

    fun save() {
        val state = uiState.value as? AliasEditUiState.Success ?: return
        val data = state.data
        val yapeErr = if (data.yape.isNotBlank() && !isValidPeruPhone(data.yape)) "Debe tener 9 dígitos" else null
        val plinErr = if (data.plin.isNotBlank() && !isValidPeruPhone(data.plin)) "Debe tener 9 dígitos" else null
        val cciErr = if (data.cciNumber.isNotBlank() && data.cciNumber.length < 10) "Número inválido" else null
        if (yapeErr != null || plinErr != null || cciErr != null) {
            updateDraft { copy(yapeError = yapeErr, plinError = plinErr, cciError = cciErr) }
            return
        }
        viewModelScope.launch {
            updateUserAlias(data.toUserAlias())
            _events.emit(AliasEditEvent.Saved)
        }
    }

    private fun isValidPeruPhone(number: String) = number.filter { it.isDigit() }.length == 9

    private fun updateDraft(block: AliasEditData.() -> AliasEditData) {
        val current = (uiState.value as? AliasEditUiState.Success)?.data ?: return
        _draft.value = current.block()
    }
}

class AliasEditViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = MockUserAliasRepository()
        return AliasEditViewModel(
            getUserAlias = GetUserAliasUseCase(repo),
            updateUserAlias = UpdateUserAliasUseCase(repo)
        ) as T
    }
}
