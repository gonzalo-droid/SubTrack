package com.gondroid.subtrack.platform

import com.gondroid.subtrack.core.result.DataError
import com.gondroid.subtrack.data.remote.toJsonElement
import com.gondroid.subtrack.data.remote.toPlain
import com.gondroid.subtrack.domain.model.GoogleCredential
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val json = Json { ignoreUnknownKeys = true }

class NativeBridgeException(message: String) : Exception(message)

/** Swift marks connectivity errors with "network:" (NativeBridges.swift); the rest are unclassified. */
private fun bridgeFailure(message: String): Throwable =
    if (message.startsWith(NETWORK_PREFIX)) RemoteFailure(DataError.Network, NativeBridgeException(message.removePrefix(NETWORK_PREFIX)))
    else NativeBridgeException(message)

private const val NETWORK_PREFIX = "network:"

/** [RemoteDocuments] over the Swift Firestore bridge. */
class IosRemoteDocuments(private val bridge: FirestoreNativeBridge) : RemoteDocuments {

    override suspend fun get(path: String): Map<String, Any?>? {
        val body = awaitJson { bridge.getDocument(path, it) }
        val element = body?.let(json::parseToJsonElement)
        return if (element == null || element is JsonNull) null else element.jsonObject.toPlain()
    }

    override suspend fun list(collectionPath: String): Map<String, Map<String, Any?>> {
        val body = awaitJson { bridge.listCollection(collectionPath, it) } ?: return emptyMap()
        return json.parseToJsonElement(body).jsonObject.mapValues { it.value.jsonObject.toPlain() }
    }

    override suspend fun commit(writes: List<RemoteWrite>) {
        if (writes.isEmpty()) return
        val body = buildJsonArray {
            writes.forEach { write ->
                add(buildJsonObject {
                    put("path", write.path)
                    when (write) {
                        is RemoteWrite.Set -> { put("op", "set"); put("data", write.data.toJsonElement()) }
                        is RemoteWrite.Delete -> put("op", "delete")
                    }
                })
            }
        }.toString()
        awaitUnit { bridge.commit(body, it) }
    }
}

@Serializable
private data class NativeUser(
    val uid: String,
    val displayName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long? = null
)

/** [AuthBridge] over the Swift Firebase Auth bridge. */
class IosAuthBridge(private val bridge: AuthNativeBridge) : AuthBridge {

    override fun currentUserId(): String? = bridge.currentUserId()

    override suspend fun signInWithGoogle(credential: GoogleCredential): AuthUser {
        val body = awaitJson { bridge.signInWithGoogle(credential.idToken, credential.requireAccessToken(), it) }
            ?: throw NativeBridgeException("Firebase returned no user")
        val user = json.decodeFromString<NativeUser>(body)
        return AuthUser(user.uid, user.displayName, user.email, user.phone, user.photoUrl, user.createdAt)
    }

    override suspend fun reauthenticate(credential: GoogleCredential) =
        awaitUnit { bridge.reauthenticate(credential.idToken, credential.requireAccessToken(), it) }

    override suspend fun signOut() = awaitUnit { bridge.signOut(it) }

    override suspend fun deleteCurrentUser() = awaitUnit { bridge.deleteCurrentUser(it) }

    private fun GoogleCredential.requireAccessToken() =
        accessToken ?: throw NativeBridgeException("Google sign-in on iOS must return an access token")
}

private suspend fun awaitJson(call: ((String?, String?) -> Unit) -> Unit): String? =
    suspendCancellableCoroutine { cont ->
        call { body, error ->
            if (error != null) cont.resumeWithException(bridgeFailure(error)) else cont.resume(body)
        }
    }

private suspend fun awaitUnit(call: ((String?) -> Unit) -> Unit): Unit =
    suspendCancellableCoroutine { cont ->
        call { error -> if (error != null) cont.resumeWithException(bridgeFailure(error)) else cont.resume(Unit) }
    }

