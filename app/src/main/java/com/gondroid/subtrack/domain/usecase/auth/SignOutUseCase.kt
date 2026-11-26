package com.gondroid.subtrack.domain.usecase.auth

import com.gondroid.subtrack.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.signOut()
}
