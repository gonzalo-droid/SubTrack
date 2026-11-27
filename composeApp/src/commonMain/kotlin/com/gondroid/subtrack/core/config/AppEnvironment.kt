package com.gondroid.subtrack.core.config

/**
 * Build flags the shared code needs. The KMP library has no build types, so each platform's entry point
 * sets [isDebug] through `initKoin` before anything reads it.
 */
object AppEnvironment {
    var isDebug: Boolean = false
        internal set
}
