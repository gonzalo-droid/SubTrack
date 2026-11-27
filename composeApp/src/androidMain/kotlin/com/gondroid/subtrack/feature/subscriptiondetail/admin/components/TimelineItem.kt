package com.gondroid.subtrack.feature.subscriptiondetail.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.MemberPaymentStatus
import com.gondroid.subtrack.domain.model.MonthlyPaymentSummary
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private val DOT_SIZE = 20.dp

@Composable
fun TimelineItem(
    summary: MonthlyPaymentSummary,
    modifier: Modifier = Modifier
) {
    val isComplete = summary.memberStatuses.all {
        it.status == PaymentStatus.PAID || it.status == PaymentStatus.LATE
    }
    val statusLabel = if (isComplete)
        stringResource(R.string.subscription_detail_month_complete)
    else
        stringResource(R.string.subscription_detail_month_partial)
    val statusColor = if (isComplete) AccentGreen else AccentAmber

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = summary.monthKey.toMonthAbbrev(),
            style = SubTrackType.monoXS,
            color = TextTertiary,
            modifier = Modifier.width(44.dp)
        )
        Spacer(Modifier.width(Spacing.s))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            summary.memberStatuses.forEach { ms ->
                StatusDot(memberStatus = ms)
            }
        }
        Text(
            text = statusLabel,
            style = SubTrackType.monoXS,
            color = statusColor
        )
    }
}

private enum class DotKind { PAID, OWES, NONE }

private fun MemberPaymentStatus.kind() = when (status) {
    PaymentStatus.PAID, PaymentStatus.LATE -> DotKind.PAID
    PaymentStatus.OVERDUE, PaymentStatus.PENDING -> DotKind.OWES
    null -> DotKind.NONE
}

@Composable
private fun StatusDot(memberStatus: MemberPaymentStatus) {
    val kind = memberStatus.kind()
    val description = stringResource(
        R.string.timeline_dot_cd,
        memberStatus.memberName,
        stringResource(kind.labelRes())
    )
    KindDot(kind, Modifier.semantics { contentDescription = description })
}

private fun DotKind.labelRes() = when (this) {
    DotKind.PAID -> R.string.timeline_legend_paid
    DotKind.OWES -> R.string.timeline_legend_owes
    DotKind.NONE -> R.string.timeline_legend_none
}

@Composable
private fun KindDot(kind: DotKind, modifier: Modifier = Modifier) {
    val (bg, label) = when (kind) {
        DotKind.PAID -> Pair(AccentGreen, "")
        DotKind.OWES -> Pair(AccentRed, "!")
        DotKind.NONE -> Pair(TextTertiary.copy(alpha = 0.4f), "–")
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(DOT_SIZE)
            .clip(SubTrackShapes.circle)
            .background(bg.copy(alpha = 0.2f))
    ) {
        if (label.isNotEmpty()) {
            Text(text = label, style = SubTrackType.monoXS, color = bg)
        } else {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(SubTrackShapes.circle)
                    .background(bg)
            )
        }
    }
}

/** What each history dot means (B4): shown once under the history list. */
@Composable
fun TimelineLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clearAndSetSemantics { },
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DotKind.entries.forEach { kind ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                KindDot(kind)
                Text(stringResource(kind.labelRes()), style = SubTrackType.monoXS, color = TextSecondary)
            }
        }
    }
}

private fun String.toMonthAbbrev(): String {
    val parts = split("-")
    if (parts.size < 2) return this
    return when (parts[1]) {
        "01" -> "Ene"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Abr"
        "05" -> "May"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Ago"
        "09" -> "Sep"; "10" -> "Oct"; "11" -> "Nov"; "12" -> "Dic"
        else -> parts[1]
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun TimelineItemPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            TimelineItem(
                summary = MonthlyPaymentSummary(
                    monthKey = "2026-02",
                    totalCollected = 21.96,
                    memberStatuses = listOf(
                        MemberPaymentStatus("1", "María", PaymentStatus.LATE),
                        MemberPaymentStatus("2", "Lucía", PaymentStatus.PAID),
                        MemberPaymentStatus("3", "Roberto", PaymentStatus.PAID)
                    )
                )
            )
            TimelineItem(
                summary = MonthlyPaymentSummary(
                    monthKey = "2026-03",
                    totalCollected = 32.94,
                    memberStatuses = listOf(
                        MemberPaymentStatus("1", "María", PaymentStatus.PAID),
                        MemberPaymentStatus("2", "Lucía", PaymentStatus.PAID),
                        MemberPaymentStatus("3", "Roberto", PaymentStatus.PAID)
                    )
                )
            )
            TimelineItem(
                summary = MonthlyPaymentSummary(
                    monthKey = "2026-04",
                    totalCollected = 10.98,
                    memberStatuses = listOf(
                        MemberPaymentStatus("1", "María", PaymentStatus.OVERDUE),
                        MemberPaymentStatus("2", "Lucía", PaymentStatus.PAID),
                        MemberPaymentStatus("3", "Roberto", PaymentStatus.PENDING)
                    )
                )
            )
        }
    }
}
