package com.gondroid.subtrack

import android.app.Application
import com.gondroid.subtrack.app.BuildConfig
import com.gondroid.subtrack.app.R
import com.gondroid.subtrack.core.auth.GoogleIdTokenProvider
import com.gondroid.subtrack.data.local.LocalStoreSync
import com.gondroid.subtrack.data.remote.FirestoreSync
import com.gondroid.subtrack.di.initKoin
import com.gondroid.subtrack.platform.AuthBridge
import org.koin.android.ext.android.get

class SubTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(this, isDebug = BuildConfig.DEBUG)
        GoogleIdTokenProvider.webClientId = getString(R.string.default_web_client_id)
        get<LocalStoreSync>().start()
        // Signed in on an earlier run: Room already has the data, keep it mirrored to Firestore
        get<AuthBridge>().currentUserId()?.let { get<FirestoreSync>().resume(it) }
    }
}
