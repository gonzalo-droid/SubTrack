package com.gondroid.subtrack.data.remote

import android.util.Log
import com.gondroid.subtrack.data.local.PaymentEntity
import com.gondroid.subtrack.data.local.TemplateEntity
import com.gondroid.subtrack.data.local.UserAliasEntity
import com.gondroid.subtrack.data.local.UserEntity
import com.gondroid.subtrack.data.local.memberEntities
import com.gondroid.subtrack.data.local.toDomain
import com.gondroid.subtrack.data.local.toEntity
import com.gondroid.subtrack.data.mock.MockDataStore
import com.gondroid.subtrack.domain.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mirrors the in-memory store ([MockDataStore], persisted to Room by LocalStoreSync) to
 * Firestore under `users/{uid}`: profile + alias in the user document, and the
 * `subscriptions`, `payments` and `templates` subcollections. Room stays the offline cache;
 * Firestore's own offline queue delivers writes made without network.
 *
 * shortcut: pulls only at sign-in, no live listener; enough for one device per account,
 * add snapshot listeners when the same account is used on several devices.
 */
@Singleton
class FirestoreSync @Inject constructor(private val db: FirebaseFirestore) {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, e -> Log.e(TAG, "Firestore sync failed", e) }
    )
    private var mirrorJob: Job? = null

    /**
     * First sign-in on this device: restores the account from Firestore when it has data,
     * otherwise uploads what is on the device (moved to the new uid). Then keeps mirroring.
     */
    suspend fun connect(user: User) {
        val root = userDoc(user.id)
        val remote = root.get().await()
        if (remote.exists()) {
            restore(root, remote.data.orEmpty(), user)
        } else {
            val localUserId = MockDataStore.currentUser.value?.id
            if (localUserId != null) {
                MockDataStore.subscriptions.value = MockDataStore.subscriptions.value.claimedBy(localUserId, user.id)
            }
            MockDataStore.currentUser.value = user
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
            if (!root.get().await().exists()) upload(root)
        }
    }

    private suspend fun restore(root: DocumentReference, profile: Map<String, Any?>, user: User) {
        val subs = root.collection(SUBSCRIPTIONS).get().await().documents
            .map { it.data.orEmpty().fromFirestore<RemoteSubscription>() }
            .map { it.subscription.toDomain(it.members) }
        val payments = root.collection(PAYMENTS).get().await().documents
            .map { it.data.orEmpty().fromFirestore<PaymentEntity>().toDomain() }
        val templates = root.collection(TEMPLATES).get().await().documents
            .map { it.data.orEmpty().fromFirestore<TemplateEntity>() }
            .sortedBy { it.position }.map { it.toDomain() }

        MockDataStore.subscriptions.value = subs
        MockDataStore.payments.value = payments
        if (templates.isNotEmpty()) MockDataStore.templates.value = templates
        (profile[ALIAS] as? Map<*, *>)?.let { alias ->
            @Suppress("UNCHECKED_CAST")
            MockDataStore.userAlias.value = (alias as Map<String, Any?>).fromFirestore<UserAliasEntity>().toDomain()
        }
        // The stored profile keeps edits (phone, name); the Google account fills what is missing
        @Suppress("UNCHECKED_CAST")
        val stored = (profile[PROFILE] as? Map<String, Any?>)?.fromFirestore<UserEntity>()?.toDomain()
        MockDataStore.currentUser.value = stored ?: user
    }

    private suspend fun upload(root: DocumentReference) {
        val batch = db.batch()
        batch.set(root, profileDoc())
        subscriptionDocs().forEach { (id, doc) -> batch.set(root.collection(SUBSCRIPTIONS).document(id), doc) }
        paymentDocs().forEach { (id, doc) -> batch.set(root.collection(PAYMENTS).document(id), doc) }
        templateDocs().forEach { (id, doc) -> batch.set(root.collection(TEMPLATES).document(id), doc) }
        batch.commit().await()
    }

    private fun mirror(uid: String) {
        if (mirrorJob != null) return
        val root = userDoc(uid)
        mirrorJob = scope.launch {
            mirrorCollection(root, SUBSCRIPTIONS, MockDataStore.subscriptions) { subscriptionDocs() }
            mirrorCollection(root, PAYMENTS, MockDataStore.payments) { paymentDocs() }
            mirrorCollection(root, TEMPLATES, MockDataStore.templates) { templateDocs() }
            launch(start = CoroutineStart.UNDISPATCHED) {
                MockDataStore.currentUser.drop(1).collect { root.set(profileDoc()).await() }
            }
            launch(start = CoroutineStart.UNDISPATCHED) {
                MockDataStore.userAlias.drop(1).collect { root.set(profileDoc()).await() }
            }
        }
    }

    /** Writes only the documents that changed since the last emission and deletes the removed ones. */
    private fun CoroutineScope.mirrorCollection(
        root: DocumentReference,
        name: String,
        flow: StateFlow<*>,
        docs: () -> Map<String, Map<String, Any?>>
    ) = launch(start = CoroutineStart.UNDISPATCHED) {
        var previous = docs()
        flow.drop(1).collect {
            val current = docs()
            val batch = db.batch()
            current.filter { (id, doc) -> previous[id] != doc }
                .forEach { (id, doc) -> batch.set(root.collection(name).document(id), doc) }
            (previous.keys - current.keys).forEach { batch.delete(root.collection(name).document(it)) }
            previous = current
            batch.commit().await()
        }
    }

    private fun profileDoc(): Map<String, Any?> = mapOf(
        PROFILE to MockDataStore.currentUser.value?.toEntity()?.toFirestore(),
        ALIAS to MockDataStore.userAlias.value.toEntity().toFirestore()
    )

    private fun subscriptionDocs() = MockDataStore.subscriptions.value.associate {
        it.id to RemoteSubscription(it.toEntity(), it.memberEntities()).toFirestore()
    }

    private fun paymentDocs() = MockDataStore.payments.value.associate { it.id to it.toEntity().toFirestore() }

    private fun templateDocs() = MockDataStore.templates.value.mapIndexed { i, t -> t.id to t.toEntity(i).toFirestore() }.toMap()

    private fun userDoc(uid: String) = db.collection(USERS).document(uid)

    private companion object {
        const val TAG = "FirestoreSync"
        const val USERS = "users"
        const val SUBSCRIPTIONS = "subscriptions"
        const val PAYMENTS = "payments"
        const val TEMPLATES = "templates"
        const val PROFILE = "profile"
        const val ALIAS = "alias"
    }
}
