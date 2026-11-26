package com.gondroid.subtrack.core.di

import com.gondroid.subtrack.data.repository.MemberRepositoryImpl
import com.gondroid.subtrack.data.repository.PaymentRepositoryImpl
import com.gondroid.subtrack.data.repository.ReferralRepositoryImpl
import com.gondroid.subtrack.data.repository.ServiceTemplateRepositoryImpl
import com.gondroid.subtrack.data.repository.SubscriptionRepositoryImpl
import com.gondroid.subtrack.data.repository.TemplateRepositoryImpl
import com.gondroid.subtrack.data.repository.UserAliasRepositoryImpl
import com.gondroid.subtrack.data.repository.UserRepositoryImpl
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
import com.gondroid.subtrack.domain.repository.AuthRepository
import com.gondroid.subtrack.data.remote.FirebaseAuthRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository

    @Binds @Singleton
    abstract fun bindMemberRepository(impl: MemberRepositoryImpl): MemberRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    @Binds @Singleton
    abstract fun bindTemplateRepository(impl: TemplateRepositoryImpl): TemplateRepository

    @Binds @Singleton
    abstract fun bindUserAliasRepository(impl: UserAliasRepositoryImpl): UserAliasRepository

    @Binds @Singleton
    abstract fun bindReferralRepository(impl: ReferralRepositoryImpl): ReferralRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds @Singleton
    abstract fun bindServiceTemplateRepository(impl: ServiceTemplateRepositoryImpl): ServiceTemplateRepository
}
