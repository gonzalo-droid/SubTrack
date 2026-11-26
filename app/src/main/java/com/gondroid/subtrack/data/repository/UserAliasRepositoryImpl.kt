package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.repository.UserAliasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MockUserAliasRepository @Inject constructor() : UserAliasRepository {

    override fun getAlias(): Flow<UserAlias> = MockDataStore.userAlias

    override suspend fun updateAlias(alias: UserAlias): Result<Unit> {
        MockDataStore.userAlias.value = alias
        return Result.success(Unit)
    }
}
