package com.gondroid.subtrack.feature.createsubscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.model.enums.SplitType
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.usecase.servicetemplate.GetPopularServicesUseCase
import com.gondroid.subtrack.domain.usecase.subscription.CreateSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionDetailUseCase
import com.gondroid.subtrack.domain.usecase.subscription.UpdateSubscriptionDetailsUseCase
import com.gondroid.subtrack.core.preferences.UserPreferences
import java.util.Locale
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltViewModel(assistedFactory = CreateSubscriptionViewModel.Factory::class)
class CreateSubscriptionViewModel @AssistedInject constructor(
    @Assisted startWithShared: Boolean,
    @Assisted editSubscriptionId: String?,
    private val createSubscription: CreateSubscriptionUseCase,
    private val getPopularServices: GetPopularServicesUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getSubscriptionDetail: GetSubscriptionDetailUseCase,
    private val updateSubscriptionDetails: UpdateSubscriptionDetailsUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(startWithShared: Boolean, editSubscriptionId: String?): CreateSubscriptionViewModel
    }


    /** D2: the notification permission is asked right after the first subscription is created. */
    /** Null until DataStore answers, so the success screen doesn't flash before the prompt. */
    val askNotificationPermission: StateFlow<Boolean?> = userPreferences.notificationPermissionRequested
        .map<Boolean, Boolean?> { requested -> !requested }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private var startsShared = false

    fun onNotificationPromptDone() {
        viewModelScope.launch { userPreferences.setNotificationPermissionRequested(true) }
    }

    private val _form = MutableStateFlow(CreateSubscriptionFormState())
    val formState: StateFlow<CreateSubscriptionFormState> = _form.asStateFlow()

    private val _currentStep = MutableStateFlow(WizardStep.SERVICE)
    val currentStep: StateFlow<WizardStep> = _currentStep.asStateFlow()

    private val _uiState = MutableStateFlow<CreateSubscriptionUiState>(CreateSubscriptionUiState.Idle)
    val uiState: StateFlow<CreateSubscriptionUiState> = _uiState.asStateFlow()

    val popularServices: List<ServiceTemplate> = getPopularServices()

    val totalSteps: StateFlow<Int> = _form
        .map { wizardSteps(it.shared, it.isEditing).size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, wizardSteps(false).size)

    val currentStepIndex: StateFlow<Int> = _form
        .combine(_currentStep) { form, step -> wizardSteps(form.shared, form.isEditing).indexOf(step) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val isCurrentStepValid: StateFlow<Boolean> = _form
        .combine(_currentStep) { form, step ->
            when (step) {
                WizardStep.SERVICE -> {
                    val s = form.selectedService
                    s != null && (s.id != "tpl_other" || form.customServiceName.isNotBlank())
                }
                WizardStep.WHO_PAYS -> form.isShared != null
                WizardStep.DETAILS -> {
                    val amount = form.totalAmount.toDoubleOrNull()
                    amount != null && amount > 0 && form.cutoffDay in 1..31
                }
                WizardStep.MEMBERS -> form.members.isNotEmpty()
                WizardStep.SPLIT -> {
                    when (form.splitType) {
                        SplitType.EQUAL -> true
                        SplitType.PERCENTAGE -> {
                            val sum = form.memberShares.values.sum()
                            abs(sum - 100.0) < 0.5
                        }
                        SplitType.FIXED -> {
                            val sum = form.memberShares.values.sum()
                            sum <= form.totalAmountDouble + 0.01
                        }
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private fun start(startWithShared: Boolean, editSubscriptionId: String?) {
        startsShared = startWithShared
        if (startWithShared) _form.update { it.copy(isShared = true) }
        if (editSubscriptionId != null) loadForEdit(editSubscriptionId)
    }

    private fun loadForEdit(id: String) {
        // Edit mode starts right away so the screen never looks like (or acts as) a create.
        _form.value = CreateSubscriptionFormState(editingId = id)
        _currentStep.value = WizardStep.DETAILS
        viewModelScope.launch {
            val sub = getSubscriptionDetail(id).first()
            if (sub == null) {
                _uiState.value = CreateSubscriptionUiState.NotFound
                return@launch
            }
            _form.value = CreateSubscriptionFormState(
                editingId = sub.id,
                selectedService = ServiceTemplate(
                    id = sub.serviceTemplateId ?: sub.id,
                    name = sub.name,
                    brandColor = sub.brandColor,
                    defaultLogoChar = sub.name.take(1),
                    category = sub.category
                ),
                isShared = sub.isShared,
                totalAmount = String.format(Locale.US, "%.2f", sub.totalAmount),
                currency = sub.currency,
                cycle = sub.cycle,
                cutoffDay = sub.cutoffDay
            )
        }
    }

    // ── Step 1 ───────────────────────────────────────────────────────────────

    fun selectService(template: ServiceTemplate) {
        _form.update {
            it.copy(
                selectedService = template,
                customServiceName = "",
                isShared = it.isShared ?: true.takeIf { template.suggestsShared }
            )
        }
    }

    fun updateCustomServiceName(name: String) {
        _form.update { it.copy(customServiceName = name) }
    }

    fun updateCustomServiceColor(color: String) {
        val s = _form.value.selectedService ?: return
        _form.update { it.copy(selectedService = s.copy(brandColor = color)) }
    }

    // ── Who pays ─────────────────────────────────────────────────────────────

    fun setShared(isShared: Boolean) {
        _form.update { it.copy(isShared = isShared) }
    }

    // ── Details ──────────────────────────────────────────────────────────────

    fun updateAmount(amount: String) {
        _form.update { it.copy(totalAmount = amount) }
    }

    fun applySuggestedAmount() {
        val suggested = _form.value.selectedService?.suggestedMonthly ?: return
        _form.update { it.copy(totalAmount = String.format(Locale.US, "%.2f", suggested)) }
    }

    fun updateCurrency(currency: String) {
        _form.update { it.copy(currency = currency) }
    }

    fun updateCycle(cycle: BillingCycle) {
        _form.update { it.copy(cycle = cycle) }
    }

    fun updateCutoffDay(day: Int) {
        _form.update { it.copy(cutoffDay = day.coerceIn(1, 31)) }
    }

    // ── Members ──────────────────────────────────────────────────────────────

    fun addMember(member: WizardMemberData) {
        _form.update { it.copy(members = it.members + member) }
    }

    fun removeMember(memberId: String) {
        _form.update { it.copy(members = it.members.filter { m -> m.id != memberId }) }
    }

    // ── Split ────────────────────────────────────────────────────────────────

    fun selectSplitType(type: SplitType) {
        val form = _form.value
        val shares: Map<String, Double> = when (type) {
            SplitType.EQUAL -> emptyMap()
            SplitType.PERCENTAGE -> {
                val perPerson = if (form.members.isNotEmpty()) 100.0 / (form.members.size + 1) else 0.0
                form.members.associate { it.id to perPerson }
            }
            SplitType.FIXED -> {
                val amount = form.totalAmountDouble / (form.members.size + 1)
                form.members.associate { it.id to amount }
            }
        }
        _form.update { it.copy(splitType = type, memberShares = shares) }
    }

    fun updateMemberShare(memberId: String, value: Double) {
        _form.update { form ->
            form.copy(memberShares = form.memberShares + (memberId to value))
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    fun nextStep() {
        val form = _form.value
        val next = nextWizardStep(_currentStep.value, form.shared, form.isEditing)
        when {
            next != null -> _currentStep.value = next
            form.isEditing -> saveEdit()
            else -> submit()
        }
    }

    fun previousStep() {
        val form = _form.value
        previousWizardStep(_currentStep.value, form.shared, form.isEditing)?.let { _currentStep.value = it }
    }

    private fun saveEdit() {
        val form = _form.value
        val id = form.editingId ?: return
        _uiState.value = CreateSubscriptionUiState.Submitting
        viewModelScope.launch {
            updateSubscriptionDetails(id, form.totalAmountDouble, form.currency, form.cycle, form.cutoffDay)
                .onSuccess { _uiState.value = CreateSubscriptionUiState.Saved }
                .onFailure { _uiState.value = CreateSubscriptionUiState.Error(it.message) }
        }
    }

    // ── Submit ───────────────────────────────────────────────────────────────

    fun submit() {
        val form = _form.value
        _uiState.value = CreateSubscriptionUiState.Submitting
        viewModelScope.launch {
            val subscriptionId = "sub_${System.currentTimeMillis()}"
            val totalAmount = form.totalAmountDouble
            val ownerId = getCurrentUser().first()?.id.orEmpty()

            val members = if (form.shared) {
                form.members.map { m ->
                    val share = when (form.splitType) {
                        SplitType.EQUAL -> totalAmount / (form.members.size + 1)
                        SplitType.PERCENTAGE -> (form.memberShares[m.id] ?: 0.0) / 100.0 * totalAmount
                        SplitType.FIXED -> form.memberShares[m.id] ?: 0.0
                    }
                    Member(
                        id = m.id,
                        userId = null,
                        name = m.name,
                        phone = m.phone,
                        profileLabel = m.profileLabel,
                        shareAmount = share,
                        isArchived = false,
                        currentStatus = PaymentStatus.PENDING,
                        joinedAt = System.currentTimeMillis()
                    )
                }
            } else emptyList()

            val subscription = Subscription(
                id = subscriptionId,
                name = form.resolvedServiceName,
                logoUrl = null,
                brandColor = form.selectedService?.brandColor ?: "#888888",
                serviceTemplateId = form.selectedService?.id?.takeIf { it != "tpl_other" },
                totalAmount = totalAmount,
                currency = form.currency,
                cycle = form.cycle,
                cutoffDay = form.cutoffDay,
                ownerId = ownerId,
                isShared = form.shared,
                splitType = form.splitType,
                category = form.selectedService?.category ?: SubscriptionCategory.OTHER,
                members = members,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            createSubscription(subscription)
                .onSuccess { id ->
                    _uiState.value = CreateSubscriptionUiState.Success(
                        createdId = id,
                        isShared = form.shared,
                        serviceName = form.resolvedServiceName,
                        memberCount = members.size
                    )
                }
                .onFailure { _uiState.value = CreateSubscriptionUiState.Error(it.message) }
        }
    }

    /** The screen showed the error; go back to Idle so it isn't shown again (e.g. on rotation). */
    fun errorShown() {
        if (_uiState.value is CreateSubscriptionUiState.Error) _uiState.value = CreateSubscriptionUiState.Idle
    }

    fun resetWizard() {
        // "Crear otra" keeps the shared mode the wizard was opened with
        _form.value = CreateSubscriptionFormState(isShared = true.takeIf { startsShared })
        _currentStep.value = WizardStep.SERVICE
        _uiState.value = CreateSubscriptionUiState.Idle
    }

    // Last in the body so every property above is initialized first
    init {
        start(startWithShared, editSubscriptionId)
    }
}
