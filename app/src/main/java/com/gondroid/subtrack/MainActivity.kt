package com.gondroid.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.navigation.BottomNavBar
import com.gondroid.subtrack.core.navigation.Route
import com.gondroid.subtrack.core.navigation.SubTrackNavHost
import com.gondroid.subtrack.feature.onboarding.OnboardingFlow
// TODO: [hilt] Add @AndroidEntryPoint when Gradle plugin is configured

private fun NavDestination?.isMainRoute(): Boolean =
    this?.hasRoute(Route.Dashboard::class) == true ||
    this?.hasRoute(Route.SubscriptionList::class) == true ||
    this?.hasRoute(Route.People::class) == true ||
    this?.hasRoute(Route.Profile::class) == true

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubTrackTheme {
                val appStateVm: AppStateViewModel = viewModel(
                    factory = AppStateViewModelFactory(applicationContext)
                )
                val appState by appStateVm.state.collectAsStateWithLifecycle()

                when (appState) {
                    AppReadyState.Loading -> SplashScreen()
                    AppReadyState.ShowOnboarding -> OnboardingFlow(
                        onComplete = appStateVm::markOnboardingComplete
                    )
                    AppReadyState.ShowMain -> MainApp()
                }
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
private fun MainApp() {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = currentDestination.isMainRoute()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgApp,
        bottomBar = { if (showBottomBar) BottomNavBar(navController) }
    ) { innerPadding ->
        SubTrackNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
