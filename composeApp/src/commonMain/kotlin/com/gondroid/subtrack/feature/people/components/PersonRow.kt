package com.gondroid.subtrack.feature.people.components

import com.gondroid.subtrack.domain.util.formatTwoDecimals

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.people_has_app_tag
import com.gondroid.subtrack.resources.people_monthly_label
import com.gondroid.subtrack.resources.people_owes_label
import com.gondroid.subtrack.resources.people_unknown_admin
import com.gondroid.subtrack.resources.subscription_paused_tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.SubscriptionParticipation
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import androidx.compose.ui.draw.alpha
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant

/** Opacity for people whose shared subscriptions are all paused. */
internal const val PAUSED_ALPHA = 0.5f

@Composable
fun PersonRow(
    person: PersonSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    debtLabel: String = stringResource(Res.string.people_owes_label)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(Spacing.m)
            // Everything with this person is paused: dimmed, the admin can delete it from the detail
            .alpha(if (person.isAllPaused) PAUSED_ALPHA else 1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        val displayName = person.name.ifBlank { stringResource(Res.string.people_unknown_admin) }
        Avatar(name = displayName, size = 44.dp)

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayName,
                    style = SubTrackType.titleL,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (person.hasApp) {
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.people_has_app_tag),
                        style = SubTrackType.monoXS,
                        color = AccentGreen,
                        modifier = Modifier
                            .clip(SubTrackShapes.circle)
                            .background(AccentGreenBg)
                            .padding(horizontal = Spacing.xs, vertical = 2.dp)
                    )
                }
            }
            val active = person.subscriptions.filter { !it.isArchived }
            if (active.isNotEmpty()) {
                Text(
                    text = active.joinToString(" · ") { it.subscriptionName },
                    style = SubTrackType.monoXS,
                    color = TextTertiary,
                    maxLines = 1
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            if (person.isAllPaused) {
                Badge(text = stringResource(Res.string.subscription_paused_tag), variant = BadgeVariant.NEUTRAL)
            } else if (person.totalDebt > 0) {
                Text(
                    text = "S/ ${formatTwoDecimals(person.totalDebt)}",
                    style = SubTrackType.monoS,
                    color = com.gondroid.subtrack.core.designsystem.theme.AccentRed
                )
                Text(
                    text = debtLabel,
                    style = SubTrackType.monoXS,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = "S/ ${formatTwoDecimals(person.totalMonthlyContribution)}",
                    style = SubTrackType.monoS,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(Res.string.people_monthly_label),
                    style = SubTrackType.monoXS,
                    color = TextTertiary
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun PersonRowPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base), verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
            PersonRow(
                person = PersonSummary(
                    userId = "usr_maria",
                    name = "María Rodríguez",
                    phone = "+51 912 345 678",
                    hasApp = true,
                    totalDebt = 10.98,
                    totalMonthlyContribution = 10.98,
                    punctualityPercent = 67,
                    firstJoinedAt = 0L,
                    lastActivityAt = null,
                    subscriptions = listOf(
                        SubscriptionParticipation("sub_netflix", "Netflix", "#E50914", PaymentStatus.OVERDUE, 10.98, false)
                    )
                ),
                onClick = {}
            )
            PersonRow(
                person = PersonSummary(
                    userId = null,
                    name = "Roberto Vargas",
                    phone = "+51 978 901 234",
                    hasApp = false,
                    totalDebt = 0.0,
                    totalMonthlyContribution = 10.98,
                    punctualityPercent = 100,
                    firstJoinedAt = 0L,
                    lastActivityAt = null,
                    subscriptions = listOf(
                        SubscriptionParticipation("sub_netflix", "Netflix", "#E50914", PaymentStatus.PENDING, 10.98, false)
                    )
                ),
                onClick = {}
            )
        }
    }
}
