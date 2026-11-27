package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.data.local.PaymentEntity
import com.gondroid.subtrack.data.local.TemplateEntity
import com.gondroid.subtrack.data.local.UserAliasEntity
import com.gondroid.subtrack.data.local.UserEntity
import com.gondroid.subtrack.data.local.memberEntities
import com.gondroid.subtrack.data.local.toDomain
import com.gondroid.subtrack.data.local.toEntity
import com.gondroid.subtrack.data.repository.AppStore
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.platform.RemoteDocuments
import com.gondroid.subtrack.platform.RemoteWrite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

/**
 * Mirrors the in-memory store ([AppStore], persisted to Room by LocalStoreSync) to
 * Firestore under `users/{uid}`: profile + alias in the user document, and the
 * `subscriptions`, `payments` and `templates` subcollections. Room stays the offline cache;
 * Firestore's own offline queue delivers writes made without network.
 *
 * shortcut: pulls only at sign-in, no live listener; enough for one device per account,
 * add snapshot listeners when the same account is used on several devices.
 */
class FirestoreSync(private val remote: RemoteDocuments, private val scope: CoroutineScope) {

    private var mirrorJob: Job? = null

    /**
     * First sign-in on this device: restores the account from Firestore when it has data,
     * otherwise uploads what is on the device (moved to the new uid). Then keeps mirroring.
     */
    suspend fun connect(user: User) {
        val root = userDoc(user.id)
        val remoteProfile = remote.get(root)
        if (remoteProfile != null) {
            restore(root, remoteProfile, user)
        } else {
            val localUserId = AppStore.currentUser.value?.id
            if (localUserId != null) {
                AppStore.subscriptions.value = AppStore.subscriptions.value.claimedBy(localUserId, user.id)
            }
            AppStore.currentUser.value = user
            upload(root)
        }
        mirror(user.id)
    }

    /** App start with a signed-in account: keep mirroring local changes. */
    fun resume(uid: String) {
        mirror(uid)
        // The first upload may have failed (sign-in without network): retry it once online
        scope.launch {
            val root = userDoc(uid)
            if (remote.get(root) == null) upload(root)
        }
    }

    /**
     * Stops mirroring. Call it BEFORE clearing the store on sign-out: a running mirror would
     * take the emptied lists as deletions and wipe the account's cloud data.
     */
    fun stop() {
        mirrorJob?.cancel()
        mirrorJob = null
    }

    /** Deletes everything under `users/{uid}` (the client must delete each subcollection document). */
    suspend fun deleteRemote(uid: String) {
        val root = userDoc(uid)
        for (name in listOf(SUBSCRIPTIONS, PAYMENTS, TEMPLATES)) {
            remote.list("$root/$name").keys.chunked(MAX_BATCH_WRITES).forEach { ids ->
                remote.commit(ids.map { RemoteWrite.Delete("$root/$name/$it") })
            }
        }
        remote.commit(listOf(RemoteWrite.Delete(root)))
    }

    private suspend fun restore(root: String, profile: Map<String, Any?>, user: User) {
        val subs = remote.list("$root/$SUBSCRIPTIONS").values
            .map { it.fromFirestore<RemoteSubscription>() }
            .map { it.subscription.toDomain(it.members) }
        val payments = remote.list("$root/$PAYMENTS").values
            .map { it.fromFirestore<PaymentEntity>().toDomain() }
        val templates = remote.list("$root/$TEMPLATES").values
            .map { it.fromFirestore<TemplateEntity>() }
            .sortedBy { it.position }.map { it.toDomain() }

        AppStore.subscriptions.value = subs
        AppStore.payments.value = payments
        if (templates.isNotEmpty()) AppStore.templates.value = templates
        (profile[ALIAS] as? Map<*, *>)?.let { alias ->
            @Suppress("UNCHECKED_CAST")
            AppStore.userAlias.value = (alias as Map<String, Any?>).fromFirestore<UserAliasEntity>().toDomain()
        }
        // The stored profile keeps edits (phone, name); the Google account fills what is missing
        @Suppress("UNCHECKED_CAST")
        val stored = (profile[PROFILE] as? Map<String, Any?>)?.fromFirestore<UserEntity>()?.toDomain()
        AppStore.currentUser.value = stored ?: user
    }

