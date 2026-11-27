package com.gondroid.subtrack.domain.usecase.user

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<User?> = repository.getCurrentUser()
}
