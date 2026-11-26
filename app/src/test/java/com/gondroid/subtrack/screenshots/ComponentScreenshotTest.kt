package com.gondroid.subtrack.screenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Public
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.takahirom.roborazzi.captureRoboImage
import com.gondroid.subtrack.core.designsystem.components.list.SettingsDivider
import com.gondroid.subtrack.core.designsystem.components.list.SettingsGroup
import com.gondroid.subtrack.core.designsystem.components.list.SettingsRow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.AccentPurpleBg
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.SubscriptionParticipation
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.feature.createsubscription.steps.WhoPaysStep
import com.gondroid.subtrack.feature.people.components.PersonRow
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.CobranzaBar
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.TimelineLegend
import com.gondroid.subtrack.feature.subscriptiondetail.components.PaymentHistoryRow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Screenshot tests for the components added in the UX batch.
 * Record:  ./gradlew recordRoborazziDebug   · Verify: ./gradlew verifyRoborazziDebug
 * Reference images live in app/src/test/screenshots/.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "es-rPE-w411dp-h891dp-xxhdpi")
class ComponentScreenshotTest {

    private fun snap(name: String, content: @Composable () -> Unit) =
        captureRoboImage("src/test/screenshots/$name.png") {
            SubTrackTheme {
                Box(Modifier.background(BgApp).padding(Spacing.base)) { content() }
            }
        }

    private fun person(name: String, debt: Double, paused: Boolean = false) = PersonSummary(
        userId = null, name = name, phone = "999", hasApp = false, totalDebt = debt,
        totalMonthlyContribution = 8.97, punctualityPercent = 80, firstJoinedAt = 0, lastActivityAt = null,
        subscriptions = listOf(SubscriptionParticipation("s", "Netflix", "#E50914", PaymentStatus.OVERDUE, 8.97, false, paused))
    )

    @Test fun whoPaysStep_familyPreselected() = snap("who_pays_shared") {
        WhoPaysStep(serviceName = "Spotify", isShared = true, suggestsShared = true, onSelect = {})
    }

    @Test fun personRow_debtAndPaused() = snap("person_rows") {
        androidx.compose.foundation.layout.Column {
            PersonRow(person = person("Ana Paredes", 8.97), onClick = {})
            PersonRow(person = person("Luis Torres", 0.0, paused = true), onClick = {}, modifier = Modifier.padding(top = Spacing.s))
        }
    }

    @Test fun cobranzaBar_singleName() = snap("cobranza_single") {
        CobranzaBar(pendingCount = 1, singlePendingName = "Ana", totalAmount = 8.97, onClick = {})
    }

    @Test fun settingsGroup() = snap("settings_group") {
        SettingsGroup {
            SettingsRow(Icons.Outlined.NotificationsNone, AccentAmberBg, AccentAmber, "Notificaciones", onClick = {})
            SettingsDivider()
            SettingsRow(Icons.Outlined.Public, AccentPurpleBg, AccentPurple, "País y moneda", value = "Perú · S/ PEN")
        }
    }

    @Test fun paymentHistoryAndLegend() = snap("payment_history") {
        androidx.compose.foundation.layout.Column {
            PaymentHistoryRow(Payment("1", "s", "m", "2025-10", 8.97, PaymentStatus.LATE, 1_760_000_000_000, null, null))
            TimelineLegend()
        }
    }
}
