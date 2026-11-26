package com.gondroid.subtrack.data.local

import com.gondroid.subtrack.data.repository.AppStore
import android.util.Log
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room persistence for the phase-1 store: the repositories keep working on [AppStore];
 * this loads it from the database at startup (seeding the demo data on first run) and writes
 * every later change back.
 *
 * shortcut: whole-table rewrites on each change; fine for a personal list of subscriptions,
 * replace with per-row DAO calls in the repositories when Firebase sync arrives (fase 2).
 */
@Singleton
class LocalStoreSync @Inject constructor(private val dao: SnapshotDao) {

    // A failed write (e.g. SQLite error) is logged and skipped instead of crashing the app
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, e -> Log.e(TAG, "Persist failed", e) }
    )

    fun start() {
        // Blocking on purpose: the first screen must read persisted data, not the demo seed.
        // Seed only a truly empty database: an empty users table alone (e.g. after a logout)
        // must not overwrite the user's subscriptions with the demo data.
        runBlocking(Dispatchers.IO) {
            if (dao.userCount() == 0 && dao.subscriptions().isEmpty()) seed() else load()
        }
        persist(AppStore.subscriptions) { subs ->
            dao.replaceSubscriptions(subs.map { it.toEntity() }, subs.flatMap { it.memberEntities() })
        }
        persist(AppStore.payments) { dao.replacePayments(it.map { p -> p.toEntity() }) }
        persist(AppStore.templates) { dao.replaceTemplates(it.mapIndexed { i, t -> t.toEntity(i) }) }
        persist(AppStore.currentUser) { dao.replaceUser(it?.toEntity()) }
        persist(AppStore.userAlias) { dao.insertAlias(it.toEntity()) }
    }

    /** Stops persisting; the app never calls it (the process ends instead), tests do. */
    @VisibleForTesting
    fun close() = scope.cancel()

    private suspend fun seed() {
        val subs = AppStore.subscriptions.value
        dao.replaceSubscriptions(subs.map { it.toEntity() }, subs.flatMap { it.memberEntities() })
        dao.replacePayments(AppStore.payments.value.map { it.toEntity() })
        dao.replaceTemplates(AppStore.templates.value.mapIndexed { i, t -> t.toEntity(i) })
        dao.replaceUser(AppStore.currentUser.value?.toEntity())
        dao.insertAlias(AppStore.userAlias.value.toEntity())
    }

    private suspend fun load() {
        val members = dao.members().groupBy { it.subscriptionId }
        AppStore.subscriptions.value = dao.subscriptions().map { it.toDomain(members[it.id].orEmpty()) }
        AppStore.payments.value = dao.payments().map { it.toDomain() }
        AppStore.templates.value = dao.templates().map { it.toDomain() }
        AppStore.currentUser.value = dao.user()?.toDomain()
        dao.alias()?.let { AppStore.userAlias.value = it.toDomain() }
    }

    /** Writes every value after the current one (already in sync) back to the database. */
    private fun <T> persist(flow: Flow<T>, write: suspend (T) -> Unit) {
        // UNDISPATCHED: subscribe right now, so a change made before the IO thread starts isn't lost
        scope.launch(start = CoroutineStart.UNDISPATCHED) { flow.drop(1).collect { write(it) } }
    }

    private companion object {
        const val TAG = "LocalStoreSync"
    }
}
