package com.gondroid.subtrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SubscriptionEntity::class,
        MemberEntity::class,
        PaymentEntity::class,
        TemplateEntity::class,
        UserEntity::class,
        UserAliasEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SubTrackDatabase : RoomDatabase() {
    abstract fun snapshotDao(): SnapshotDao

    companion object {
        const val NAME = "subtrack.db"
    }
}
