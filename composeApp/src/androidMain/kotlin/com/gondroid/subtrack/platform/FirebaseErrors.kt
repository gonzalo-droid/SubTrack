package com.gondroid.subtrack.platform

import com.gondroid.subtrack.core.result.DataError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.coroutines.cancellation.CancellationException

/** Runs a Firebase call and rethrows its failure as a classified [RemoteFailure]. */
internal suspend fun <T> firebaseCall(block: suspend () -> T): T = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: FirebaseNetworkException) {
    throw RemoteFailure(DataError.Network, e)
} catch (e: FirebaseFirestoreException) {
    val error = when (e.code) {
        FirebaseFirestoreException.Code.UNAVAILABLE -> DataError.Network
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> DataError.Timeout
        FirebaseFirestoreException.Code.NOT_FOUND -> DataError.NotFound
        else -> DataError.Server(e.code.value())
    }
    throw RemoteFailure(error, e)
}
