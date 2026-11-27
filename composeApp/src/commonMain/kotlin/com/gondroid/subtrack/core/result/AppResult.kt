package com.gondroid.subtrack.core.result

/** Data errors the UI knows how to explain (ARQ-03). */
sealed interface DataError {
    /** No connection: the user can retry once online. */
    data object Network : DataError
    data object Timeout : DataError
    data object NotFound : DataError
    data class Server(val code: Int) : DataError
    data class Unknown(val message: String?) : DataError
}

/** Result of an operation that can fail outside the app (network, Firebase). */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: DataError) : AppResult<Nothing>
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> = also { if (it is AppResult.Success) action(it.data) }

inline fun <T> AppResult<T>.onFailure(action: (DataError) -> Unit): AppResult<T> = also { if (it is AppResult.Failure) action(it.error) }

/** Errors worth a "check your connection" message instead of the generic one. */
val DataError.isConnectivity: Boolean get() = this is DataError.Network || this is DataError.Timeout
