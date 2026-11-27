package com.gondroid.subtrack.core.util

import android.util.Log

actual fun logError(tag: String, message: String, error: Throwable?) {
    Log.e(tag, message, error)
}
