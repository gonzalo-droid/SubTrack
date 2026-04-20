package com.gondroid.subtrack.feature.subscriptiondetail.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

@Composable
fun ArchivedMemberRow(
    member: Member,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.m)
            .background(BgSurfaceEl)
            .drawBehind {
                val stripeColor = AccentAmberBg
                val step = 12.dp.toPx()
                var x = -size.height
                while (x < size.width + size.height) {
                    drawLine(
                        color = stripeColor,
                        start = Offset(x, 0f),
                        end = Offset(x + size.height, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                    x += step
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.m, vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = member.name, size = 40.dp, modifier = Modifier.then(Modifier.then(
            Modifier // opacity applied via color in Avatar — use graphicsLayer alpha
        )))
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = member.name,
                    style = SubTrackType.titleL,
                    color = TextTertiary
                )
                Spacer(Modifier.width(Spacing.xs))
                Badge(
                    text = stringResource(R.string.subscription_detail_archived_tag),
                    variant = BadgeVariant.PRO
                )
            }
            val profileText = member.profileLabel
                ?: stringResource(R.string.member_no_profile)
            Text(text = profileText, style = SubTrackType.monoXS, color = TextTertiary)
        }
        AmountDisplay(amount = member.shareAmount, size = AmountSize.SMALL, color = AccentRed)
        Spacer(Modifier.width(Spacing.s))
        Badge(
            text = stringResource(R.string.subscription_detail_archived_status),
            variant = BadgeVariant.ERROR
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ArchivedMemberRowPreview() {
    SubTrackTheme {
        ArchivedMemberRow(
            member = Member(
                id = "mem_carlos", userId = null, name = "Carlos Lima",
                phone = "+51 934 567 890", profileLabel = "Perfil 3",
                shareAmount = 10.98, isArchived = true,
                currentStatus = PaymentStatus.OVERDUE, joinedAt = 0L
            ),
            onClick = {},
            modifier = Modifier.padding(Spacing.base)
        )
    }
}
