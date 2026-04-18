package com.gondroid.subtrack.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary

private data class BottomNavItem(
    val route: Route,
    val icon: ImageVector,
    val labelRes: Int
)

private val bottomNavItems = listOf(
    BottomNavItem(Route.Dashboard, Icons.Outlined.Dashboard, R.string.nav_dashboard),
    BottomNavItem(Route.SubscriptionList, Icons.Outlined.CreditCard, R.string.nav_subscriptions),
    BottomNavItem(Route.People, Icons.Outlined.People, R.string.nav_people),
    BottomNavItem(Route.Profile, Icons.Outlined.Person, R.string.nav_profile),
)

@Composable
fun BottomNavBar(navController: NavController) {
    val backStack = navController.currentBackStackEntryAsState()
    val currentRoute = backStack.value?.destination?.route

    NavigationBar(containerColor = BgSurface, contentColor = TextPrimary) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute?.contains(item.route::class.qualifiedName ?: "") == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Route.Dashboard) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelRes)
                    )
                },
                label = { Text(stringResource(item.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBlue,
                    selectedTextColor = AccentBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
