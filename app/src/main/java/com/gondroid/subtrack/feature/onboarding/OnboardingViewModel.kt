package com.gondroid.subtrack.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.data.mock.MockAuthRepository
import com.gondroid.subtrack.data.mock.MockServiceTemplateRepository
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.data.mock.MockUserRepository
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
import java.util.UUID
import android.os.Build
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
    /** Null message: the UI shows the generic error from strings.xml. */
    data class Error(val message: String?) : AuthState
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

    /** First name for the success greeting; null until the mock user is available. */
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

    fun authenticateWithGoogle() = authenticate { authenticateUser.withGoogle() }
    fun authenticateWithApple() = authenticate { authenticateUser.withApple() }

    private fun authenticate(block: suspend () -> Result<User>) {
        viewModelScope.launch {
            _authState.value = AuthState.Authenticating
            block()
                .onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                    _currentStep.value = OnboardingStep.FIRST_SUBSCRIPTION
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message)
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
            val now = System.currentTimeMillis()
            val sub = Subscription(
                id = "sub_${UUID.randomUUID()}",
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
            val askNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !userPreferences.notificationPermissionRequested.first()
            _currentStep.value = if (askNotifications) OnboardingStep.NOTIFICATIONS else OnboardingStep.SUCCESS
        }
    }

    fun skipFirstSubscription() {
        _currentStep.value = OnboardingStep.SUCCESS
    }
}

// ── Factory ───────────────────────────────────────────────────────────────────

class OnboardingViewModelFactory(
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        OnboardingViewModel(
            authenticateUser = AuthenticateUserUseCase(MockAuthRepository()),
            createSubscription = CreateSubscriptionUseCase(MockSubscriptionRepository()),
            getCurrentUser = GetCurrentUserUseCase(MockUserRepository()),
            getPopularServices = GetPopularServicesUseCase(MockServiceTemplateRepository()),
            userPreferences = userPreferences
        ) as T
}
