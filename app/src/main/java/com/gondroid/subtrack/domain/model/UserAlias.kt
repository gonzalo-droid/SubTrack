package com.gondroid.subtrack.domain.model

import com.gondroid.subtrack.domain.model.enums.PaymentAliasType

data class UserAlias(
    val yape: String? = null,
    val plin: String? = null,
    val cciBank: String? = null,
    val cciNumber: String? = null,
    val defaultMethod: PaymentAliasType = PaymentAliasType.YAPE
)
