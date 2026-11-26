package com.gondroid.subtrack.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gondroid.subtrack.data.mock.MockData
import com.gondroid.subtrack.data.mock.MockDataStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LocalStoreSyncTest {

    private lateinit var db: SubTrackDatabase
    private val dao get() = db.snapshotDao()

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), SubTrackDatabase::class.java
        ).build()
        resetStore()
    }

    @After fun tearDown() {
        db.close()
        resetStore()
    }

    private fun resetStore() {
        MockDataStore.subscriptions.value = MockData.subscriptions
        MockDataStore.payments.value = MockData.payments
        MockDataStore.templates.value = MockData.templates
        MockDataStore.currentUser.value = MockData.currentUser
        MockDataStore.userAlias.value = MockData.userAlias
    }

    /** Writes are async on IO; poll briefly instead of sleeping a fixed time. */
    private fun eventually(check: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 3_000
        while (!check()) {
            check(System.currentTimeMillis() < deadline) { "condition not met in time" }
            Thread.sleep(20)
        }
    }

    @Test fun `first start seeds the empty database with the demo data`() = runBlocking {
        LocalStoreSync(dao).start()
        assertEquals(MockData.subscriptions.map { it.id }.toSet(), dao.subscriptions().map { it.id }.toSet())
        assertEquals(MockData.currentUser.id, dao.user()?.id)
    }

    @Test fun `later starts load what was saved, not the demo`() {
        val first = LocalStoreSync(dao).also { it.start() }
        val kept = MockData.subscriptions.first()
        MockDataStore.subscriptions.value = listOf(kept.copy(name = "Renombrada"))
        eventually { runBlocking { dao.subscriptions().map { it.name } } == listOf("Renombrada") }

        first.close() // simulate a new process: the old one stops, the store starts from the demo
        resetStore()
        LocalStoreSync(dao).start()
        assertEquals(listOf("Renombrada"), MockDataStore.subscriptions.value.map { it.name })
    }

    @Test fun `deleting a subscription removes its members`() {
        LocalStoreSync(dao).start()
        val shared = MockData.subscriptions.first { it.members.isNotEmpty() }
        MockDataStore.subscriptions.value = MockData.subscriptions - shared
        eventually { runBlocking { dao.members().none { it.subscriptionId == shared.id } } }
        assertTrue(runBlocking { dao.subscriptions().none { it.id == shared.id } })
    }

    @Test fun `an empty users table alone does not reseed over real data`() {
        val first = LocalStoreSync(dao).also { it.start() }
        MockDataStore.subscriptions.value = listOf(MockData.subscriptions.first().copy(name = "Mía"))
        MockDataStore.currentUser.value = null // e.g. a future logout
        eventually { runBlocking { dao.user() == null && dao.subscriptions().map { it.name } == listOf("Mía") } }

        first.close()
        resetStore()
        LocalStoreSync(dao).start()
        assertEquals(listOf("Mía"), MockDataStore.subscriptions.value.map { it.name })
    }
}
