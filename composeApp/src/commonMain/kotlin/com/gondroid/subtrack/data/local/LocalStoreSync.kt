package com.gondroid.subtrack.data.local

import com.gondroid.subtrack.data.repository.AppStore
import com.gondroid.subtrack.core.util.logError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Room persistence for the in-memory store ([AppStore]): the repositories work on it; this loads
 * it from the database at startup (seeding the demo data on a debug first run) and writes every
 * later change back, row by row.
 */
class LocalStoreSync(private val dao: SnapshotDao) {

    // A failed write (e.g. SQLite error) is logged and skipped instead of crashing the app
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, e -> logError(TAG, "Persist failed", e) }
    )

    fun start() {
        // Blocking on purpose: the first screen must read persisted data, not the demo seed.
        // Seed only a truly empty database: an empty users table alone (e.g. after a logout)
        // must not overwrite the user's subscriptions with the demo data.
        runBlocking(Dispatchers.IO) {
            if (dao.userCount() == 0 && dao.subscriptions().isEmpty()) seed() else load()
        }
        persistRows(AppStore.subscriptions, { subs -> subs.associate { it.id to (it.toEntity() to it.memberEntities()) } }) { changed, removed ->
            dao.applySubscriptionChanges(changed.map { it.first }, changed.flatMap { it.second }, removed)
        }
        persistRows(AppStore.payments, { it.associate { p -> p.id to p.toEntity() } }, dao::applyPaymentChanges)
        persistRows(AppStore.templates, { it.mapIndexed { i, t -> t.id to t.toEntity(i) }.toMap() }, dao::applyTemplateChanges)
        persist(AppStore.currentUser) { dao.replaceUser(it?.toEntity()) }
        persist(AppStore.userAlias) { dao.insertAlias(it.toEntity()) }
    }

    /** Stops persisting; the app never calls it (the process ends instead), tests do. */
    fun close() = scope.cancel()

    private suspend fun seed() {
        val subs = AppStore.subscriptions.value
        dao.applySubscriptionChanges(subs.map { it.toEntity() }, subs.flatMap { it.memberEntities() }, emptyList())
        dao.applyPaymentChanges(AppStore.payments.value.map { it.toEntity() }, emptyList())
        dao.applyTemplateChanges(AppStore.templates.value.mapIndexed { i, t -> t.toEntity(i) }, emptyList())
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

    /**
     * Per-row persistence: compares each new list with the previous one by id and writes only
     * the changed rows plus the removed ids.
     */
    private fun <T, R> persistRows(
        flow: StateFlow<List<T>>,
        toRows: (List<T>) -> Map<String, R>,
        apply: suspend (changed: List<R>, removedIds: Set<String>) -> Unit
    ) {
        var previous = toRows(flow.value)
        persist(flow) { list ->
            val current = toRows(list)
            val changed = current.filter { (id, row) -> previous[id] != row }.values.toList()
            val removed = previous.keys - current.keys
            previous = current
            if (changed.isNotEmpty() || removed.isNotEmpty()) apply(changed, removed)
        }
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
