package com.gondroid.subtrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/**
 * Reads and replaces whole tables. The in-memory store (AppStore) stays the working copy
 * in phase 1; this DAO persists it so changes survive restarts.
 */
@Dao
interface SnapshotDao {

    @Query("SELECT * FROM subscriptions") suspend fun subscriptions(): List<SubscriptionEntity>
    @Query("SELECT * FROM members ORDER BY position") suspend fun members(): List<MemberEntity>
    @Query("SELECT * FROM payments") suspend fun payments(): List<PaymentEntity>
    @Query("SELECT * FROM templates ORDER BY position") suspend fun templates(): List<TemplateEntity>
    @Query("SELECT * FROM users LIMIT 1") suspend fun user(): UserEntity?
    @Query("SELECT * FROM user_alias WHERE id = 0") suspend fun alias(): UserAliasEntity?
    @Query("SELECT COUNT(*) FROM users") suspend fun userCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSubscriptions(items: List<SubscriptionEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertMembers(items: List<MemberEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertPayments(items: List<PaymentEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertTemplates(items: List<TemplateEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertUser(item: UserEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAlias(item: UserAliasEntity)

    @Query("DELETE FROM subscriptions") suspend fun clearSubscriptions()
    @Query("DELETE FROM payments") suspend fun clearPayments()
    @Query("DELETE FROM templates") suspend fun clearTemplates()
    @Query("DELETE FROM users") suspend fun clearUsers()

    /**
     * Members go with their subscriptions (ON DELETE CASCADE). Careful if this becomes a per-row
     * write: a REPLACE on `subscriptions` deletes that row first and cascades to its members, so
     * use @Upsert there instead.
     */
    @Transaction
    suspend fun replaceSubscriptions(subscriptions: List<SubscriptionEntity>, members: List<MemberEntity>) {
        clearSubscriptions()
        insertSubscriptions(subscriptions)
        insertMembers(members)
    }

    @Transaction
    suspend fun replacePayments(items: List<PaymentEntity>) {
        clearPayments()
        insertPayments(items)
    }

    @Transaction
    suspend fun replaceTemplates(items: List<TemplateEntity>) {
        clearTemplates()
        insertTemplates(items)
    }

    @Transaction
    suspend fun replaceUser(item: UserEntity?) {
        clearUsers()
        if (item != null) insertUser(item)
    }
}
