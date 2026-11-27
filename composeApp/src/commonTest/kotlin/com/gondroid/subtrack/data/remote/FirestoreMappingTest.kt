package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.data.local.PaymentEntity
import com.gondroid.subtrack.data.local.memberEntities
import com.gondroid.subtrack.data.local.toDomain
import com.gondroid.subtrack.data.local.toEntity
import com.gondroid.subtrack.data.demo.DemoData
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.Test

class FirestoreMappingTest {

    @Test
    fun subscriptionWithMembersSurvivesTheFirestoreRoundTrip() {
        val sub = DemoData.subscriptions.first { it.members.isNotEmpty() }
        val doc = RemoteSubscription(sub.toEntity(), sub.memberEntities()).toFirestore()

        val back = doc.fromFirestore<RemoteSubscription>()

        assertEquals(sub, back.subscription.toDomain(back.members))
    }

    @Test
    fun numbersComeBackFromFirestoreAsLongAndStillDecode() {
        // Firestore returns whole numbers as Long, even for Double fields
        val payment = PaymentEntity("p1", "s1", "m1", "2026-11", 15.0, PaymentStatus.PAID, 1L, null, null)
        val doc = payment.toFirestore().mapValues { (_, v) -> if (v is Double && v % 1 == 0.0) v.toLong() else v }

        assertEquals(payment, doc.fromFirestore<PaymentEntity>())
        assertTrue(payment.toFirestore()["status"] == "PAID")
    }

    @Test
    fun claimMovesOwnerAndMemberLinksToTheNewUidOnly() {
        val subs = DemoData.subscriptions
        val oldId = DemoData.currentUser.id

        val claimed = subs.claimedBy(oldId, "uid_123")

        assertTrue(claimed.none { it.ownerId == oldId })
        assertTrue(claimed.flatMap { it.members + it.archivedMembers }.none { it.userId == oldId })
        // Subscriptions owned by someone else keep their owner
        assertEquals(subs.map { it.ownerId != oldId }, claimed.map { it.ownerId != "uid_123" })
        assertEquals(subs, subs.claimedBy(oldId, oldId))
    }
}
