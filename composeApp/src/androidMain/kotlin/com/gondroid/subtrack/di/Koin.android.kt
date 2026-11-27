package com.gondroid.subtrack.di

import android.content.Context
import androidx.room.Room
import com.gondroid.subtrack.core.config.AppEnvironment
import com.gondroid.subtrack.core.preferences.USER_PREFS_FILE
import com.gondroid.subtrack.core.preferences.createUserPrefsDataStore
import com.gondroid.subtrack.data.local.SubTrackDatabase
import com.gondroid.subtrack.data.local.buildSubTrack
import com.gondroid.subtrack.platform.AndroidExternalActions
import com.gondroid.subtrack.platform.AuthBridge
import com.gondroid.subtrack.platform.ExternalActions
import com.gondroid.subtrack.platform.FirebaseAuthBridge
import com.gondroid.subtrack.platform.FirestoreRemoteDocuments
import com.gondroid.subtrack.platform.RemoteDocuments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
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
    single<ExternalActions> { AndroidExternalActions(get()) }
}


fun initKoin(context: Context, isDebug: Boolean): KoinApplication {
    AppEnvironment.isDebug = isDebug
    return startKoin {
        androidContext(context)
        modules(platformModule, dataModule, domainModule, uiModule)
    }
}
