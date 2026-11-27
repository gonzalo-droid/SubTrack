package com.gondroid.subtrack.core.util

/** Error log with a fixed message (OBS-04: never personal data). Logcat on Android, the system log on iOS. */
expect fun logError(tag: String, message: String, error: Throwable? = null)
