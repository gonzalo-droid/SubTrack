package com.gondroid.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gondroid.subtrack.app.BuildConfig
import com.gondroid.subtrack.core.auth.AuthConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG && intent.getBooleanExtra(AuthConfig.FAKE_AUTH_EXTRA, false)) {
            AuthConfig.useFakeAuth = true
        }
        enableEdgeToEdge()
        setContent { App() }
    }
}
