package com.gondroid.subtrack.di

import com.gondroid.subtrack.data.local.LocalStoreSync
import com.gondroid.subtrack.data.remote.FirestoreSync
import com.gondroid.subtrack.platform.AuthBridge
import com.gondroid.subtrack.platform.AuthNativeBridge
import com.gondroid.subtrack.platform.FirestoreNativeBridge
import com.gondroid.subtrack.platform.GoogleSignInNativeBridge
import platform.Foundation.NSLog
import kotlin.experimental.ExperimentalNativeApi

/**
 * Called once from iOSApp.swift (ARQ-06) with the Swift bridges:
 * `IosEntryKt.startSubTrack(firestore:auth:googleSignIn:isDebug:)`. Mirrors SubTrackApplication on Android.
 */
@OptIn(ExperimentalNativeApi::class)
fun startSubTrack(
    firestore: FirestoreNativeBridge,
    auth: AuthNativeBridge,
    googleSignIn: GoogleSignInNativeBridge,
    isDebug: Boolean
) {
    // Kotlin/Native only writes uncaught errors to stderr; NSLog puts them in the system log before the abort
    setUnhandledExceptionHook { error ->
        NSLog("SubTrack: uncaught Kotlin exception: %@", error.stackTraceToString())
        terminateWithUnhandledException(error)
    }
    val koin = initKoinIos(firestore, auth, googleSignIn, isDebug).koin
    koin.get<LocalStoreSync>().start()
    // Signed in on an earlier run: keep it mirrored to Firestore
    koin.get<AuthBridge>().currentUserId()?.let { koin.get<FirestoreSync>().resume(it) }
}
