package com.gondroid.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.navigation.BottomNavBar
import com.gondroid.subtrack.core.navigation.Route
import com.gondroid.subtrack.core.navigation.SubTrackNavHost
// TODO: [hilt] Add @AndroidEntryPoint when Gradle plugin is configured

private val bottomNavRoutes = setOf(
    Route.Dashboard::class.qualifiedName,
    Route.SubscriptionList::class.qualifiedName,
    Route.People::class.qualifiedName,
    Route.Profile::class.qualifiedName,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubTrackTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val showBottomBar = bottomNavRoutes.any { currentRoute?.contains(it ?: "") == true }

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
        }
    }
}
