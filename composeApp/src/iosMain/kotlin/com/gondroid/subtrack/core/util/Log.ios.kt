package com.gondroid.subtrack.core.util

import platform.Foundation.NSLog

actual fun logError(tag: String, message: String, error: Throwable?) {
    // The text goes in as the format itself (with % escaped): Kotlin Strings as variadic %@ arguments crash
    val text = "E/$tag: $message" + (error?.let { " $it" } ?: "")
    NSLog(text.replace("%", "%%"))
}
