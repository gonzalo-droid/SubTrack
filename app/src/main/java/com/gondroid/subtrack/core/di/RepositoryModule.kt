package com.gondroid.subtrack.core.di

import com.gondroid.subtrack.data.mock.MockMemberRepository
import com.gondroid.subtrack.data.mock.MockPaymentRepository
import com.gondroid.subtrack.data.mock.MockReferralRepository
import com.gondroid.subtrack.data.mock.MockServiceTemplateRepository
import com.gondroid.subtrack.data.mock.MockSubscriptionRepository
import com.gondroid.subtrack.data.mock.MockTemplateRepository
import com.gondroid.subtrack.data.mock.MockUserAliasRepository
import com.gondroid.subtrack.data.mock.MockUserRepository
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.PaymentRepository
import com.gondroid.subtrack.domain.repository.ReferralRepository
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.TemplateRepository
import com.gondroid.subtrack.domain.repository.UserAliasRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindSubscriptionRepository(impl: MockSubscriptionRepository): SubscriptionRepository

    @Binds @Singleton
    abstract fun bindMemberRepository(impl: MockMemberRepository): MemberRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: MockUserRepository): UserRepository

    @Binds @Singleton
    abstract fun bindPaymentRepository(impl: MockPaymentRepository): PaymentRepository

    @Binds @Singleton
    abstract fun bindTemplateRepository(impl: MockTemplateRepository): TemplateRepository

    @Binds @Singleton
    abstract fun bindUserAliasRepository(impl: MockUserAliasRepository): UserAliasRepository

    @Binds @Singleton
    abstract fun bindReferralRepository(impl: MockReferralRepository): ReferralRepository

    @Binds @Singleton
    abstract fun bindServiceTemplateRepository(impl: MockServiceTemplateRepository): ServiceTemplateRepository
}
