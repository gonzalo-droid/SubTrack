package com.gondroid.subtrack.platform

import com.gondroid.subtrack.domain.model.GoogleCredential

/** The signed-in account as Firebase Auth reports it. */
data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val phone: String?,
    val photoUrl: String?,
    /** Account creation time in epoch milliseconds, when Firebase knows it. */
    val createdAt: Long?
)

/** Firebase Auth, implemented with the native SDK on each platform (PLT-02). */
interface AuthBridge {
    /** The uid kept by the SDK from an earlier run, or null when signed out. */
    fun currentUserId(): String?

    suspend fun signInWithGoogle(credential: GoogleCredential): AuthUser

    /** Firebase only deletes an account after a recent sign-in. */
    suspend fun reauthenticate(credential: GoogleCredential)

    suspend fun signOut()

    suspend fun deleteCurrentUser()
}
