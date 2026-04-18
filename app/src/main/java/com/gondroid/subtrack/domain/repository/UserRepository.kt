package com.gondroid.subtrack.domain.repository

import com.gondroid.subtrack.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun updateUser(user: User)
}
