package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.repository.UserAliasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserAliasRepositoryImpl @Inject constructor() : UserAliasRepository {

    override fun getAlias(): Flow<UserAlias> = AppStore.userAlias

    override suspend fun updateAlias(alias: UserAlias): Result<Unit> {
        AppStore.userAlias.value = alias
        return Result.success(Unit)
    }
}
