package com.gondroid.subtrack.domain.usecase.auth

import com.gondroid.subtrack.domain.model.GoogleCredential
import com.gondroid.subtrack.domain.repository.AuthRepository

class DeleteAccountUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(credential: GoogleCredential): Result<Unit> = repository.deleteAccount(credential)
}
