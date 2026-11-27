package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.core.auth.AuthConfig
import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.data.demo.DemoData
import com.gondroid.subtrack.data.repository.AppStore
import com.gondroid.subtrack.domain.model.GoogleCredential
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.AuthRepository
import com.gondroid.subtrack.domain.util.currentTimeMillis
import com.gondroid.subtrack.platform.AuthBridge

class AuthRepositoryImpl(
    private val auth: AuthBridge,
    private val sync: FirestoreSync,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun signInWithGoogle(credential: GoogleCredential): Result<User> = runCatching {
        if (AuthConfig.useFakeAuth) {
            // E2E tests: the demo user, no Firebase and no sync
            return@runCatching DemoData.currentUser.also { AppStore.currentUser.value = it }
        }
        val account = auth.signInWithGoogle(credential)
        val user = User(
            id = account.uid,
            name = account.displayName.orEmpty(),
            phone = account.phone.orEmpty(),
            email = account.email,
            profileImageUrl = account.photoUrl,
            referralCode = account.uid.take(REFERRAL_CODE_LENGTH).uppercase(),
            createdAt = account.createdAt ?: currentTimeMillis()
        )
        sync.connect(user)
        AppStore.currentUser.value ?: user
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        sync.stop()
        auth.signOut()
        clearDevice()
    }

    override suspend fun deleteAccount(credential: GoogleCredential): Result<Unit> = runCatching {
        if (!AuthConfig.useFakeAuth) {
            val uid = auth.currentUserId() ?: error("No signed-in user")
            auth.reauthenticate(credential)
            sync.stop()
            // Cloud data first: the security rules need the account to still exist
            sync.deleteRemote(uid)
            auth.deleteCurrentUser()
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
