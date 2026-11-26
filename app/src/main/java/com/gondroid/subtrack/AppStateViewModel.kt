package com.gondroid.subtrack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.core.preferences.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.gondroid.subtrack.core.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface AppReadyState {
    data object Loading : AppReadyState
    data object ShowOnboarding : AppReadyState
    data object ShowMain : AppReadyState
}

@HiltViewModel
class AppStateViewModel @Inject constructor(
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

    private val _pendingRoute = MutableStateFlow<Route?>(null)
    /** Screen to open over the dashboard once the main app shows (from the onboarding success). */
    val pendingRoute: StateFlow<Route?> = _pendingRoute.asStateFlow()

    fun markOnboardingComplete(then: Route? = null) {
        _pendingRoute.value = then
        viewModelScope.launch { userPreferences.setOnboardingCompleted(true) }
    }

    fun consumePendingRoute() {
        _pendingRoute.value = null
    }

    fun resetOnboarding() {
        viewModelScope.launch { userPreferences.setOnboardingCompleted(false) }
    }
}
