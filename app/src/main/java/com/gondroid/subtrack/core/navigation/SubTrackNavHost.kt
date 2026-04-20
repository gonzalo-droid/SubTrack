package com.gondroid.subtrack.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.gondroid.subtrack.feature.auth.AuthScreen
import com.gondroid.subtrack.feature.profile.debug.DebugScreen
import com.gondroid.subtrack.feature.createsubscription.CreateSubscriptionScreen
import com.gondroid.subtrack.feature.dashboard.DashboardScreen
import com.gondroid.subtrack.feature.onboarding.OnboardingScreen
import com.gondroid.subtrack.feature.people.PeopleScreen
import com.gondroid.subtrack.feature.profile.ProfileScreen
import com.gondroid.subtrack.feature.profile.alias.AliasEditScreen
import com.gondroid.subtrack.feature.profile.referral.ReferralScreen
import com.gondroid.subtrack.feature.profile.templates.EditTemplateScreen
import com.gondroid.subtrack.feature.profile.templates.TemplatesScreen
import com.gondroid.subtrack.feature.subscriptiondetail.SubscriptionDetailScreen
import com.gondroid.subtrack.feature.subscriptionlist.SubscriptionListScreen

@Composable
fun SubTrackNavHost(
    navController: NavHostController,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    startDestination: Route = Route.Dashboard
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {

        composable<Route.Onboarding> {
            OnboardingScreen(onFinish = { navController.navigate(Route.Auth) })
        }

        composable<Route.Auth> {
            AuthScreen(onAuthenticated = {
                navController.navigate(Route.Dashboard) {
                    popUpTo(Route.Onboarding) { inclusive = true }
                }
            })
        }

        composable<Route.Dashboard> {
            DashboardScreen(
                onNavigateToDetail = { id -> navController.navigate(Route.SubscriptionDetail(id)) },
                onNavigateToCreate = { navController.navigate(Route.CreateSubscription()) },
                onNavigateToNotifications = { /* TODO: [fase-futura] */ },
                onNavigateToProfile = { navController.navigate(Route.Profile) }
            )
        }

        composable<Route.SubscriptionList> {
            SubscriptionListScreen(
                onNavigateToDetail = { id -> navController.navigate(Route.SubscriptionDetail(id)) },
                onCreateSubscription = { navController.navigate(Route.CreateSubscription()) }
            )
        }

        composable<Route.People> {
            PeopleScreen(
                onNavigateToDetail = { id -> navController.navigate(Route.SubscriptionDetail(id)) }
            )
        }

        composable<Route.Profile> {
            ProfileScreen(
                onNavigateToTemplates = { navController.navigate(Route.Templates) },
                onNavigateToReferral = { navController.navigate(Route.Referral) },
                onNavigateToAliasEdit = { navController.navigate(Route.AliasEdit) },
                onNavigateToEditUser = { /* TODO: [fase-futura] EditUserScreen */ },
                onNavigateToEditTemplate = { id -> navController.navigate(Route.EditTemplate(id)) },
                onDebugOnboarding = {
                    navController.navigate(Route.Onboarding) {
                        popUpTo(Route.Dashboard) { inclusive = true }
                    }
                },
                onDebugData = { navController.navigate(Route.Debug) }
            )
        }

        composable<Route.AliasEdit> {
            AliasEditScreen(onBack = { navController.popBackStack() })
        }

        composable<Route.Debug> {
            DebugScreen(onBack = { navController.popBackStack() })
        }

        composable<Route.SubscriptionDetail> { entry ->
            val args = entry.toRoute<Route.SubscriptionDetail>()
            SubscriptionDetailScreen(
                subscriptionId = args.id,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.CreateSubscription> { entry ->
            val args = entry.toRoute<Route.CreateSubscription>()
            CreateSubscriptionScreen(
                onBack = { navController.popBackStack() },
                onCreated = { id ->
                    navController.navigate(Route.SubscriptionDetail(id)) {
                        popUpTo(Route.CreateSubscription()) { inclusive = true }
                    }
                },
                startWithShared = args.startWithShared
            )
        }

        composable<Route.Templates> {
            TemplatesScreen(
                onBack = { navController.popBackStack() },
                onEditTemplate = { id -> navController.navigate(Route.EditTemplate(id)) }
            )
        }

        composable<Route.EditTemplate> { entry ->
            val args = entry.toRoute<Route.EditTemplate>()
            EditTemplateScreen(
                templateId = args.id,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Referral> {
            ReferralScreen(onBack = { navController.popBackStack() })
        }
    }
}
