package com.gondroid.subtrack.di

import androidx.room.Room
import com.gondroid.subtrack.core.config.AppEnvironment
import com.gondroid.subtrack.core.preferences.USER_PREFS_FILE
import com.gondroid.subtrack.core.preferences.createUserPrefsDataStore
import com.gondroid.subtrack.data.local.SubTrackDatabase
import com.gondroid.subtrack.data.local.buildSubTrack
import com.gondroid.subtrack.platform.AuthBridge
import com.gondroid.subtrack.platform.AuthNativeBridge
import com.gondroid.subtrack.platform.ExternalActions
import com.gondroid.subtrack.platform.GoogleSignInNativeBridge
import com.gondroid.subtrack.platform.IosExternalActions
import com.gondroid.subtrack.platform.FirestoreNativeBridge
import com.gondroid.subtrack.platform.IosAuthBridge
import com.gondroid.subtrack.platform.IosRemoteDocuments
import com.gondroid.subtrack.platform.RemoteDocuments
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val platformModule: Module = module {
    single { Room.databaseBuilder<SubTrackDatabase>(name = documentPath(SubTrackDatabase.NAME)).buildSubTrack() }
    single { createUserPrefsDataStore(documentPath(USER_PREFS_FILE)) }
    single<RemoteDocuments> { IosRemoteDocuments(get()) }
    single<AuthBridge> { IosAuthBridge(get()) }
    single<ExternalActions> { IosExternalActions() }
}

/** Called from Swift at launch (ARQ-06) with the Firebase bridges written in Swift. */
fun initKoinIos(
    firestore: FirestoreNativeBridge,
    auth: AuthNativeBridge,
    googleSignIn: GoogleSignInNativeBridge,
    isDebug: Boolean
): KoinApplication {
    AppEnvironment.isDebug = isDebug
    val bridges = module {
        single { firestore }
        single { auth }
        single { googleSignIn }
    }
    return startKoin { modules(bridges, platformModule, dataModule, domainModule, uiModule) }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentPath(fileName: String): String {
    val documents: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    return requireNotNull(documents?.path) { "No documents directory" } + "/" + fileName
}
