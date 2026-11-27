package com.gondroid.subtrack.platform

import com.gondroid.subtrack.domain.model.GoogleCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/** [AuthBridge] on the Firebase Android SDK; failures arrive as [RemoteFailure]. */
class FirebaseAuthBridge(private val auth: FirebaseAuth) : AuthBridge {

    override fun currentUserId(): String? = auth.currentUser?.uid

    override suspend fun signInWithGoogle(credential: GoogleCredential): AuthUser = firebaseCall {
        val user = auth.signInWithCredential(credential.toFirebase()).await().user
            ?: error("Firebase returned no user")
        AuthUser(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            phone = user.phoneNumber,
            photoUrl = user.photoUrl?.toString(),
            createdAt = user.metadata?.creationTimestamp
        )
    }

    override suspend fun reauthenticate(credential: GoogleCredential) = firebaseCall {
        val user = auth.currentUser ?: error("No signed-in user")
        user.reauthenticate(credential.toFirebase()).await()
        Unit
    }

    override suspend fun signOut() = auth.signOut()

    override suspend fun deleteCurrentUser() = firebaseCall {
        (auth.currentUser ?: error("No signed-in user")).delete().await()
        Unit
    }

    private fun GoogleCredential.toFirebase() = GoogleAuthProvider.getCredential(idToken, accessToken)
}
