package com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.text.StatusPill
import com.gondroid.subtrack.core.designsystem.icons.CustomIcons
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun EditMemberSheet(
    member: Member,
    subscriptionName: String,
    onSendReminder: () -> Unit,
    onMarkAsPaid: () -> Unit,
    onEditData: () -> Unit,
    onViewHistory: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    val monthsInGroup = run {
        val now = Calendar.getInstance().timeInMillis
        val diff = now - member.joinedAt
        (TimeUnit.MILLISECONDS.toDays(diff) / 30).toInt().coerceAtLeast(1)
    }
    val hasPendingDebt = member.currentStatus == PaymentStatus.OVERDUE ||
            member.currentStatus == PaymentStatus.PENDING

    SheetContainer {
        Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
            SheetHandle()
            Spacer(Modifier.height(Spacing.s))

            // Eyebrow
            Text(
                text = stringResource(R.string.member_sheet_edit_eyebrow, subscriptionName),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
            Spacer(Modifier.height(Spacing.m))

            // Hero
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(name = member.name, size = 56.dp)
                Spacer(Modifier.width(Spacing.m))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = member.name, style = SubTrackType.headlineM, color = TextPrimary)
                    Text(text = member.phone, style = SubTrackType.monoXS, color = TextTertiary)
                    member.profileLabel?.let {
                        Text(text = it, style = SubTrackType.monoXS, color = TextTertiary)
                    }
                }
                StatusPill(status = member.currentStatus)
            }
            Spacer(Modifier.height(Spacing.m))

            // Quick stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.base)
                    .background(BgSurfaceEl)
                    .padding(Spacing.m),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = if (hasPendingDebt) "S/ ${"%.2f".format(member.shareAmount)}" else "—",
                    label = "Debe",
                    valueColor = if (hasPendingDebt) AccentAmber else TextTertiary
                )
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(BorderDefault))
                StatItem(value = "$monthsInGroup", label = "Meses")
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(BorderDefault))
                StatItem(value = "—", label = "Puntual")
            }
            Spacer(Modifier.height(Spacing.m))

            // Actions
            ActionRow(
                icon = CustomIcons.WhatsApp,
                iconColor = Color(0xFF25D366),
                iconBg = Color(0xFF25D366).copy(alpha = 0.12f),
                title = stringResource(R.string.member_sheet_edit_action_remind),
                subtitle = stringResource(R.string.member_sheet_edit_action_remind_sub),
                onClick = onSendReminder
            )
            ActionRow(
                icon = Icons.Outlined.CheckCircle,
                iconColor = AccentBlue,
                iconBg = AccentBlue.copy(alpha = 0.12f),
                title = stringResource(R.string.member_sheet_edit_action_paid),
                subtitle = stringResource(R.string.member_sheet_edit_action_paid_sub),
                onClick = onMarkAsPaid
            )
            ActionRow(
                icon = Icons.Outlined.Edit,
                iconColor = AccentAmber,
                iconBg = AccentAmber.copy(alpha = 0.12f),
                title = stringResource(R.string.member_sheet_edit_action_data),
                subtitle = stringResource(R.string.member_sheet_edit_action_data_sub),
                onClick = onEditData
            )
            ActionRow(
                icon = Icons.Outlined.History,
                iconColor = AccentPurple,
                iconBg = AccentPurple.copy(alpha = 0.12f),
                title = stringResource(R.string.member_sheet_edit_action_history),
                subtitle = stringResource(R.string.member_sheet_edit_action_history_sub),
                onClick = onViewHistory
            )
            ActionRow(
                icon = Icons.Outlined.Delete,
                iconColor = AccentRed,
                iconBg = AccentRed.copy(alpha = 0.12f),
                title = stringResource(R.string.member_sheet_edit_action_remove),
                subtitle = stringResource(R.string.member_sheet_edit_action_remove_sub),
                onClick = onRemove
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, valueColor: Color = TextPrimary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = SubTrackType.headlineS, color = valueColor)
        Text(text = label, style = SubTrackType.monoXS, color = TextTertiary)
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(SubTrackShapes.m)
                .background(iconBg)
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = SubTrackType.titleL, color = TextPrimary)
            Text(text = subtitle, style = SubTrackType.bodyS, color = TextSecondary)
        }
        Icon(
            Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun EditMemberSheetPreview() {
    SubTrackTheme {
        Box(modifier = Modifier.background(BgSheet)) {
            EditMemberSheet(
                member = Member(
                    id = "mem_maria", userId = null, name = "María Rodríguez",
                    phone = "+51 912 345 678", profileLabel = "Perfil 2",
                    shareAmount = 10.98, currentStatus = PaymentStatus.OVERDUE,
                    joinedAt = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
                ),
                subscriptionName = "Netflix",
                onSendReminder = {}, onMarkAsPaid = {}, onEditData = {},
                onViewHistory = {}, onRemove = {}, onDismiss = {}
            )
        }
    }
}
