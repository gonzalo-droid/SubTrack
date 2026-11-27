package com.gondroid.subtrack.feature.createsubscription

import kotlinx.coroutines.cancel

import androidx.lifecycle.viewModelScope

import androidx.lifecycle.ViewModel

import app.cash.turbine.test
import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.core.preferences.createUserPrefsDataStore
import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import com.gondroid.subtrack.domain.usecase.servicetemplate.GetPopularServicesUseCase
import com.gondroid.subtrack.domain.usecase.subscription.CreateSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionDetailUseCase
import com.gondroid.subtrack.domain.usecase.subscription.UpdateSubscriptionDetailsUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import com.gondroid.subtrack.domain.util.randomId
import com.gondroid.subtrack.testing.UnusedSubscriptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.FileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** TST-05: the create wizard's steps and submit, observed with Turbine. */
class CreateSubscriptionViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val netflix = ServiceTemplate("tpl_netflix", "Netflix", "#E50914", "N", 54.9, SubscriptionCategory.STREAMING)
    private val spotifyFamily = ServiceTemplate("tpl_spotify", "Spotify", "#1DB954", "S", 26.9, SubscriptionCategory.MUSIC, suggestsShared = true)
    private val createdSubs = mutableListOf<Subscription>()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    private fun <T : ViewModel> track(vm: T): T = vm.also { created += it }
    private val created = mutableListOf<ViewModel>()

    // Main stays on a test dispatcher (each setUp installs a fresh one): DataStore resumes cancelled
    // ViewModel work on Main from its IO thread, and a reset Main would crash the next test
    @AfterTest fun tearDown() = created.forEach { it.viewModelScope.cancel() }

    private inner class RecordingSubs : SubscriptionRepository by UnusedSubscriptions {
        override suspend fun createSubscription(subscription: Subscription): Result<String> {
            createdSubs += subscription
            return Result.success(subscription.id)
        }
    }

    private object Users : UserRepository {
        override fun getCurrentUser() = MutableStateFlow<User?>(User("me", "Ana", "", null, referralCode = "ME", createdAt = 0))
        override suspend fun updateUser(user: User): Result<Unit> = Result.success(Unit)
        override suspend fun getUserById(userId: String): User? = null
    }

    private fun viewModel(): CreateSubscriptionViewModel {
        val subs = RecordingSubs()
        return track(CreateSubscriptionViewModel(
            startWithShared = false,
            editSubscriptionId = null,
            createSubscription = CreateSubscriptionUseCase(subs),
            getPopularServices = GetPopularServicesUseCase(object : ServiceTemplateRepository {
                override fun getPopularServices() = listOf(netflix, spotifyFamily)
                override fun getOtherService() = ServiceTemplate("tpl_other", "Otro", "#888888", "+", null, SubscriptionCategory.OTHER, isPopular = false)
            }),
            getCurrentUser = GetCurrentUserUseCase(Users),
            getSubscriptionDetail = GetSubscriptionDetailUseCase(subs),
            updateSubscriptionDetails = UpdateSubscriptionDetailsUseCase(subs),
            userPreferences = UserPreferences(
                createUserPrefsDataStore((FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "prefs-${randomId()}.preferences_pb").toString())
            )
        ))
    }

    @Test fun personalSubscriptionTakesThreeStepsAndIsCreatedForMe() = runTest(dispatcher) {
        val vm = viewModel()
        vm.currentStep.test {
            assertEquals(WizardStep.SERVICE, awaitItem())
            vm.selectService(netflix)
            vm.nextStep()
            assertEquals(WizardStep.WHO_PAYS, awaitItem())
            vm.setShared(false)
            vm.nextStep()
            assertEquals(WizardStep.DETAILS, awaitItem())
        }
        vm.updateAmount("54.90")
        vm.uiState.test {
            assertEquals(CreateSubscriptionUiState.Idle, awaitItem())
            vm.nextStep() // last step: submits
            assertEquals(CreateSubscriptionUiState.Submitting, awaitItem())
            val success = assertIs<CreateSubscriptionUiState.Success>(awaitItem())
            assertEquals("Netflix", success.serviceName)
        }
        assertEquals(3, vm.totalSteps.value)
        assertEquals(listOf("me"), createdSubs.map { it.ownerId })
        assertEquals(54.9, createdSubs.single().totalAmount)
    }

    @Test fun familyPlansStartAsShared() = runTest(dispatcher) {
        val vm = viewModel()
        vm.selectService(spotifyFamily)
        assertEquals(true, vm.formState.value.isShared)
    }
}
