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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.gondroid.subtrack.core.auth.GoogleIdTokenProvider
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.animation.ONBOARDING_TRANSITION_MS
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.navigation.Route
import com.gondroid.subtrack.feature.onboarding.screens.AuthOnboardingScreen
import com.gondroid.subtrack.feature.onboarding.screens.FirstSubscriptionScreen
import com.gondroid.subtrack.feature.onboarding.screens.NotificationsPermissionScreen
import com.gondroid.subtrack.feature.onboarding.screens.OnboardingSuccessScreen
import com.gondroid.subtrack.feature.onboarding.screens.ValueInsightsScreen
import com.gondroid.subtrack.feature.onboarding.screens.ValueShareScreen
import com.gondroid.subtrack.feature.onboarding.screens.ValueTrackerScreen
import androidx.compose.ui.res.stringResource
import com.gondroid.subtrack.R
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingFlow(
    /** Finishes the onboarding; [then] is an optional screen to open over the dashboard. */
    onComplete: (then: Route?) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: OnboardingViewModel = hiltViewModel()

    val step by vm.currentStep.collectAsStateWithLifecycle()
    val authState by vm.authState.collectAsStateWithLifecycle()
    val selectedService by vm.selectedService.collectAsStateWithLifecycle()
    val isCreating by vm.isCreatingSubscription.collectAsStateWithLifecycle()
    val createdServiceName by vm.createdServiceName.collectAsStateWithLifecycle()
    val userFirstName by vm.userFirstName.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Each time the flow enters composition (first launch or after a sign-out) it starts over;
    // rememberSaveable keeps a rotation from restarting it.
    var started by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!started) {
            vm.restart()
            started = true
        }
    }
    val scope = rememberCoroutineScope()

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
                onGoogleSignIn = {
                    // The account picker needs the Activity, so it runs here; a closed picker does nothing
                    scope.launch {
                        GoogleIdTokenProvider.request(context)
                            .onSuccess { token -> token?.let(vm::authenticateWithGoogle) }
                            .onFailure { vm.onGoogleSignInFailed() }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            OnboardingStep.NOTIFICATIONS -> NotificationsPermissionScreen(
                onGrant = vm::onNotificationPromptDone,
                onSkip = vm::onNotificationPromptDone,
                progressStep = 1,
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
            OnboardingStep.SUCCESS -> OnboardingSuccessScreen(
                userName = userFirstName ?: stringResource(R.string.onboarding_success_fallback_name),
                createdServiceName = createdServiceName,
                onAddAnother = { onComplete(Route.CreateSubscription()) },
                onShareWithFriends = { onComplete(Route.CreateSubscription(startWithShared = true)) },
                onGoHome = { onComplete(null) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
