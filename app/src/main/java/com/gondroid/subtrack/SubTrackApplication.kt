package com.gondroid.subtrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.gondroid.subtrack.data.local.LocalStoreSync
import com.gondroid.subtrack.data.remote.FirestoreSync
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@HiltAndroidApp
class SubTrackApplication : Application() {

    @Inject lateinit var localStoreSync: LocalStoreSync
    @Inject lateinit var firestoreSync: FirestoreSync
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        localStoreSync.start()
        // Signed in on an earlier run: Room already has the data, keep it mirrored to Firestore
        firebaseAuth.currentUser?.let { firestoreSync.resume(it.uid) }
    }
}
