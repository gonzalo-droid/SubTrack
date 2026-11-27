package com.gondroid.subtrack.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

/** File name kept from the Android-only version, so existing installs keep their flags. */
const val USER_PREFS_FILE = "user_prefs.preferences_pb"

/** Each platform passes the absolute path of [USER_PREFS_FILE]. */
fun createUserPrefsDataStore(path: String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { path.toPath() })

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val NOTIFICATION_PERMISSION_REQUESTED = booleanPreferencesKey("notif_permission_requested")
    }

    val onboardingCompleted: Flow<Boolean>
        get() = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    val notificationPermissionRequested: Flow<Boolean>
        get() = dataStore.data.map { it[NOTIFICATION_PERMISSION_REQUESTED] ?: false }

    suspend fun setNotificationPermissionRequested(requested: Boolean) {
        dataStore.edit { it[NOTIFICATION_PERMISSION_REQUESTED] = requested }
    }
}
