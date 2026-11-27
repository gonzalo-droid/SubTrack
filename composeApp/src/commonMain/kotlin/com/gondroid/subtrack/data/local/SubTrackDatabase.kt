package com.gondroid.subtrack.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

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
@ConstructedBy(SubTrackDatabaseConstructor::class)
abstract class SubTrackDatabase : RoomDatabase() {
    abstract fun snapshotDao(): SnapshotDao

    companion object {
        const val NAME = "subtrack.db"
    }
}

// Room generates the actual implementations on each platform
@Suppress("KotlinNoActualForExpect")
expect object SubTrackDatabaseConstructor : RoomDatabaseConstructor<SubTrackDatabase> {
    override fun initialize(): SubTrackDatabase
}

/** Same driver and dispatcher on every platform; each one only says where the file lives. */
fun RoomDatabase.Builder<SubTrackDatabase>.buildSubTrack(): SubTrackDatabase =
    setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO).build()
