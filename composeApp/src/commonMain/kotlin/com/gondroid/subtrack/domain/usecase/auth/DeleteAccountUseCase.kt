package com.gondroid.subtrack.domain.usecase.auth

import com.gondroid.subtrack.domain.repository.AuthRepository

class DeleteAccountUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> = repository.deleteAccount(idToken)
}
