package com.gondroid.subtrack.feature.createsubscription

import com.gondroid.subtrack.domain.util.randomId

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SplitType

data class WizardMemberData(
    val id: String = randomId(),
    val name: String,
    val phone: String,
    val profileLabel: String? = null,
    val fromContact: Boolean = false
)

data class CreateSubscriptionFormState(
    /** Set when the wizard edits an existing subscription instead of creating one. */
    val editingId: String? = null,

    // Servicio
    val selectedService: ServiceTemplate? = null,
    val customServiceName: String = "",

    // Quién paga (null = todavía no elige)
    val isShared: Boolean? = null,

    // Detalles
    val totalAmount: String = "",
    val currency: String = "PEN",
    val cycle: BillingCycle = BillingCycle.MONTHLY,
    val cutoffDay: Int = 15,

    // Miembros
    val members: List<WizardMemberData> = emptyList(),

    // División
    val splitType: SplitType = SplitType.EQUAL,
    val memberShares: Map<String, Double> = emptyMap()
) {
    val resolvedServiceName: String get() =
        if (selectedService?.id == "tpl_other") customServiceName.trim()
        else selectedService?.name ?: ""

    val shared: Boolean get() = isShared == true

    val isEditing: Boolean get() = editingId != null

    val totalAmountDouble: Double get() = totalAmount.toDoubleOrNull() ?: 0.0

    val equalSharePerPerson: Double get() {
        val count = members.size + 1 // +1 for admin
        return if (count > 0) totalAmountDouble / count else 0.0
    }
}

enum class WizardStep { SERVICE, WHO_PAYS, DETAILS, MEMBERS, SPLIT }

/**
 * Steps the wizard walks through: 3 for a personal subscription, 5 for a shared one.
 * Editing only touches the details; members are managed from the detail screen.
 */
fun wizardSteps(isShared: Boolean, isEditing: Boolean = false): List<WizardStep> = when {
    isEditing -> listOf(WizardStep.DETAILS)
    isShared -> WizardStep.entries
    else -> listOf(WizardStep.SERVICE, WizardStep.WHO_PAYS, WizardStep.DETAILS)
}

/** Step after [current], or null when [current] is the last one (time to submit). */
fun nextWizardStep(current: WizardStep, isShared: Boolean, isEditing: Boolean = false): WizardStep? =
    wizardSteps(isShared, isEditing).let { it.getOrNull(it.indexOf(current) + 1) }

/** Step before [current], or null on the first step. */
fun previousWizardStep(current: WizardStep, isShared: Boolean, isEditing: Boolean = false): WizardStep? =
    wizardSteps(isShared, isEditing).let { it.getOrNull(it.indexOf(current) - 1) }

sealed interface CreateSubscriptionUiState {
    data object Idle : CreateSubscriptionUiState
    data object Submitting : CreateSubscriptionUiState
    data object Saved : CreateSubscriptionUiState
    data object NotFound : CreateSubscriptionUiState
    data class Success(val createdId: String, val isShared: Boolean, val serviceName: String, val memberCount: Int) : CreateSubscriptionUiState
    /** Generic failure; the UI shows common_error_generic, never the exception text. */
    data object Error : CreateSubscriptionUiState
}
