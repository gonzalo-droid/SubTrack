package com.gondroid.subtrack.domain.usecase.onboarding

import com.gondroid.subtrack.core.preferences.UserPreferences

class MarkOnboardingCompleteUseCase(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke() = userPreferences.setOnboardingCompleted(true)
}
