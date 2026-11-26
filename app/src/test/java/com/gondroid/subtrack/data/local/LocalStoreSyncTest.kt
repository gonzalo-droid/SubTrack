package com.gondroid.subtrack.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gondroid.subtrack.data.demo.DemoData
import com.gondroid.subtrack.data.repository.AppStore
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
        AppStore.subscriptions.value = DemoData.subscriptions
        AppStore.payments.value = DemoData.payments
        AppStore.templates.value = DemoData.templates
        AppStore.currentUser.value = DemoData.currentUser
        AppStore.userAlias.value = DemoData.userAlias
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
        assertEquals(DemoData.subscriptions.map { it.id }.toSet(), dao.subscriptions().map { it.id }.toSet())
        assertEquals(DemoData.currentUser.id, dao.user()?.id)
    }

    @Test fun `later starts load what was saved, not the demo`() {
        val first = LocalStoreSync(dao).also { it.start() }
        val kept = DemoData.subscriptions.first()
        AppStore.subscriptions.value = listOf(kept.copy(name = "Renombrada"))
        eventually { runBlocking { dao.subscriptions().map { it.name } } == listOf("Renombrada") }

        first.close() // simulate a new process: the old one stops, the store starts from the demo
        resetStore()
        LocalStoreSync(dao).start()
        assertEquals(listOf("Renombrada"), AppStore.subscriptions.value.map { it.name })
    }

    @Test fun `deleting a subscription removes its members`() {
        LocalStoreSync(dao).start()
        val shared = DemoData.subscriptions.first { it.members.isNotEmpty() }
        AppStore.subscriptions.value = DemoData.subscriptions - shared
        eventually { runBlocking { dao.members().none { it.subscriptionId == shared.id } } }
        assertTrue(runBlocking { dao.subscriptions().none { it.id == shared.id } })
    }

    @Test fun `editing one subscription rewrites only its members`() {
        LocalStoreSync(dao).start()
        val shared = DemoData.subscriptions.filter { it.members.size > 1 }
        val edited = shared.first()
        val others = runBlocking { dao.members() }.filter { it.subscriptionId != edited.id }
        AppStore.subscriptions.value = DemoData.subscriptions.map {
            if (it.id == edited.id) it.copy(members = it.members.drop(1)) else it
        }
        eventually { runBlocking { dao.members().count { it.subscriptionId == edited.id } } == edited.members.size - 1 + edited.archivedMembers.size }
        assertEquals(others, runBlocking { dao.members() }.filter { it.subscriptionId != edited.id })
    }

    @Test fun `clearing the store on sign-out empties the database but keeps the default templates`() {
        LocalStoreSync(dao).start()
        AppStore.clear()
        eventually { runBlocking { dao.subscriptions().isEmpty() && dao.payments().isEmpty() && dao.user() == null } }
        assertTrue(runBlocking { dao.members() }.isEmpty())
        assertEquals(DemoData.templates.map { it.id }, runBlocking { dao.templates() }.map { it.id })
    }

    @Test fun `an empty users table alone does not reseed over real data`() {
        val first = LocalStoreSync(dao).also { it.start() }
        AppStore.subscriptions.value = listOf(DemoData.subscriptions.first().copy(name = "Mía"))
        AppStore.currentUser.value = null // e.g. a future logout
        eventually { runBlocking { dao.user() == null && dao.subscriptions().map { it.name } == listOf("Mía") } }

        first.close()
        resetStore()
        LocalStoreSync(dao).start()
        assertEquals(listOf("Mía"), AppStore.subscriptions.value.map { it.name })
    }
}
