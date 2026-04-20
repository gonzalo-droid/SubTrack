package com.gondroid.subtrack.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences private constructor(private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val NOTIFICATION_PERMISSION_REQUESTED = booleanPreferencesKey("notif_permission_requested")

        @Volatile private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context.applicationContext).also { INSTANCE = it }
            }
    }

    val onboardingCompleted: Flow<Boolean>
        get() = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    val notificationPermissionRequested: Flow<Boolean>
        get() = context.dataStore.data.map { it[NOTIFICATION_PERMISSION_REQUESTED] ?: false }

    suspend fun setNotificationPermissionRequested(requested: Boolean) {
        context.dataStore.edit { it[NOTIFICATION_PERMISSION_REQUESTED] = requested }
    }
}
