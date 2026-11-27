package com.gondroid.subtrack.platform

/**
 * The few Firestore operations the sync needs, so FirestoreSync stays common code (PLT-01).
 * Paths are slash-separated, e.g. `users/abc/subscriptions/sub_1`. Values are plain Kotlin:
 * String, Boolean, Long, Double, null, List and Map.
 */
interface RemoteDocuments {
    /** The document's data, or null when it does not exist. */
    suspend fun get(path: String): Map<String, Any?>?

    /** Every document of a collection, by document id. */
    suspend fun list(collectionPath: String): Map<String, Map<String, Any?>>

    /** Applies the writes atomically. Firestore takes at most 500 per call. */
    suspend fun commit(writes: List<RemoteWrite>)
}

sealed interface RemoteWrite {
    val path: String

    data class Set(override val path: String, val data: Map<String, Any?>) : RemoteWrite
    data class Delete(override val path: String) : RemoteWrite
}
