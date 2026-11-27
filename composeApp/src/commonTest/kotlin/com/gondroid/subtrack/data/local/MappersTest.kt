package com.gondroid.subtrack.data.local

import com.gondroid.subtrack.data.demo.DemoData
import kotlin.test.assertEquals
import kotlin.test.Test

class MappersTest {

    @Test fun subscriptionsRoundTripWithActiveAndArchivedMembersInOrder() {
        DemoData.subscriptions.forEach { sub ->
            val withArchived = sub.copy(archivedMembers = listOf(DemoData.memCarlos))
            val back = withArchived.toEntity().toDomain(withArchived.memberEntities())
            assertEquals(withArchived, back)
        }
    }

    @Test fun paymentsTemplatesUserAndAliasRoundTrip() {
        DemoData.payments.forEach { assertEquals(it, it.toEntity().toDomain()) }
        DemoData.templates.forEachIndexed { i, t -> assertEquals(t, t.toEntity(i).toDomain()) }
        assertEquals(DemoData.currentUser, DemoData.currentUser.toEntity().toDomain())
        assertEquals(DemoData.userAlias, DemoData.userAlias.toEntity().toDomain())
    }
}
