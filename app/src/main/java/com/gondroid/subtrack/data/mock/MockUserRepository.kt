package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockUserRepository @Inject constructor() : UserRepository {

    private val _user = MutableStateFlow<User?>(MockData.currentUser)

    override fun getCurrentUser(): Flow<User?> = _user

    override suspend fun updateUser(user: User) {
        _user.value = user
    }
}
