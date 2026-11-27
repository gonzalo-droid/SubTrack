package com.gondroid.subtrack.platform

import com.gondroid.subtrack.core.result.DataError

/** Thrown by the platform bridges (Firebase on Android, Swift on iOS) with the error already classified. */
class RemoteFailure(val error: DataError, cause: Throwable? = null) : Exception(error.toString(), cause)
