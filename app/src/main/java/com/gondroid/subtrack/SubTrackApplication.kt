package com.gondroid.subtrack

import android.app.Application
import com.gondroid.subtrack.core.di.initKoin
import com.gondroid.subtrack.data.local.LocalStoreSync
import com.gondroid.subtrack.data.remote.FirestoreSync
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.get

class SubTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(this)
        get<LocalStoreSync>().start()
        // Signed in on an earlier run: Room already has the data, keep it mirrored to Firestore
        get<FirebaseAuth>().currentUser?.let { get<FirestoreSync>().resume(it.uid) }
    }
}
