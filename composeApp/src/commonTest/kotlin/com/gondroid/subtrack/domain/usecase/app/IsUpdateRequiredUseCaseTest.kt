package com.gondroid.subtrack.domain.usecase.app

import com.gondroid.subtrack.domain.repository.AppConfigRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsUpdateRequiredUseCaseTest {

    private fun useCase(min: Int?) = IsUpdateRequiredUseCase(object : AppConfigRepository {
        override suspend fun minSupportedVersion(): Int? = min
    })

    @Test fun olderBuildMustUpdate() = runTest { assertTrue(useCase(min = 5)(installedVersion = 4)) }

    @Test fun sameOrNewerBuildKeepsWorking() = runTest {
        assertFalse(useCase(min = 5)(installedVersion = 5))
        assertFalse(useCase(min = 5)(installedVersion = 6))
    }

    @Test fun unknownMinimumNeverBlocks() = runTest { assertFalse(useCase(min = null)(installedVersion = 1)) }
}
