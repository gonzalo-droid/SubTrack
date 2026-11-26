package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.User

interface AuthRepository {
    /** Signs in with the ID token of the Google account the user picked. */
    suspend fun signInWithGoogle(idToken: String): Result<User>
}
