package com.gondroid.subtrack.feature.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.usecase.auth.DeleteAccountUseCase
import com.gondroid.subtrack.domain.usecase.subscription.ExportSubscriptionsCsvUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SettingsUiState(val isDeletingAccount: Boolean = false)

class SettingsViewModel(
    private val exportSubscriptionsCsv: ExportSubscriptionsCsvUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _deleteFailed = Channel<Unit>(Channel.BUFFERED)
    /** Emits when the deletion failed; on success the app goes back to the onboarding by itself. */
    val deleteFailed = _deleteFailed.receiveAsFlow()

    suspend fun buildExport(): String = exportSubscriptionsCsv()

    /** [idToken] comes from a fresh Google sign-in (Firebase needs a recent login to delete). */
    fun deleteAccount(idToken: String) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(isDeletingAccount = true)
            deleteAccountUseCase(idToken).onFailure { _deleteFailed.send(Unit) }
            _uiState.value = SettingsUiState()
        }
    }

    fun onReauthFailed() {
        viewModelScope.launch { _deleteFailed.send(Unit) }
    }
}
