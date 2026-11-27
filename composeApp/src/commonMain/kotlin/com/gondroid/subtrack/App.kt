package com.gondroid.subtrack

import com.gondroid.subtrack.feature.update.UpdateRequiredScreen

import androidx.compose.foundation.background
import org.koin.compose.koinInject
import com.gondroid.subtrack.platform.ExternalActions
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger
import com.gondroid.subtrack.core.designsystem.components.feedback.STSnackbarHost
import com.gondroid.subtrack.core.designsystem.components.feedback.rememberSnackbarMessenger
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.navigation.BottomNavBar
import com.gondroid.subtrack.core.navigation.Route
import com.gondroid.subtrack.core.navigation.SubTrackNavHost
import com.gondroid.subtrack.feature.onboarding.OnboardingFlow
import org.koin.compose.viewmodel.koinViewModel

private fun NavDestination?.isMainRoute(): Boolean =
    this?.hasRoute(Route.Dashboard::class) == true ||
    this?.hasRoute(Route.SubscriptionList::class) == true ||
    this?.hasRoute(Route.People::class) == true ||
    this?.hasRoute(Route.Profile::class) == true

/** Root of the UI, shared by every platform's entry point. */
@Composable
fun App() {
    val externalActions = koinInject<ExternalActions>()
    val systemUriHandler = LocalUriHandler.current
    val uriHandler = remember(externalActions, systemUriHandler) { OwnPagesUriHandler(externalActions, systemUriHandler) }
    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        AppContent()
    }
}

/** LNK-01: our own pages open inside the app; everything else (WhatsApp, stores) goes to the system. */
private class OwnPagesUriHandler(
    private val externalActions: ExternalActions,
    private val system: UriHandler
) : UriHandler {
    override fun openUri(uri: String) {
        if (uri.startsWith(OWN_PAGES)) externalActions.openInAppBrowser(uri) else system.openUri(uri)
    }

    private companion object {
        const val OWN_PAGES = "https://nekodojo.app/"
    }
}

@Composable
private fun AppContent() {
    SubTrackTheme {
        val appStateVm: AppStateViewModel = koinViewModel()
        val appState by appStateVm.state.collectAsStateWithLifecycle()

        when (appState) {
            AppReadyState.Loading -> SplashScreen()
            AppReadyState.UpdateRequired -> UpdateRequiredScreen()
            AppReadyState.ShowOnboarding -> OnboardingFlow(
                onComplete = appStateVm::markOnboardingComplete
            )
            AppReadyState.ShowMain -> {
                val pendingRoute by appStateVm.pendingRoute.collectAsStateWithLifecycle()
                MainApp(pendingRoute = pendingRoute, onPendingRouteOpened = appStateVm::consumePendingRoute)
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(BgPage),
        contentAlignment = Alignment.Center
    ) {
        Text("SubTrack.", style = SubTrackType.displayM, color = TextPrimary)
    }
}

@Composable
private fun MainApp(pendingRoute: Route?, onPendingRouteOpened: () -> Unit) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = currentDestination.isMainRoute()

    val snackbarHostState = remember { SnackbarHostState() }
    val messenger = rememberSnackbarMessenger(snackbarHostState)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgApp,
        bottomBar = { if (showBottomBar) BottomNavBar(navController) },
        snackbarHost = { STSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        CompositionLocalProvider(LocalSnackbarMessenger provides messenger) {
            SubTrackNavHost(
                navController = navController,
                pendingRoute = pendingRoute,
                onPendingRouteOpened = onPendingRouteOpened,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
