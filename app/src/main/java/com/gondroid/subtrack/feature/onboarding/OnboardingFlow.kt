package com.gondroid.subtrack.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.AppStateViewModelFactory
import com.gondroid.subtrack.core.designsystem.animation.ONBOARDING_TRANSITION_MS
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.navigation.Route
import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.feature.onboarding.screens.AuthOnboardingScreen
import com.gondroid.subtrack.feature.onboarding.screens.FirstSubscriptionScreen
import com.gondroid.subtrack.feature.onboarding.screens.NotificationsPermissionScreen
import com.gondroid.subtrack.feature.onboarding.screens.OnboardingSuccessScreen
import com.gondroid.subtrack.feature.onboarding.screens.ValueInsightsScreen
import com.gondroid.subtrack.feature.onboarding.screens.ValueShareScreen
import com.gondroid.subtrack.feature.onboarding.screens.ValueTrackerScreen

@Composable
fun OnboardingFlow(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vm: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(UserPreferences.getInstance(context))
    )

    val step by vm.currentStep.collectAsStateWithLifecycle()
    val authState by vm.authState.collectAsStateWithLifecycle()
    val selectedService by vm.selectedService.collectAsStateWithLifecycle()
    val isCreating by vm.isCreatingSubscription.collectAsStateWithLifecycle()
    val createdServiceName by vm.createdServiceName.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = step,
        transitionSpec = {
            val forward = targetState.ordinal >= initialState.ordinal
            val enter = slideInHorizontally(
                initialOffsetX = { if (forward) it else -it },
                animationSpec = tween(ONBOARDING_TRANSITION_MS)
            ) + fadeIn(tween(ONBOARDING_TRANSITION_MS))
            val exit = slideOutHorizontally(
                targetOffsetX = { if (forward) -it else it },
                animationSpec = tween(ONBOARDING_TRANSITION_MS)
            ) + fadeOut(tween(ONBOARDING_TRANSITION_MS))
            enter togetherWith exit
        },
        label = "onboarding_step",
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
    ) { currentStep ->
        when (currentStep) {
            OnboardingStep.VALUE_TRACKER -> ValueTrackerScreen(
                onNext = vm::goToNextValue,
                onSkip = vm::skipToAuth,
                modifier = Modifier.fillMaxSize()
            )
            OnboardingStep.VALUE_SHARE -> ValueShareScreen(
                onNext = vm::goToNextValue,
                onSkip = vm::skipToAuth,
                modifier = Modifier.fillMaxSize()
            )
            OnboardingStep.VALUE_INSIGHTS -> ValueInsightsScreen(
                onNext = vm::goToNextValue,
                onSkip = vm::skipToAuth,
                modifier = Modifier.fillMaxSize()
            )
            OnboardingStep.AUTH -> AuthOnboardingScreen(
                authState = authState,
                onGoogleSignIn = vm::authenticateWithGoogle,
                onAppleSignIn = vm::authenticateWithApple,
                modifier = Modifier.fillMaxSize()
            )
            OnboardingStep.NOTIFICATIONS -> NotificationsPermissionScreen(
                onGrant = vm::grantNotifications,
                onSkip = vm::skipNotifications,
                modifier = Modifier.fillMaxSize()
            )
            OnboardingStep.FIRST_SUBSCRIPTION -> FirstSubscriptionScreen(
                popularServices = vm.popularServices,
                selectedService = selectedService,
                isCreating = isCreating,
                onSelectService = vm::selectService,
                onContinue = vm::createFirstSubscription,
                onSkip = vm::skipFirstSubscription,
                modifier = Modifier.fillMaxSize()
            )
            OnboardingStep.SUCCESS -> {
                val user = com.gondroid.subtrack.data.mock.MockDataStore.currentUser.collectAsStateWithLifecycle().value
                OnboardingSuccessScreen(
                    userName = user?.name?.substringBefore(" ") ?: "amigo",
                    createdServiceName = createdServiceName,
                    onAddAnother = onComplete,
                    onShareWithFriends = onComplete,
                    onGoHome = onComplete,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
