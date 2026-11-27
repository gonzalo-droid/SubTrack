package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.core.util.logError
import com.gondroid.subtrack.domain.repository.AppConfigRepository
import com.gondroid.subtrack.platform.MIN_VERSION_FIELD
import com.gondroid.subtrack.platform.RemoteDocuments
import kotlinx.coroutines.withTimeoutOrNull

/** Reads `config/app` (public, read-only in firestore.rules). Gives up after a few seconds: never blocks startup. */
class AppConfigRepositoryImpl(private val remote: RemoteDocuments) : AppConfigRepository {

    override suspend fun minSupportedVersion(): Int? = try {
        withTimeoutOrNull(TIMEOUT_MS) { (remote.get(CONFIG_DOC)?.get(MIN_VERSION_FIELD) as? Number)?.toInt() }
    } catch (e: Exception) {
        logError(TAG, "Min version check failed", e)
        null
    }

    private companion object {
        const val TAG = "AppConfig"
        const val CONFIG_DOC = "config/app"
        const val TIMEOUT_MS = 5_000L
    }
}
