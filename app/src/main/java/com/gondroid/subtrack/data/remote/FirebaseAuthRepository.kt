package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.core.auth.AuthConfig
import com.gondroid.subtrack.data.mock.MockData
import com.gondroid.subtrack.data.mock.MockDataStore
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val sync: FirestoreSync
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        if (AuthConfig.useFakeAuth) {
            // E2E tests: the demo user, no Firebase and no sync
            return@runCatching MockData.currentUser.also { MockDataStore.currentUser.value = it }
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
        MockDataStore.currentUser.value ?: user
    }

    private companion object {
        const val REFERRAL_CODE_LENGTH = 8
    }
}
