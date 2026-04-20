package com.gondroid.subtrack.feature.profile

import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.model.enums.PaymentAliasType

data class ProfileData(
    val user: User,
    val isPro: Boolean,
    val userAlias: UserAlias,
    val defaultAliasMethod: PaymentAliasType,
    val totalServices: Int,
    val totalMonthlySpend: Double,
    val totalMonthlyRecovered: Double,
    val templates: List<Template>,
    val activeReferrals: Int,
    val monthsProEarned: Int
)

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val data: ProfileData) : ProfileUiState
}
