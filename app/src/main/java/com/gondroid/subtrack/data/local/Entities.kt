package com.gondroid.subtrack.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentAliasType
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.model.enums.SplitType
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.model.enums.TemplateTone

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logoUrl: String?,
    val brandColor: String,
    val serviceTemplateId: String?,
    val totalAmount: Double,
    val currency: String,
    val cycle: BillingCycle,
    val cutoffDay: Int,
    val ownerId: String,
    val isShared: Boolean,
    val splitType: SplitType,
    val category: SubscriptionCategory,
    val lastActivityAt: Long?,
    val isPaused: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/** Active and archived members of a subscription; [position] keeps the list order. */
@Entity(
    tableName = "members",
    primaryKeys = ["subscriptionId", "id"],
    foreignKeys = [ForeignKey(
        entity = SubscriptionEntity::class,
        parentColumns = ["id"],
        childColumns = ["subscriptionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("subscriptionId")]
)
data class MemberEntity(
    val subscriptionId: String,
    val id: String,
    val userId: String?,
    val name: String,
    val phone: String,
    val profileLabel: String?,
    val shareAmount: Double,
    val isArchived: Boolean,
    val currentStatus: PaymentStatus,
    val joinedAt: Long,
    val position: Int
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val subscriptionId: String,
    val memberId: String,
    val monthKey: String,
    val amount: Double,
    val status: PaymentStatus,
    val paidAt: Long?,
    val proofUrl: String?,
    val note: String?
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val tone: TemplateTone,
    val messageBody: String,
    val isDefault: Boolean,
    val isUserCreated: Boolean,
    val position: Int
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val profileImageUrl: String?,
    val isPro: Boolean,
    val referralCode: String,
    val createdAt: Long
)

/** Single row (id = 0): the signed-in user's payment aliases. */
@Entity(tableName = "user_alias")
data class UserAliasEntity(
    @PrimaryKey val id: Int = 0,
    val yape: String?,
    val plin: String?,
    val cciBank: String?,
    val cciNumber: String?,
    val defaultMethod: PaymentAliasType
)
