package com.gondroid.subtrack.di

import com.gondroid.subtrack.core.config.createAppScope

import com.gondroid.subtrack.core.config.APP_SCOPE

import com.gondroid.subtrack.domain.usecase.app.IsUpdateRequiredUseCase

import com.gondroid.subtrack.domain.repository.AppConfigRepository

import com.gondroid.subtrack.data.remote.AppConfigRepositoryImpl

import com.gondroid.subtrack.core.preferences.UserPreferences
import com.gondroid.subtrack.data.local.LocalStoreSync
import com.gondroid.subtrack.data.local.SubTrackDatabase
import com.gondroid.subtrack.data.remote.AuthRepositoryImpl
import com.gondroid.subtrack.data.remote.FirestoreSync
import com.gondroid.subtrack.data.repository.MemberRepositoryImpl
import com.gondroid.subtrack.data.repository.PaymentRepositoryImpl
import com.gondroid.subtrack.data.repository.ReferralRepositoryImpl
import com.gondroid.subtrack.data.repository.ServiceTemplateRepositoryImpl
import com.gondroid.subtrack.data.repository.SubscriptionRepositoryImpl
import com.gondroid.subtrack.data.repository.TemplateRepositoryImpl
import com.gondroid.subtrack.data.repository.UserAliasRepositoryImpl
import com.gondroid.subtrack.data.repository.UserRepositoryImpl
import com.gondroid.subtrack.domain.repository.AuthRepository
import com.gondroid.subtrack.domain.repository.MemberRepository
import com.gondroid.subtrack.domain.repository.PaymentRepository
import com.gondroid.subtrack.domain.repository.ReferralRepository
import com.gondroid.subtrack.domain.repository.ServiceTemplateRepository
import com.gondroid.subtrack.domain.repository.SubscriptionRepository
import com.gondroid.subtrack.domain.repository.TemplateRepository
import com.gondroid.subtrack.domain.repository.UserAliasRepository
import com.gondroid.subtrack.domain.repository.UserRepository
import com.gondroid.subtrack.domain.usecase.alias.GetUserAliasUseCase
import com.gondroid.subtrack.domain.usecase.alias.UpdateUserAliasUseCase
import com.gondroid.subtrack.domain.usecase.auth.AuthenticateUserUseCase
import com.gondroid.subtrack.domain.usecase.auth.DeleteAccountUseCase
import com.gondroid.subtrack.domain.usecase.auth.SignOutUseCase
import com.gondroid.subtrack.domain.usecase.member.AddMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.ApproveExitRequestUseCase
import com.gondroid.subtrack.domain.usecase.member.ArchiveMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.GetAllPeopleWithSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.member.GetArchivedMembersUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMemberPaymentHistoryUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMemberSubscriptionDetailUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMembersForSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.member.GetMyDebtsUseCase
import com.gondroid.subtrack.domain.usecase.member.MarkMemberAsPaidUseCase
import com.gondroid.subtrack.domain.usecase.member.MarkOwnPaymentUseCase
import com.gondroid.subtrack.domain.usecase.member.RejectExitRequestUseCase
import com.gondroid.subtrack.domain.usecase.member.RemoveMemberUseCase
import com.gondroid.subtrack.domain.usecase.member.RequestExitUseCase
import com.gondroid.subtrack.domain.usecase.member.RequestGroupExitUseCase
import com.gondroid.subtrack.domain.usecase.member.SendReminderUseCase
import com.gondroid.subtrack.domain.usecase.member.UpdateMemberUseCase
import com.gondroid.subtrack.domain.usecase.payment.GetPaymentHistoryUseCase
import com.gondroid.subtrack.domain.usecase.referral.GetRecentReferralsUseCase
import com.gondroid.subtrack.domain.usecase.referral.GetReferralInfoUseCase
import com.gondroid.subtrack.domain.usecase.servicetemplate.GetPopularServicesUseCase
import com.gondroid.subtrack.domain.usecase.subscription.CreateSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.subscription.DeleteSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.subscription.ExportSubscriptionsCsvUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetAllSubscriptionsAlphabeticalUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetDebtorsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetInsightsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionDetailUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionStatsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.GetUpcomingCutoffsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.PauseSubscriptionUseCase
import com.gondroid.subtrack.domain.usecase.subscription.SearchSubscriptionsUseCase
import com.gondroid.subtrack.domain.usecase.subscription.UpdateSubscriptionDetailsUseCase
import com.gondroid.subtrack.domain.usecase.template.DeleteTemplateUseCase
import com.gondroid.subtrack.domain.usecase.template.DuplicateTemplateUseCase
import com.gondroid.subtrack.domain.usecase.template.GetAllTemplatesUseCase
import com.gondroid.subtrack.domain.usecase.template.GetTemplateByIdUseCase
import com.gondroid.subtrack.domain.usecase.template.SaveTemplateUseCase
import com.gondroid.subtrack.domain.usecase.user.GetCurrentUserUseCase
import com.gondroid.subtrack.domain.usecase.user.GetUserProfileUseCase
import com.gondroid.subtrack.domain.usecase.user.UpdateUserUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import com.gondroid.subtrack.AppStateViewModel
import com.gondroid.subtrack.feature.createsubscription.CreateSubscriptionViewModel
import com.gondroid.subtrack.feature.dashboard.DashboardViewModel
import com.gondroid.subtrack.feature.onboarding.OnboardingViewModel
import com.gondroid.subtrack.feature.people.PeopleViewModel
import com.gondroid.subtrack.feature.profile.ProfileViewModel
import com.gondroid.subtrack.feature.profile.alias.AliasEditViewModel
import com.gondroid.subtrack.feature.profile.editprofile.EditProfileViewModel
import com.gondroid.subtrack.feature.profile.referral.ReferralViewModel
import com.gondroid.subtrack.feature.profile.settings.SettingsViewModel
import com.gondroid.subtrack.feature.profile.templates.EditTemplateViewModel
import com.gondroid.subtrack.feature.profile.templates.TemplatesViewModel
import com.gondroid.subtrack.feature.subscriptiondetail.SubscriptionDetailRouterViewModel
import com.gondroid.subtrack.feature.subscriptiondetail.admin.AdminDetailViewModel
import com.gondroid.subtrack.feature.subscriptiondetail.history.MemberHistoryViewModel
import com.gondroid.subtrack.feature.subscriptiondetail.member.MemberDetailViewModel
import com.gondroid.subtrack.feature.subscriptionlist.SubscriptionListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * ARQ-04: Koin in four modules. [platformModule] gives each platform's database, DataStore and
 * Firebase bridges; data and domain are common; the UI module joins them at the entry point.
 */
