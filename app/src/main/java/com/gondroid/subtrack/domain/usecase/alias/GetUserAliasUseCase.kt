package com.gondroid.subtrack.domain.usecase.alias

import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.repository.UserAliasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserAliasUseCase @Inject constructor(
    private val repository: UserAliasRepository
) {
    operator fun invoke(): Flow<UserAlias> = repository.getAlias()
}
