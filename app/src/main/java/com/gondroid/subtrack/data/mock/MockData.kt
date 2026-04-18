package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory

object MockData {

    val currentUser = User(
        id = "user_1",
        name = "Carlos Mendoza",
        phone = "+51987654321",
        email = "carlos@example.com",
        profileImageUrl = null,
        isPro = false,
        createdAt = 1_700_000_000_000L
    )

    private val member1 = Member(
        id = "member_1",
        userId = "user_2",
        name = "Ana Torres",
        phone = "+51912345678",
        profileLabel = "Hermana",
        shareAmount = 16.63,
        isArchived = false,
        currentStatus = PaymentStatus.PAID,
        joinedAt = 1_700_000_000_000L
    )

    private val member2 = Member(
        id = "member_2",
        userId = null,
        name = "Diego Quispe",
        phone = "+51998765432",
        profileLabel = null,
        shareAmount = 16.63,
        isArchived = false,
        currentStatus = PaymentStatus.PENDING,
        joinedAt = 1_701_000_000_000L
    )

    private val member3Archived = Member(
        id = "member_3",
        userId = null,
        name = "Lucia Vega",
        phone = "+51976543210",
        profileLabel = "Amiga",
        shareAmount = 16.63,
        isArchived = true,
        currentStatus = PaymentStatus.OVERDUE,
        joinedAt = 1_698_000_000_000L
    )

    val subscriptions = listOf(
        // Suscripción personal — Netflix
        Subscription(
            id = "sub_1",
            name = "Netflix",
            logoUrl = null,
            brandColor = "#E50914",
            totalAmount = 49.90,
            currency = "PEN",
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 15,
            ownerId = "user_1",
            isShared = false,
            category = SubscriptionCategory.STREAMING,
            members = emptyList(),
            archivedMembers = emptyList(),
            createdAt = 1_700_000_000_000L,
            updatedAt = 1_700_000_000_000L
        ),
        // Suscripción personal — Spotify
        Subscription(
            id = "sub_2",
            name = "Spotify",
            logoUrl = null,
            brandColor = "#1DB954",
            totalAmount = 25.90,
            currency = "PEN",
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 1,
            ownerId = "user_1",
            isShared = false,
            category = SubscriptionCategory.MUSIC,
            members = emptyList(),
            archivedMembers = emptyList(),
            createdAt = 1_700_000_000_000L,
            updatedAt = 1_700_000_000_000L
        ),
        // Suscripción compartida — iCloud (admin)
        Subscription(
            id = "sub_3",
            name = "iCloud+",
            logoUrl = null,
            brandColor = "#147EFB",
            totalAmount = 49.90,
            currency = "PEN",
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 20,
            ownerId = "user_1",
            isShared = true,
            category = SubscriptionCategory.CLOUD,
            members = listOf(member1, member2),
            archivedMembers = listOf(member3Archived),
            createdAt = 1_699_000_000_000L,
            updatedAt = 1_702_000_000_000L
        ),
        // Suscripción compartida — ChatGPT (admin)
        Subscription(
            id = "sub_4",
            name = "ChatGPT Plus",
            logoUrl = null,
            brandColor = "#10A37F",
            totalAmount = 80.00,
            currency = "PEN",
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 10,
            ownerId = "user_1",
            isShared = true,
            category = SubscriptionCategory.AI,
            members = listOf(member1),
            archivedMembers = emptyList(),
            createdAt = 1_701_000_000_000L,
            updatedAt = 1_703_000_000_000L
        )
    )
}
