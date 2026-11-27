package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.GoogleCredential
import com.gondroid.subtrack.domain.model.User

interface AuthRepository {
    /** Signs in with the Google account the user picked. */
    suspend fun signInWithGoogle(credential: GoogleCredential): Result<User>

    /** Ends the session and clears this device; the account data stays in the cloud. */
    suspend fun signOut(): Result<Unit>

    /**
     * Deletes the account, its cloud data and this device's data. [credential] is a fresh Google
     * sign-in: Firebase requires a recent login before deleting an account.
     */
    suspend fun deleteAccount(credential: GoogleCredential): Result<Unit>
}
