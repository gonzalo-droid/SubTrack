package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.core.result.AppResult
import com.gondroid.subtrack.core.result.DataError
import com.gondroid.subtrack.platform.RemoteFailure
import kotlin.coroutines.cancellation.CancellationException

/** The one place where exceptions become [DataError] (ARQ-03). */
internal fun Throwable.toDataError(): DataError = (this as? RemoteFailure)?.error ?: DataError.Unknown(message)

internal suspend inline fun <T> dataCall(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    AppResult.Failure(e.toDataError())
}
