package com.gondroid.subtrack.feature.profile.alias

import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.model.enums.PaymentAliasType

data class AliasEditData(
    val yape: String,
    val plin: String,
    val cciBank: String,
    val cciNumber: String,
    val defaultMethod: PaymentAliasType,
    val hasChanges: Boolean = false,
    val yapeError: String? = null,
    val plinError: String? = null,
    val cciError: String? = null
) {
    fun toUserAlias() = UserAlias(
        yape = yape.ifBlank { null },
        plin = plin.ifBlank { null },
        cciBank = cciBank.ifBlank { null },
        cciNumber = cciNumber.ifBlank { null },
        defaultMethod = defaultMethod
    )
}

sealed interface AliasEditUiState {
    data object Loading : AliasEditUiState
    data class Success(val data: AliasEditData) : AliasEditUiState
}
