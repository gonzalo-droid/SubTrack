package com.gondroid.subtrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.gondroid.subtrack.data.local.LocalStoreSync
import javax.inject.Inject

@HiltAndroidApp
class SubTrackApplication : Application() {

    @Inject lateinit var localStoreSync: LocalStoreSync

    override fun onCreate() {
        super.onCreate()
        localStoreSync.start()
    }
}
