package com.gondroid.subtrack.data.local

import com.gondroid.subtrack.data.demo.DemoData
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test fun `subscriptions round-trip with active and archived members in order`() {
        DemoData.subscriptions.forEach { sub ->
            val withArchived = sub.copy(archivedMembers = listOf(DemoData.memCarlos))
            val back = withArchived.toEntity().toDomain(withArchived.memberEntities())
            assertEquals(withArchived, back)
        }
    }

    @Test fun `payments, templates, user and alias round-trip`() {
        DemoData.payments.forEach { assertEquals(it, it.toEntity().toDomain()) }
        DemoData.templates.forEachIndexed { i, t -> assertEquals(t, t.toEntity(i).toDomain()) }
        assertEquals(DemoData.currentUser, DemoData.currentUser.toEntity().toDomain())
        assertEquals(DemoData.userAlias, DemoData.userAlias.toEntity().toDomain())
    }
}
