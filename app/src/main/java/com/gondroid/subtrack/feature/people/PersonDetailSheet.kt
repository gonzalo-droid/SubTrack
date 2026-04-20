package com.gondroid.subtrack.feature.people

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.text.StatusPill
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.domain.model.PersonSummary
import com.gondroid.subtrack.domain.model.SubscriptionParticipation
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailSheet(
    person: PersonSummary,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BgSheet
    ) {
        PersonDetailContent(person = person)
    }
}

@Composable
private fun PersonDetailContent(person: PersonSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base)
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Avatar(name = person.name, size = 56.dp)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = person.name,
                        style = SubTrackType.headlineM,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (person.hasApp) {
                        Spacer(Modifier.width(Spacing.xs))
                        Text(
                            text = stringResource(R.string.people_has_app_tag),
                            style = SubTrackType.monoXS,
                            color = AccentGreen,
                            modifier = Modifier
                                .clip(SubTrackShapes.circle)
                                .background(AccentGreenBg)
                                .padding(horizontal = Spacing.xs, vertical = 2.dp)
                        )
                    }
                }
                Text(text = person.phone, style = SubTrackType.bodyS, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(Spacing.l))

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PersonStatItem(
                label = stringResource(R.string.people_detail_punctuality),
                value = "${person.punctualityPercent}%"
            )
            Box(modifier = Modifier.height(32.dp).width(1.dp).background(BorderDefault))
            PersonStatItem(
                label = stringResource(R.string.people_detail_monthly),
                value = "S/ ${"%.2f".format(person.totalMonthlyContribution)}"
            )
            Box(modifier = Modifier.height(32.dp).width(1.dp).background(BorderDefault))
            PersonStatItem(
                label = stringResource(R.string.people_detail_since),
                value = formatDate(person.firstJoinedAt)
            )
        }

        Spacer(Modifier.height(Spacing.l))

        // Subscriptions section
        Text(
            text = stringResource(R.string.people_detail_subs_title).uppercase(),
            style = SubTrackType.monoXS,
            color = TextTertiary
        )
        Spacer(Modifier.height(Spacing.s))

        val active = person.subscriptions.filter { !it.isArchived }
        val archived = person.subscriptions.filter { it.isArchived }

        active.forEach { participation ->
            ParticipationRow(participation = participation)
            Spacer(Modifier.height(Spacing.s))
        }

        if (archived.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.people_detail_archived_label).uppercase(),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
            Spacer(Modifier.height(Spacing.s))
            archived.forEach { participation ->
                ParticipationRow(participation = participation)
                Spacer(Modifier.height(Spacing.s))
            }
        }

        Spacer(Modifier.height(Spacing.m))
    }
}

@Composable
private fun ParticipationRow(participation: SubscriptionParticipation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.base)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.base)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(SubTrackShapes.circle)
                .background(participation.brandColor.toComposeColor())
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(participation.subscriptionName, style = SubTrackType.titleL, color = TextPrimary)
            Text(
                "S/ ${"%.2f".format(participation.shareAmount)}/mes",
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
        }
        StatusPill(status = participation.status)
    }
}

@Composable
private fun PersonStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = SubTrackType.headlineS, color = TextPrimary)
        Text(label, style = SubTrackType.monoXS, color = TextTertiary)
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM yy", Locale("es", "PE"))
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun PersonDetailContentPreview() {
    SubTrackTheme {
        PersonDetailContent(
            person = PersonSummary(
                userId = "usr_maria",
                name = "María Rodríguez",
                phone = "+51 912 345 678",
                hasApp = true,
                totalDebt = 10.98,
                totalMonthlyContribution = 10.98,
                punctualityPercent = 67,
                firstJoinedAt = 1740787200000L,
                lastActivityAt = 1771545600000L,
                subscriptions = listOf(
                    SubscriptionParticipation("sub_netflix", "Netflix", "#E50914", PaymentStatus.OVERDUE, 10.98, false),
                    SubscriptionParticipation("sub_spotify", "Spotify", "#1DB954", PaymentStatus.PAID, 8.97, true)
                )
            )
        )
    }
}
