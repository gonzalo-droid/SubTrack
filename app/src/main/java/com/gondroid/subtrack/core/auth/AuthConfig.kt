package com.gondroid.subtrack.core.auth

/**
 * Debug-only switch for the E2E tests: Maestro launches the app with the `fakeAuth` extra and
 * the sign-in skips Google/Firebase (they can't drive the real account picker). Release builds
 * never turn it on: MainActivity only reads the extra when BuildConfig.DEBUG.
 */
object AuthConfig {
    const val FAKE_AUTH_EXTRA = "fakeAuth"

    @Volatile
    var useFakeAuth: Boolean = false
}
