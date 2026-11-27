package com.gondroid.subtrack.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * TST-07 / TRP-10: a database written by the published schema (composeApp/schemas/.../1.json) must open
 * with the current code and keep its rows. When SubTrackDatabase goes to version 2, add the migration and
 * a test that opens this version 1 file through it; this one fails until then on purpose.
 */
@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class DatabaseVersionTest {
    private val path = NSTemporaryDirectory() + "subtrack-v1-test.db"

    @AfterTest fun clean() {
        listOf("", "-wal", "-shm", "-journal").forEach { NSFileManager.defaultManager.removeItemAtPath(path + it, null) }
    }

    private fun createVersion1() {
        clean()
        val connection = BundledSQLiteDriver().open(path)
        listOf(
            "CREATE TABLE IF NOT EXISTS `subscriptions` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `logoUrl` TEXT, `brandColor` TEXT NOT NULL, `serviceTemplateId` TEXT, `totalAmount` REAL NOT NULL, `currency` TEXT NOT NULL, `cycle` TEXT NOT NULL, `cutoffDay` INTEGER NOT NULL, `ownerId` TEXT NOT NULL, `isShared` INTEGER NOT NULL, `splitType` TEXT NOT NULL, `category` TEXT NOT NULL, `lastActivityAt` INTEGER, `isPaused` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            "CREATE TABLE IF NOT EXISTS `members` (`subscriptionId` TEXT NOT NULL, `id` TEXT NOT NULL, `userId` TEXT, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `profileLabel` TEXT, `shareAmount` REAL NOT NULL, `isArchived` INTEGER NOT NULL, `currentStatus` TEXT NOT NULL, `joinedAt` INTEGER NOT NULL, `position` INTEGER NOT NULL, PRIMARY KEY(`subscriptionId`, `id`), FOREIGN KEY(`subscriptionId`) REFERENCES `subscriptions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
            "CREATE INDEX IF NOT EXISTS `index_members_subscriptionId` ON `members` (`subscriptionId`)",
            "CREATE TABLE IF NOT EXISTS `payments` (`id` TEXT NOT NULL, `subscriptionId` TEXT NOT NULL, `memberId` TEXT NOT NULL, `monthKey` TEXT NOT NULL, `amount` REAL NOT NULL, `status` TEXT NOT NULL, `paidAt` INTEGER, `proofUrl` TEXT, `note` TEXT, PRIMARY KEY(`id`))",
            "CREATE TABLE IF NOT EXISTS `templates` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `emoji` TEXT NOT NULL, `tone` TEXT NOT NULL, `messageBody` TEXT NOT NULL, `isDefault` INTEGER NOT NULL, `isUserCreated` INTEGER NOT NULL, `position` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            "CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `email` TEXT, `profileImageUrl` TEXT, `isPro` INTEGER NOT NULL, `referralCode` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            "CREATE TABLE IF NOT EXISTS `user_alias` (`id` INTEGER NOT NULL, `yape` TEXT, `plin` TEXT, `cciBank` TEXT, `cciNumber` TEXT, `defaultMethod` TEXT NOT NULL, PRIMARY KEY(`id`))",
            "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)",
            "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ab270e227ace6023f511477236e800b9')",
            "PRAGMA user_version = 1",
            "INSERT INTO subscriptions VALUES ('sub_1', 'Netflix', NULL, '#E50914', 'tpl_netflix', 54.9, 'PEN', 'MONTHLY', 15, 'me', 0, 'EQUAL', 'STREAMING', NULL, 0, 1, 1)"
        ).forEach { connection.execSQL(it) }
        connection.close()
    }

    @Test fun version1DatabaseOpensWithTheCurrentCodeAndKeepsItsRows() = runTest {
        createVersion1()
        val db = Room.databaseBuilder<SubTrackDatabase>(name = path).buildSubTrack()
        try {
            val subs = db.snapshotDao().subscriptions()
            assertEquals(listOf("Netflix"), subs.map { it.name })
            assertEquals(54.9, subs.single().totalAmount)
        } finally {
            db.close()
        }
    }
}
