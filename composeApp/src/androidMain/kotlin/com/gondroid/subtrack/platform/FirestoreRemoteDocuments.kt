package com.gondroid.subtrack.platform

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** [RemoteDocuments] on the Firebase Android SDK; failures arrive as [RemoteFailure]. */
class FirestoreRemoteDocuments(private val db: FirebaseFirestore) : RemoteDocuments {

    override suspend fun get(path: String): Map<String, Any?>? = firebaseCall {
        val snapshot = db.document(path).get().await()
        // A document without fields still exists: empty map, not null
        if (snapshot.exists()) snapshot.data.orEmpty() else null
    }

    override suspend fun list(collectionPath: String): Map<String, Map<String, Any?>> = firebaseCall {
        db.collection(collectionPath).get().await().documents.associate { it.id to it.data.orEmpty() }
    }

    override suspend fun commit(writes: List<RemoteWrite>) {
        if (writes.isEmpty()) return
        firebaseCall {
            val batch = db.batch()
            writes.forEach { write ->
                when (write) {
                    is RemoteWrite.Set -> batch.set(db.document(write.path), write.data)
                    is RemoteWrite.Delete -> batch.delete(db.document(write.path))
                }
            }
            batch.commit().await()
        }
    }
}
