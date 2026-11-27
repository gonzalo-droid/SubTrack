package com.gondroid.subtrack.feature.onboarding

import app.cash.turbine.test
import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.core.preferences.createUserPrefsDataStore
import com.gondroid.subtrack.core.result.AppResult
import com.gondroid.subtrack.core.result.DataError
import com.gondroid.subtrack.domain.model.GoogleCredential
import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.repository.AuthRepository
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import com.gondroid.subtrack.domain.usecase.auth.AuthenticateUserUseCase
import com.gondroid.subtrack.domain.usecase.servicetemplate.GetPopularServicesUseCase
import com.gondroid.subtrack.domain.usecase.subscription.CreateSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import com.gondroid.subtrack.domain.util.randomId
import com.gondroid.subtrack.testing.UnusedSubscriptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.FileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** TST-05: the onboarding's sign-in states, observed with Turbine. */
class OnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val user = User(id = "u1", name = "Ana Pérez", phone = "", email = null, referralCode = "U1", createdAt = 0)

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private class FakeAuth(private val result: AppResult<User>) : AuthRepository {
        override suspend fun signInWithGoogle(credential: GoogleCredential) = result
        override suspend fun signOut(): AppResult<Unit> = AppResult.Success(Unit)
        override suspend fun deleteAccount(credential: GoogleCredential): AppResult<Unit> = AppResult.Success(Unit)
    }

    private inner class FakeUsers : UserRepository {
        override fun getCurrentUser() = MutableStateFlow<User?>(user)
        override suspend fun updateUser(user: User): Result<Unit> = Result.success(Unit)
        override suspend fun getUserById(userId: String): User? = null
    }

    private object FakeTemplates : ServiceTemplateRepository {
        override fun getPopularServices() = listOf(
            ServiceTemplate("tpl_netflix", "Netflix", "#E50914", "N", 54.9, SubscriptionCategory.STREAMING)
        )
    }

    private fun viewModel(signIn: AppResult<User>) = OnboardingViewModel(
        authenticateUser = AuthenticateUserUseCase(FakeAuth(signIn)),
        createSubscription = CreateSubscriptionUseCase(UnusedSubscriptions),
        getCurrentUser = GetCurrentUserUseCase(FakeUsers()),
        getPopularServices = GetPopularServicesUseCase(FakeTemplates),
        userPreferences = UserPreferences(
            createUserPrefsDataStore((FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "prefs-${randomId()}.preferences_pb").toString())
        )
    )

    @Test fun signInGoesFromAuthenticatingToTheFirstSubscription() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(user))
        vm.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            vm.authenticateWithGoogle(GoogleCredential("token"))
            assertEquals(AuthState.Authenticating, awaitItem())
            assertEquals(AuthState.Authenticated(user), awaitItem())
        }
        assertEquals(OnboardingStep.FIRST_SUBSCRIPTION, vm.currentStep.value)
    }

    @Test fun offlineSignInShowsAConnectivityErrorAndStaysOnAuth() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Failure(DataError.Network))
        vm.skipToAuth()
        vm.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            vm.authenticateWithGoogle(GoogleCredential("token"))
            assertEquals(AuthState.Authenticating, awaitItem())
            assertEquals(AuthState.Error(DataError.Network), awaitItem())
        }
        assertEquals(OnboardingStep.AUTH, vm.currentStep.value)
    }

    @Test fun restartGoesBackToTheFirstValueScreen() = runTest(dispatcher) {
        val vm = viewModel(AppResult.Success(user))
        vm.currentStep.test {
            assertEquals(OnboardingStep.VALUE_TRACKER, awaitItem())
            vm.goToNextValue()
            assertEquals(OnboardingStep.VALUE_SHARE, awaitItem())
            vm.restart()
            assertEquals(OnboardingStep.VALUE_TRACKER, awaitItem())
        }
    }
}
