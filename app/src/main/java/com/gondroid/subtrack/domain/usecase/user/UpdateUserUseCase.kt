package com.gondroid.subtrack.domain.usecase.user

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.UserRepository

class UpdateUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> = repository.updateUser(user)
}
