package com.gondroid.subtrack.core.util

import platform.Foundation.NSLog

actual fun logError(tag: String, message: String, error: Throwable?) {
    NSLog("E/%@: %@ %@", tag, message, error?.toString() ?: "")
}
