package com.gondroid.subtrack.data.repository

import com.gondroid.subtrack.data.demo.DemoData

import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val store = AppStore.currentUser

    private val knownUsers = mapOf(
        "usr_gonzalo" to DemoData.currentUser,
        "usr_maria" to com.gondroid.subtrack.domain.model.User(
            id = "usr_maria", name = "María Rodríguez", phone = "+51 912 345 678",
            email = "maria@mail.com", isPro = false, referralCode = "", createdAt = 0L
        )
    )

    override fun getCurrentUser(): Flow<User?> = store

    override suspend fun updateUser(user: User): Result<Unit> {
        store.value = user
        return Result.success(Unit)
    }

    override suspend fun getUserById(userId: String): User? = knownUsers[userId]
}
