package com.gondroid.subtrack.di

import android.content.Context
import androidx.room.Room
import com.gondroid.subtrack.AppStateViewModel
import com.gondroid.subtrack.core.config.AppEnvironment
import com.gondroid.subtrack.core.preferences.USER_PREFS_FILE
import com.gondroid.subtrack.core.preferences.createUserPrefsDataStore
import com.gondroid.subtrack.data.local.SubTrackDatabase
import com.gondroid.subtrack.data.local.buildSubTrack
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
import com.gondroid.subtrack.platform.AuthBridge
import com.gondroid.subtrack.platform.FirebaseAuthBridge
import com.gondroid.subtrack.platform.FirestoreRemoteDocuments
import com.gondroid.subtrack.platform.RemoteDocuments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        val context = get<Context>()
        Room.databaseBuilder<SubTrackDatabase>(context, context.getDatabasePath(SubTrackDatabase.NAME).absolutePath).buildSubTrack()
    }
    // Same file the Android-only version used (preferencesDataStore("user_prefs")), so the flags survive the update
    single { createUserPrefsDataStore(get<Context>().filesDir.resolve("datastore/$USER_PREFS_FILE").absolutePath) }
    single<RemoteDocuments> { FirestoreRemoteDocuments(FirebaseFirestore.getInstance()) }
    single<AuthBridge> { FirebaseAuthBridge(FirebaseAuth.getInstance()) }
}

// shortcut: the ViewModels are still Android-only; this moves to commonMain with the screens
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
    viewModel { (subscriptionId: String) -> AdminDetailViewModel(subscriptionId, get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { (subscriptionId: String, memberId: String) -> MemberHistoryViewModel(subscriptionId, memberId, get(), get(), get()) }
    viewModel { (memberId: String, subscriptionId: String) -> MemberDetailViewModel(memberId, subscriptionId, get(), get(), get(), get()) }
    viewModelOf(::SubscriptionListViewModel)
}

fun initKoin(context: Context, isDebug: Boolean): KoinApplication {
    AppEnvironment.isDebug = isDebug
    return startKoin {
        androidContext(context)
        modules(platformModule, dataModule, domainModule, uiModule)
    }
}
