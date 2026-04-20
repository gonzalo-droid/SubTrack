package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class MockAuthRepository : AuthRepository {

    override suspend fun signInWithGoogle(): Result<User> {
        delay(1500)
        MockDataStore.currentUser.value = MockData.currentUser
        return Result.success(MockData.currentUser)
    }

    override suspend fun signInWithApple(): Result<User> {
        delay(1500)
        MockDataStore.currentUser.value = MockData.currentUser
        return Result.success(MockData.currentUser)
    }
}
