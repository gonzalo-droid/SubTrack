package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.core.auth.AuthConfig
import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.data.demo.DemoData
import com.gondroid.subtrack.data.repository.AppStore
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val sync: FirestoreSync,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        if (AuthConfig.useFakeAuth) {
            // E2E tests: the demo user, no Firebase and no sync
            return@runCatching DemoData.currentUser.also { AppStore.currentUser.value = it }
        }
        val firebaseUser = auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await().user
            ?: error("Firebase returned no user")
        val user = User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName.orEmpty(),
            phone = firebaseUser.phoneNumber.orEmpty(),
            email = firebaseUser.email,
            profileImageUrl = firebaseUser.photoUrl?.toString(),
            referralCode = firebaseUser.uid.take(REFERRAL_CODE_LENGTH).uppercase(),
            createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis()
        )
        sync.connect(user)
        AppStore.currentUser.value ?: user
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        sync.stop()
        auth.signOut()
        clearDevice()
    }

    override suspend fun deleteAccount(idToken: String): Result<Unit> = runCatching {
        if (!AuthConfig.useFakeAuth) {
            val user = auth.currentUser ?: error("No signed-in user")
            // Firebase only deletes an account after a recent sign-in
            user.reauthenticate(GoogleAuthProvider.getCredential(idToken, null)).await()
            sync.stop()
            // Cloud data first: the security rules need the account to still exist
            sync.deleteRemote(user.uid)
            user.delete().await()
        }
        clearDevice()
    }

    /** Empties the store (LocalStoreSync then clears Room) and sends the app back to the onboarding. */
    private suspend fun clearDevice() {
        AppStore.clear()
        userPreferences.setOnboardingCompleted(false)
    }

    private companion object {
        const val REFERRAL_CODE_LENGTH = 8
    }
}
