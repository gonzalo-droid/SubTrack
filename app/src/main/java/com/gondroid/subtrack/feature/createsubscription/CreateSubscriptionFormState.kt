package com.gondroid.subtrack.feature.createsubscription

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SplitType

data class WizardMemberData(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val profileLabel: String? = null,
    val fromContact: Boolean = false
)

data class CreateSubscriptionFormState(
    // Paso 1
    val selectedService: ServiceTemplate? = null,
    val customServiceName: String = "",

    // Paso 2
    val totalAmount: String = "",
    val currency: String = "PEN",
    val cycle: BillingCycle = BillingCycle.MONTHLY,
    val cutoffDay: Int = 15,

    // Paso 3
    val isShared: Boolean = false,
    val members: List<WizardMemberData> = emptyList(),

    // Paso 4
    val splitType: SplitType = SplitType.EQUAL,
    val memberShares: Map<String, Double> = emptyMap()
) {
    val resolvedServiceName: String get() =
        if (selectedService?.id == "tpl_other") customServiceName.trim()
        else selectedService?.name ?: ""

    val totalAmountDouble: Double get() = totalAmount.toDoubleOrNull() ?: 0.0

    val equalSharePerPerson: Double get() {
        val count = members.size + 1 // +1 for admin
        return if (count > 0) totalAmountDouble / count else 0.0
    }
}

enum class WizardStep { SERVICE, DETAILS, MEMBERS, SPLIT }

sealed interface CreateSubscriptionUiState {
    data object Idle : CreateSubscriptionUiState
    data object Submitting : CreateSubscriptionUiState
    data class Success(val createdId: String, val isShared: Boolean, val serviceName: String, val memberCount: Int) : CreateSubscriptionUiState
    data class Error(val message: String) : CreateSubscriptionUiState
}
