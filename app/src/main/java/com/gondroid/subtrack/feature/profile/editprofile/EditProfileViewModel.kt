package com.gondroid.subtrack.feature.profile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.data.mock.MockUserRepository
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import com.gondroid.subtrack.domain.usecase.user.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val updateUser: UpdateUserUseCase,
    private val authProvider: AuthProvider = AuthProvider.GOOGLE
) : ViewModel() {

    private val _events = MutableSharedFlow<EditProfileEvent>()
    val events = _events.asSharedFlow()

    private val _draft = MutableStateFlow<EditProfileData?>(null)
    private var originalUser: User? = null

    val uiState = combine(getCurrentUser(), _draft) { user, draft ->
        if (user != null && originalUser == null) originalUser = user
        if (draft != null) return@combine EditProfileUiState.Success(draft)
        if (user == null) return@combine EditProfileUiState.Loading
        EditProfileUiState.Success(
            EditProfileData(
                id = user.id,
                name = user.name,
                phone = user.phone,
                email = user.email,
                authProvider = authProvider,
                imageUrl = user.profileImageUrl
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditProfileUiState.Loading)

    fun onNameChange(value: String) = updateDraft { copy(name = value, hasChanges = true, nameError = null) }

    fun onPhoneChange(value: String) = updateDraft { copy(phone = value, hasChanges = true) }

    fun onPhotoSelected(uri: String) = updateDraft { copy(imageUrl = uri, hasChanges = true) }

    fun save() {
        val state = uiState.value as? EditProfileUiState.Success ?: return
        val data = state.data
        val base = originalUser ?: return

        if (data.name.isBlank()) {
            updateDraft { copy(nameError = "El nombre no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            updateDraft { copy(isSaving = true) }
            updateUser(
                base.copy(
                    name = data.name.trim(),
                    phone = data.phone.trim(),
                    profileImageUrl = data.imageUrl
                )
            )
            _events.emit(EditProfileEvent.Saved)
        }
    }

    private fun updateDraft(block: EditProfileData.() -> EditProfileData) {
        val current = (uiState.value as? EditProfileUiState.Success)?.data ?: return
        _draft.value = current.block()
    }
}

class EditProfileViewModelFactory(
    private val authProvider: AuthProvider = AuthProvider.GOOGLE
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = MockUserRepository()
        return EditProfileViewModel(
            getCurrentUser = GetCurrentUserUseCase(repo),
            updateUser = UpdateUserUseCase(repo),
            authProvider = authProvider
        ) as T
    }
}
