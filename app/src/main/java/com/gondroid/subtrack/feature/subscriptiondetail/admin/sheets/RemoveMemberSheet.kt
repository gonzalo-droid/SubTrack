package com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.SecondaryButton
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.enums.DebtDecision
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

@Composable
fun RemoveMemberSheet(
    member: Member,
    onConfirm: (DebtDecision) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf(DebtDecision.ARCHIVE_WITH_DEBT) }

    SheetContainer {
        Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
            SheetHandle()
            Spacer(Modifier.height(Spacing.m))

            Eyebrow(text = stringResource(R.string.member_sheet_remove_eyebrow))
            Spacer(Modifier.height(Spacing.m))

            // Warning hero
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.l)
                    .background(AccentAmberBg)
                    .padding(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = AccentAmber,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(Spacing.m))
                Column {
                    Text(
                        text = stringResource(
                            R.string.member_sheet_remove_warning_title,
                            member.name,
                            "%.2f".format(member.shareAmount)
                        ),
                        style = SubTrackType.headlineS,
                        color = AccentAmber
                    )
                    Text(
                        text = stringResource(R.string.member_sheet_remove_warning_sub, member.name),
                        style = SubTrackType.bodyS,
                        color = AccentAmber.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(Modifier.height(Spacing.l))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                DecisionCard(
                    title = stringResource(R.string.member_sheet_remove_archive_title),
                    subtitle = stringResource(R.string.member_sheet_remove_archive_sub),
                    selected = selected == DebtDecision.ARCHIVE_WITH_DEBT,
                    onClick = { selected = DebtDecision.ARCHIVE_WITH_DEBT }
                )
                DecisionCard(
                    title = stringResource(R.string.member_sheet_remove_forgive_title),
                    subtitle = stringResource(
                        R.string.member_sheet_remove_forgive_sub,
                        "%.2f".format(member.shareAmount)
                    ),
                    selected = selected == DebtDecision.FORGIVE,
                    onClick = { selected = DebtDecision.FORGIVE }
                )
                DecisionCard(
                    title = stringResource(R.string.member_sheet_remove_transfer_title),
                    subtitle = stringResource(R.string.member_sheet_remove_transfer_sub),
                    selected = selected == DebtDecision.TRANSFER_TO_NEW,
                    onClick = { selected = DebtDecision.TRANSFER_TO_NEW }
                )
            }

            Spacer(Modifier.height(Spacing.l))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.member_sheet_cancel),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                val confirmText = when (selected) {
                    DebtDecision.ARCHIVE_WITH_DEBT -> stringResource(R.string.member_sheet_remove_confirm_archive)
                    DebtDecision.FORGIVE -> stringResource(R.string.member_sheet_remove_confirm_forgive)
                    DebtDecision.TRANSFER_TO_NEW -> stringResource(R.string.member_sheet_remove_confirm_transfer)
                }
                PrimaryButton(
                    text = confirmText,
                    onClick = { onConfirm(selected) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun RemoveMemberSheetPreview() {
    SubTrackTheme {
        RemoveMemberSheet(
            member = Member(
                id = "mem_maria", userId = null, name = "María Rodríguez",
                phone = "+51 912 345 678", profileLabel = "Perfil 2",
                shareAmount = 10.98, currentStatus = PaymentStatus.OVERDUE, joinedAt = 0L
            ),
            onConfirm = {}, onDismiss = {}
        )
    }
}
