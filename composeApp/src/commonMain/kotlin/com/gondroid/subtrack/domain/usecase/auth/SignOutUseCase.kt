package com.gondroid.subtrack.domain.usecase.auth

import com.gondroid.subtrack.core.result.AppResult

import com.gondroid.subtrack.domain.repository.AuthRepository

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AppResult<Unit> = repository.signOut()
}
