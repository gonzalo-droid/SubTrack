package com.gondroid.subtrack.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Onboarding : Route
    @Serializable data object Auth : Route

    @Serializable data object Dashboard : Route
    @Serializable data object SubscriptionList : Route
    @Serializable data object People : Route
    @Serializable data object Profile : Route

    @Serializable data class SubscriptionDetail(val id: String) : Route
    @Serializable data class CreateSubscription(val startWithShared: Boolean = false) : Route

    @Serializable data object AliasEdit : Route
    @Serializable data object Templates : Route
    @Serializable data class EditTemplate(val id: String? = null) : Route
    @Serializable data object Referral : Route

    @Serializable data object Debug : Route
}
