package com.gondroid.subtrack.feature.profile.editprofile

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState
    data class Success(val data: EditProfileData) : EditProfileUiState
}

sealed interface EditProfileEvent {
    data object Saved : EditProfileEvent
}

enum class AuthProvider { GOOGLE, APPLE }

data class EditProfileData(
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val authProvider: AuthProvider,
    val imageUrl: String?,
    val hasChanges: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: String? = null
)
