package com.gondroid.subtrack.feature.onboarding

import com.gondroid.subtrack.core.result.onSuccess

import com.gondroid.subtrack.core.result.onFailure

import com.gondroid.subtrack.core.result.DataError

import com.gondroid.subtrack.core.result.AppResult

import com.gondroid.subtrack.platform.notificationsNeedPermission

import com.gondroid.subtrack.domain.util.randomId

import com.gondroid.subtrack.domain.util.currentTimeMillis

import com.gondroid.subtrack.domain.model.GoogleCredential

import com.gondroid.subtrack.core.util.logError

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.usecase.auth.AuthenticateUserUseCase
import com.gondroid.subtrack.domain.usecase.subscription.CreateSubscriptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import com.gondroid.subtrack.domain.usecase.servicetemplate.GetPopularServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// ── Step enum ────────────────────────────────────────────────────────────────

enum class OnboardingStep {
    VALUE_TRACKER, VALUE_SHARE, VALUE_INSIGHTS, AUTH, FIRST_SUBSCRIPTION, NOTIFICATIONS, SUCCESS
}

// ── Auth state ────────────────────────────────────────────────────────────────

sealed interface AuthState {
    data object Idle : AuthState
    data object Authenticating : AuthState
    data class Authenticated(val user: User) : AuthState
    /** The UI explains [error] (no connection or generic); the cause goes to the log. */
    data class Error(val error: DataError) : AuthState
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class OnboardingViewModel(
    private val authenticateUser: AuthenticateUserUseCase,
    private val createSubscription: CreateSubscriptionUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    getPopularServices: GetPopularServicesUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val popularServices: List<ServiceTemplate> = getPopularServices()

    /** First name for the success greeting; null until the user is available. */
    val userFirstName: StateFlow<String?> = getCurrentUser()
        .map { it?.name?.substringBefore(" ") }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _currentStep = MutableStateFlow(OnboardingStep.VALUE_TRACKER)
    val currentStep: StateFlow<OnboardingStep> = _currentStep.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _selectedService = MutableStateFlow(popularServices.firstOrNull())
    val selectedService: StateFlow<ServiceTemplate?> = _selectedService.asStateFlow()

    private val _isCreatingSubscription = MutableStateFlow(false)
    val isCreatingSubscription: StateFlow<Boolean> = _isCreatingSubscription.asStateFlow()

    private val _createdServiceName = MutableStateFlow<String?>(null)
    val createdServiceName: StateFlow<String?> = _createdServiceName.asStateFlow()

    /**
     * Back to the first step. This ViewModel lives as long as the Activity, so after a sign-out
     * the onboarding would otherwise reopen on its last screen.
     */
    fun restart() {
        _currentStep.value = OnboardingStep.VALUE_TRACKER
        _authState.value = AuthState.Idle
        _selectedService.value = popularServices.firstOrNull()
        _isCreatingSubscription.value = false
        _createdServiceName.value = null
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    fun goToNextValue() {
        _currentStep.value = when (_currentStep.value) {
            OnboardingStep.VALUE_TRACKER -> OnboardingStep.VALUE_SHARE
            OnboardingStep.VALUE_SHARE -> OnboardingStep.VALUE_INSIGHTS
            else -> OnboardingStep.AUTH
        }
    }

    fun skipToAuth() {
        _currentStep.value = OnboardingStep.AUTH
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    fun authenticateWithGoogle(credential: GoogleCredential) = authenticate { authenticateUser.withGoogle(credential) }

    /** The account picker failed before reaching Firebase (no Google account, no network...). */
    fun onGoogleSignInFailed() {
        _authState.value = AuthState.Error(DataError.Unknown("Google sign-in failed"))
    }

    private fun authenticate(block: suspend () -> AppResult<User>) {
        viewModelScope.launch {
            _authState.value = AuthState.Authenticating
            block()
                .onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                    _currentStep.value = OnboardingStep.FIRST_SUBSCRIPTION
                }
                .onFailure { error ->
                    logError(TAG, "Sign-in failed: $error")
                    _authState.value = AuthState.Error(error)
                }
        }
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    // Asked right after the first subscription exists, when the reminders make sense.

    fun onNotificationPromptDone() {
        viewModelScope.launch {
            userPreferences.setNotificationPermissionRequested(true)
            _currentStep.value = OnboardingStep.SUCCESS
        }
    }

    // ── First subscription ────────────────────────────────────────────────────

    fun selectService(template: ServiceTemplate) {
        _selectedService.value = template
    }

    fun createFirstSubscription() {
        val service = _selectedService.value ?: return
        viewModelScope.launch {
            _isCreatingSubscription.value = true
            val now = currentTimeMillis()
            val sub = Subscription(
                id = "sub_${randomId()}",
                name = service.name,
                brandColor = service.brandColor,
                totalAmount = service.suggestedMonthly ?: 0.0,
                cycle = BillingCycle.MONTHLY,
                cutoffDay = 15,
                ownerId = getCurrentUser().first()?.id.orEmpty(),
                isShared = false,
                category = service.category,
                createdAt = now,
                updatedAt = now
            )
            createSubscription(sub)
            _createdServiceName.value = service.name
            _isCreatingSubscription.value = false
            val askNotifications = notificationsNeedPermission &&
                !userPreferences.notificationPermissionRequested.first()
            _currentStep.value = if (askNotifications) OnboardingStep.NOTIFICATIONS else OnboardingStep.SUCCESS
        }
    }

    fun skipFirstSubscription() {
        _currentStep.value = OnboardingStep.SUCCESS
    }

    private companion object {
        const val TAG = "OnboardingViewModel"
    }
}
