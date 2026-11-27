package com.gondroid.subtrack.core.auth

/**
 * Debug-only switch for the E2E tests: Maestro launches the app with the `fakeAuth` extra and
 * the sign-in skips Google/Firebase (they can't drive the real account picker). Release builds
 * never turn it on: the entry points only read the switch in debug builds.
 */
object AuthConfig {
    const val FAKE_AUTH_EXTRA = "fakeAuth"

    /** Token returned in fake-auth mode; the auth repository never sends it to Firebase. */
    const val FAKE_TOKEN = "fake-token"

    var useFakeAuth: Boolean = false
}
