package com.gondroid.subtrack.core.config

import com.gondroid.subtrack.core.util.logError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named

/** Koin qualifier of the app-wide scope (ARQ-05): work that outlives any screen, like syncing. */
val APP_SCOPE = named("appScope")

/** One failing job is logged and skipped; it never cancels its siblings or crashes the app. */
fun createAppScope(): CoroutineScope = CoroutineScope(
    SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, e -> logError("AppScope", "Background work failed", e) }
)