    private suspend fun upload(root: String) {
        val writes = buildList {
            add(RemoteWrite.Set(root, profileDoc()))
            subscriptionDocs().forEach { (id, doc) -> add(RemoteWrite.Set("$root/$SUBSCRIPTIONS/$id", doc)) }
            paymentDocs().forEach { (id, doc) -> add(RemoteWrite.Set("$root/$PAYMENTS/$id", doc)) }
            templateDocs().forEach { (id, doc) -> add(RemoteWrite.Set("$root/$TEMPLATES/$id", doc)) }
        }
        writes.chunked(MAX_BATCH_WRITES).forEach { remote.commit(it) }
    }

    private fun mirror(uid: String) {
        if (mirrorJob != null) return
        val root = userDoc(uid)
        mirrorJob = scope.launch {
            mirrorCollection("$root/$SUBSCRIPTIONS", AppStore.subscriptions) { subscriptionDocs() }
            mirrorCollection("$root/$PAYMENTS", AppStore.payments) { paymentDocs() }
            mirrorCollection("$root/$TEMPLATES", AppStore.templates) { templateDocs() }
            launch(start = CoroutineStart.UNDISPATCHED) {
                AppStore.currentUser.drop(1).collect { remote.commit(listOf(RemoteWrite.Set(root, profileDoc()))) }
            }
            launch(start = CoroutineStart.UNDISPATCHED) {
                AppStore.userAlias.drop(1).collect { remote.commit(listOf(RemoteWrite.Set(root, profileDoc()))) }
            }
        }
    }

    /** Writes only the documents that changed since the last emission and deletes the removed ones. */
    private fun CoroutineScope.mirrorCollection(
        collectionPath: String,
        flow: StateFlow<*>,
        docs: () -> Map<String, Map<String, Any?>>
    ) = launch(start = CoroutineStart.UNDISPATCHED) {
        var previous = docs()
        flow.drop(1).collect {
            val current = docs()
            val writes = current.filter { (id, doc) -> previous[id] != doc }
                .map { (id, doc) -> RemoteWrite.Set("$collectionPath/$id", doc) } +
                (previous.keys - current.keys).map { RemoteWrite.Delete("$collectionPath/$it") }
            previous = current
            writes.chunked(MAX_BATCH_WRITES).forEach { remote.commit(it) }
        }
    }

    private fun profileDoc(): Map<String, Any?> = mapOf(
        // UPD-04: new fields stay optional; an incompatible change bumps this and the min version (config/app)
        SCHEMA_VERSION to CURRENT_SCHEMA_VERSION,
        PROFILE to AppStore.currentUser.value?.toEntity()?.toFirestore(),
        ALIAS to AppStore.userAlias.value.toEntity().toFirestore()
    )

    private fun subscriptionDocs() = AppStore.subscriptions.value.associate {
        it.id to RemoteSubscription(it.toEntity(), it.memberEntities()).toFirestore()
    }

    private fun paymentDocs() = AppStore.payments.value.associate { it.id to it.toEntity().toFirestore() }

    private fun templateDocs() = AppStore.templates.value.mapIndexed { i, t -> t.id to t.toEntity(i).toFirestore() }.toMap()

    private fun userDoc(uid: String) = "$USERS/$uid"

    private companion object {
        const val USERS = "users"
        const val SUBSCRIPTIONS = "subscriptions"
        const val PAYMENTS = "payments"
        const val TEMPLATES = "templates"
        const val PROFILE = "profile"
        const val ALIAS = "alias"
        const val SCHEMA_VERSION = "schemaVersion"
        const val CURRENT_SCHEMA_VERSION = 1
        const val MAX_BATCH_WRITES = 450 // Firestore allows 500 writes per batch
    }
}
