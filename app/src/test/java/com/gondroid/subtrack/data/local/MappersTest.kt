package com.gondroid.subtrack.data.local

import com.gondroid.subtrack.data.mock.MockData
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test fun `subscriptions round-trip with active and archived members in order`() {
        MockData.subscriptions.forEach { sub ->
            val withArchived = sub.copy(archivedMembers = listOf(MockData.memCarlos))
            val back = withArchived.toEntity().toDomain(withArchived.memberEntities())
            assertEquals(withArchived, back)
        }
    }

    @Test fun `payments, templates, user and alias round-trip`() {
        MockData.payments.forEach { assertEquals(it, it.toEntity().toDomain()) }
        MockData.templates.forEachIndexed { i, t -> assertEquals(t, t.toEntity(i).toDomain()) }
        assertEquals(MockData.currentUser, MockData.currentUser.toEntity().toDomain())
        assertEquals(MockData.userAlias, MockData.userAlias.toEntity().toDomain())
    }
}
