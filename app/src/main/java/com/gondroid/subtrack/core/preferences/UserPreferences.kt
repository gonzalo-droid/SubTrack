package com.gondroid.subtrack.core.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val NOTIFICATION_PERMISSION_REQUESTED = booleanPreferencesKey("notif_permission_requested")
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
