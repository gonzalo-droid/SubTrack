package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.UserAlias
import kotlinx.coroutines.flow.Flow

interface UserAliasRepository {
    fun getAlias(): Flow<UserAlias>
    suspend fun updateAlias(alias: UserAlias): Result<Unit>
}
