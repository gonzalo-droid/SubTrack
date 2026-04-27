package com.gondroid.subtrack.domain.usecase.user

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> = repository.updateUser(user)
}
