package com.gondroid.subtrack.data.local

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.UserAlias

fun Subscription.toEntity() = SubscriptionEntity(
    id, name, logoUrl, brandColor, serviceTemplateId, totalAmount, currency, cycle, cutoffDay,
    ownerId, isShared, splitType, category, lastActivityAt, isPaused, createdAt, updatedAt
)

/** Active members first, then archived, each in list order. */
fun Subscription.memberEntities(): List<MemberEntity> =
    (members.map { it to false } + archivedMembers.map { it to true }).mapIndexed { index, (m, archived) ->
        MemberEntity(id, m.id, m.userId, m.name, m.phone, m.profileLabel, m.shareAmount, archived, m.currentStatus, m.joinedAt, index)
    }

fun SubscriptionEntity.toDomain(members: List<MemberEntity>) = Subscription(
    id = id, name = name, logoUrl = logoUrl, brandColor = brandColor, serviceTemplateId = serviceTemplateId,
    totalAmount = totalAmount, currency = currency, cycle = cycle, cutoffDay = cutoffDay, ownerId = ownerId,
    isShared = isShared, splitType = splitType, category = category,
    members = members.filter { !it.isArchived }.sortedBy { it.position }.map { it.toDomain() },
    archivedMembers = members.filter { it.isArchived }.sortedBy { it.position }.map { it.toDomain() },
    lastActivityAt = lastActivityAt, isPaused = isPaused, createdAt = createdAt, updatedAt = updatedAt
)

private fun MemberEntity.toDomain() =
    Member(id, userId, name, phone, profileLabel, shareAmount, isArchived, currentStatus, joinedAt)

fun Payment.toEntity() = PaymentEntity(id, subscriptionId, memberId, monthKey, amount, status, paidAt, proofUrl, note)
fun PaymentEntity.toDomain() = Payment(id, subscriptionId, memberId, monthKey, amount, status, paidAt, proofUrl, note)

fun Template.toEntity(position: Int) = TemplateEntity(id, name, emoji, tone, messageBody, isDefault, isUserCreated, position)
fun TemplateEntity.toDomain() = Template(id, name, emoji, tone, messageBody, isDefault, isUserCreated)

fun User.toEntity() = UserEntity(id, name, phone, email, profileImageUrl, isPro, referralCode, createdAt)
fun UserEntity.toDomain() = User(id, name, phone, email, profileImageUrl, isPro, referralCode, createdAt)

fun UserAlias.toEntity() = UserAliasEntity(yape = yape, plin = plin, cciBank = cciBank, cciNumber = cciNumber, defaultMethod = defaultMethod)
fun UserAliasEntity.toDomain() = UserAlias(yape, plin, cciBank, cciNumber, defaultMethod)
