package com.gondroid.subtrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

/**
 * Persists the in-memory store (AppStore) row by row: LocalStoreSync sends only the rows
 * that changed and the ids that were removed.
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

    @Upsert suspend fun upsertSubscriptions(items: List<SubscriptionEntity>)
    @Upsert suspend fun upsertPayments(items: List<PaymentEntity>)
    @Upsert suspend fun upsertTemplates(items: List<TemplateEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertMembers(items: List<MemberEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertUser(item: UserEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAlias(item: UserAliasEntity)

    // Members go with their subscription (ON DELETE CASCADE)
    @Query("DELETE FROM subscriptions WHERE id IN (:ids)") suspend fun deleteSubscriptions(ids: Collection<String>)
    @Query("DELETE FROM members WHERE subscriptionId IN (:subscriptionIds)") suspend fun deleteMembersOf(subscriptionIds: Collection<String>)
    @Query("DELETE FROM payments WHERE id IN (:ids)") suspend fun deletePayments(ids: Collection<String>)
    @Query("DELETE FROM templates WHERE id IN (:ids)") suspend fun deleteTemplates(ids: Collection<String>)
    @Query("DELETE FROM users") suspend fun clearUsers()

    /**
     * Writes the changed subscriptions with their full member lists and deletes the removed ones.
     * @Upsert, not REPLACE: a REPLACE deletes the row first and would cascade to its members.
     */
    @Transaction
    suspend fun applySubscriptionChanges(changed: List<SubscriptionEntity>, members: List<MemberEntity>, removedIds: Collection<String>) {
        deleteSubscriptions(removedIds)
        upsertSubscriptions(changed)
        deleteMembersOf(changed.map { it.id })
        insertMembers(members)
    }

    @Transaction
    suspend fun applyPaymentChanges(changed: List<PaymentEntity>, removedIds: Collection<String>) {
        deletePayments(removedIds)
        upsertPayments(changed)
    }

    @Transaction
    suspend fun applyTemplateChanges(changed: List<TemplateEntity>, removedIds: Collection<String>) {
        deleteTemplates(removedIds)
        upsertTemplates(changed)
    }

    @Transaction
    suspend fun replaceUser(item: UserEntity?) {
        clearUsers()
        if (item != null) insertUser(item)
    }
}
