package com.gondroid.subtrack

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.core.preferences.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppReadyState {
    data object Loading : AppReadyState
    data object ShowOnboarding : AppReadyState
    data object ShowMain : AppReadyState
}

class AppStateViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val state: StateFlow<AppReadyState> = userPreferences.onboardingCompleted
        .map { completed ->
            if (completed) AppReadyState.ShowMain else AppReadyState.ShowOnboarding
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppReadyState.Loading
        )

    fun markOnboardingComplete() {
        viewModelScope.launch { userPreferences.setOnboardingCompleted(true) }
    }

    fun resetOnboarding() {
        viewModelScope.launch { userPreferences.setOnboardingCompleted(false) }
    }
}

class AppStateViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AppStateViewModel(UserPreferences.getInstance(context)) as T
}
