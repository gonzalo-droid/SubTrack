package com.gondroid.subtrack.domain.usecase.alias

import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.repository.UserAliasRepository
import javax.inject.Inject

class UpdateUserAliasUseCase @Inject constructor(
    private val repository: UserAliasRepository
) {
    suspend operator fun invoke(alias: UserAlias): Result<Unit> = repository.updateAlias(alias)
}
