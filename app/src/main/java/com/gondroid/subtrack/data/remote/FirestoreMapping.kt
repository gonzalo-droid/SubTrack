package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.data.local.MemberEntity
import com.gondroid.subtrack.data.local.SubscriptionEntity
import com.gondroid.subtrack.domain.model.Subscription
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull

/** Firestore document of a subscription: the row plus its members, so one write keeps both in sync. */
@Serializable
data class RemoteSubscription(val subscription: SubscriptionEntity, val members: List<MemberEntity>)

// Firestore documents are Maps; the Room entities (already @Serializable) go through JSON to get one
internal val firestoreJson = Json { ignoreUnknownKeys = true }

internal inline fun <reified T> T.toFirestore(): Map<String, Any?> =
    firestoreJson.encodeToJsonElement(this).jsonObject.toPlain()

internal inline fun <reified T> Map<String, Any?>.fromFirestore(): T =
    firestoreJson.decodeFromJsonElement(toJsonElement())

internal fun JsonObject.toPlain(): Map<String, Any?> = mapValues { it.value.toPlainValue() }

private fun JsonElement.toPlainValue(): Any? = when (this) {
    is JsonObject -> toPlain()
    is JsonArray -> map { it.toPlainValue() }
    JsonNull -> null
    is JsonPrimitive -> if (isString) content else booleanOrNull ?: longOrNull ?: doubleOrNull
}

internal fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Map<*, *> -> JsonObject(entries.associate { (k, v) -> k.toString() to v.toJsonElement() })
    is List<*> -> JsonArray(map { it.toJsonElement() })
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    else -> JsonPrimitive(toString())
}

/**
 * Moves the data created before signing in (owned by the local user id) to the Firebase uid,
 * so the admin checks (`ownerId == user.id`) keep working after the first sign-in.
 */
fun List<Subscription>.claimedBy(oldUserId: String, newUserId: String): List<Subscription> =
    if (oldUserId == newUserId) this
    else map { sub ->
        sub.copy(
            ownerId = if (sub.ownerId == oldUserId) newUserId else sub.ownerId,
            members = sub.members.map { if (it.userId == oldUserId) it.copy(userId = newUserId) else it },
            archivedMembers = sub.archivedMembers.map { if (it.userId == oldUserId) it.copy(userId = newUserId) else it }
        )
    }
