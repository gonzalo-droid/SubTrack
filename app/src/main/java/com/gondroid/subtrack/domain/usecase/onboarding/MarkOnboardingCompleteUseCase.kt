package com.gondroid.subtrack.domain.usecase.onboarding

import com.gondroid.subtrack.core.preferences.UserPreferences
import javax.inject.Inject

class MarkOnboardingCompleteUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke() = userPreferences.setOnboardingCompleted(true)
}
