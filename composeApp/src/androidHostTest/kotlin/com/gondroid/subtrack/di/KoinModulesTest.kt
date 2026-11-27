package com.gondroid.subtrack.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

/**
 * Checks that every constructor in the Koin graph finds its dependencies, so a missing
 * definition fails here instead of crashing the app at launch or on a screen.
 */
class KoinModulesTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun everyDefinitionResolves() {
        module { includes(platformModule, dataModule, domainModule, uiModule) }.verify(
            // Supplied at runtime: the Android context and the navigation arguments of some ViewModels
            extraTypes = listOf(Context::class, SavedStateHandle::class, String::class, Boolean::class)
        )
    }
}
