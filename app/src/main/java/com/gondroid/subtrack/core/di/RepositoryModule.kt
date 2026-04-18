package com.gondroid.subtrack.core.di

import com.gondroid.subtrack.data.mock.MockMemberRepository
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.data.mock.MockUserRepository
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: MockSubscriptionRepository): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindMemberRepository(impl: MockMemberRepository): MemberRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: MockUserRepository): UserRepository
}
