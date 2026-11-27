package com.gondroid.subtrack

import kotlinx.coroutines.flow.onStart

import kotlinx.coroutines.flow.flow

import kotlinx.coroutines.flow.combine

import com.gondroid.subtrack.domain.usecase.app.IsUpdateRequiredUseCase

import com.gondroid.subtrack.core.config.AppInfo

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

sealed interface AppReadyState {
    data object Loading : AppReadyState
    data object ShowOnboarding : AppReadyState
    data object ShowMain : AppReadyState
    /** The installed build is below the remote minimum (UPD-02): only the store can fix it. */
    data object UpdateRequired : AppReadyState
}

class AppStateViewModel(
    private val userPreferences: UserPreferences,
    isUpdateRequired: IsUpdateRequiredUseCase
) : ViewModel() {

    // Checked in the background: the app shows right away and only switches if the build is too old
    private val updateRequired = flow { emit(isUpdateRequired(AppInfo.VERSION_CODE)) }.onStart { emit(false) }

    val state: StateFlow<AppReadyState> = combine(userPreferences.onboardingCompleted, updateRequired) { completed, outdated ->
        when {
            outdated -> AppReadyState.UpdateRequired
            completed -> AppReadyState.ShowMain
            else -> AppReadyState.ShowOnboarding
        }
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
