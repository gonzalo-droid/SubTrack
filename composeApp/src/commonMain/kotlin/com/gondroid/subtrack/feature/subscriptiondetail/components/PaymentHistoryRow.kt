package com.gondroid.subtrack.feature.subscriptiondetail.components

import com.gondroid.subtrack.core.util.asMoney

import com.gondroid.subtrack.core.util.dayMonthYear


import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.member_history_paid_on
import com.gondroid.subtrack.resources.month_names

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.text.StatusPill
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

/** One month of a member's payments: "marzo 2026", paid date, amount and status. */
@Composable
fun PaymentHistoryRow(payment: Payment, modifier: Modifier = Modifier) {
    val months = stringArrayResource(Res.array.month_names)
    val monthLabel = payment.monthKey.split("-").let { parts ->
        val month = parts.getOrNull(1)?.toIntOrNull()
        if (parts.size == 2 && month != null && month in 1..12) "${months[month - 1]} ${parts[0]}" else payment.monthKey
    }
    val paidOn = payment.paidAt?.let { dayMonthYear(it) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(vertical = Spacing.s)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(monthLabel.replaceFirstChar { it.uppercase() }, style = SubTrackType.bodyS, color = TextPrimary)
            if (paidOn != null) {
                Text(
                    stringResource(Res.string.member_history_paid_on, paidOn),
                    style = SubTrackType.bodyXS,
                    color = TextTertiary
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            Text(payment.amount.asMoney(), style = SubTrackType.monoS, color = TextSecondary)
            StatusPill(status = payment.status)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun PaymentHistoryRowPreview() {
    SubTrackTheme {
        PaymentHistoryRow(
            Payment("p1", "sub", "mem", "2026-03", 13.73, PaymentStatus.PAID, 1_772_000_000_000, null, null),
            modifier = Modifier.padding(Spacing.base)
        )
    }
}
