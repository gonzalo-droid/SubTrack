package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.User

interface AuthRepository {
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signInWithApple(): Result<User>
}
