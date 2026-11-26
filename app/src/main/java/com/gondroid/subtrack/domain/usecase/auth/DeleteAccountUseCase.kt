package com.gondroid.subtrack.domain.usecase.auth

import com.gondroid.subtrack.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> = repository.deleteAccount(idToken)
}
