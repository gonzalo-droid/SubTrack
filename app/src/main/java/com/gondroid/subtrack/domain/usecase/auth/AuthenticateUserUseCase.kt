package com.gondroid.subtrack.domain.usecase.auth

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.AuthRepository
import javax.inject.Inject

class AuthenticateUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun withGoogle(idToken: String): Result<User> = repository.signInWithGoogle(idToken)
}
