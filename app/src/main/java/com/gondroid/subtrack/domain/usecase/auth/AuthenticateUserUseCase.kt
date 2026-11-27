package com.gondroid.subtrack.domain.usecase.auth

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.AuthRepository

class AuthenticateUserUseCase(
    private val repository: AuthRepository
) {
    suspend fun withGoogle(idToken: String): Result<User> = repository.signInWithGoogle(idToken)
}