expect val platformModule: Module

val dataModule = module {
    single { get<SubTrackDatabase>().snapshotDao() }
    singleOf(::UserPreferences)
    single(APP_SCOPE) { createAppScope() }
    single { LocalStoreSync(get(), get(APP_SCOPE)) }
    single { FirestoreSync(get(), get(APP_SCOPE)) }
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::AppConfigRepositoryImpl) { bind<AppConfigRepository>() }
    singleOf(::MemberRepositoryImpl) { bind<MemberRepository>() }
    singleOf(::PaymentRepositoryImpl) { bind<PaymentRepository>() }
    singleOf(::ReferralRepositoryImpl) { bind<ReferralRepository>() }
    singleOf(::ServiceTemplateRepositoryImpl) { bind<ServiceTemplateRepository>() }
    singleOf(::SubscriptionRepositoryImpl) { bind<SubscriptionRepository>() }
    singleOf(::TemplateRepositoryImpl) { bind<TemplateRepository>() }
    singleOf(::UserAliasRepositoryImpl) { bind<UserAliasRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}

val domainModule = module {
    factoryOf(::IsUpdateRequiredUseCase)
    factoryOf(::GetUserAliasUseCase)
    factoryOf(::UpdateUserAliasUseCase)
    factoryOf(::AuthenticateUserUseCase)
    factoryOf(::DeleteAccountUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::AddMemberUseCase)
    factoryOf(::ApproveExitRequestUseCase)
    factoryOf(::ArchiveMemberUseCase)
    factoryOf(::GetAllPeopleWithSubscriptionsUseCase)
    factoryOf(::GetArchivedMembersUseCase)
    factoryOf(::GetMemberPaymentHistoryUseCase)
    factoryOf(::GetMemberSubscriptionDetailUseCase)
    factoryOf(::GetMembersForSubscriptionUseCase)
    factoryOf(::GetMyDebtsUseCase)
    factoryOf(::MarkMemberAsPaidUseCase)
    factoryOf(::MarkOwnPaymentUseCase)
    factoryOf(::RejectExitRequestUseCase)
    factoryOf(::RemoveMemberUseCase)
    factoryOf(::RequestExitUseCase)
    factoryOf(::RequestGroupExitUseCase)
    factoryOf(::SendReminderUseCase)
    factoryOf(::UpdateMemberUseCase)
    factoryOf(::GetPaymentHistoryUseCase)
    factoryOf(::GetRecentReferralsUseCase)
    factoryOf(::GetReferralInfoUseCase)
    factoryOf(::GetPopularServicesUseCase)
    factoryOf(::CreateSubscriptionUseCase)
    factoryOf(::DeleteSubscriptionUseCase)
    factoryOf(::ExportSubscriptionsCsvUseCase)
    factoryOf(::GetAllSubscriptionsAlphabeticalUseCase)
    factoryOf(::GetDebtorsUseCase)
    factoryOf(::GetInsightsUseCase)
    factoryOf(::GetSubscriptionDetailUseCase)
    factoryOf(::GetSubscriptionStatsUseCase)
    factoryOf(::GetSubscriptionsUseCase)
    factoryOf(::GetUpcomingCutoffsUseCase)
    factoryOf(::PauseSubscriptionUseCase)
    factoryOf(::SearchSubscriptionsUseCase)
    factoryOf(::UpdateSubscriptionDetailsUseCase)
    factoryOf(::DeleteTemplateUseCase)
    factoryOf(::DuplicateTemplateUseCase)
    factoryOf(::GetAllTemplatesUseCase)
    factoryOf(::GetTemplateByIdUseCase)
    factoryOf(::SaveTemplateUseCase)
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::GetUserProfileUseCase)
    factoryOf(::UpdateUserUseCase)
}

val uiModule = module {
    viewModelOf(::AppStateViewModel)
    viewModel { (startWithShared: Boolean, editSubscriptionId: String?) -> CreateSubscriptionViewModel(startWithShared, editSubscriptionId, get(), get(), get(), get(), get(), get()) }
    viewModelOf(::DashboardViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::PeopleViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AliasEditViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::ReferralViewModel)
    viewModelOf(::SettingsViewModel)
    viewModel { (templateId: String?) -> EditTemplateViewModel(templateId, get(), get(), get()) }
    viewModelOf(::TemplatesViewModel)
    viewModel { (subscriptionId: String) -> SubscriptionDetailRouterViewModel(subscriptionId, get(), get()) }
    viewModel { (subscriptionId: String) -> AdminDetailViewModel(subscriptionId, get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { (subscriptionId: String, memberId: String) -> MemberHistoryViewModel(subscriptionId, memberId, get(), get(), get()) }
    viewModel { (memberId: String, subscriptionId: String) -> MemberDetailViewModel(memberId, subscriptionId, get(), get(), get(), get()) }
    viewModelOf(::SubscriptionListViewModel)
}
