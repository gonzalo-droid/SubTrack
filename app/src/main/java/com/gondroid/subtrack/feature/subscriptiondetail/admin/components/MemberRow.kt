package com.gondroid.subtrack.feature.subscriptiondetail.admin.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.StatusPill
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

@Composable
fun MemberRow(
    member: Member,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = member.name, size = 40.dp)
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = member.name, style = SubTrackType.titleL, color = TextPrimary)
            val profileText = member.profileLabel
                ?: stringResource(R.string.member_no_profile)
            Text(text = profileText, style = SubTrackType.monoXS, color = TextTertiary)
        }
        AmountDisplay(amount = member.shareAmount, size = AmountSize.SMALL)
        Spacer(Modifier.width(Spacing.s))
        StatusPill(status = member.currentStatus)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun MemberRowPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            MemberRow(
                member = Member(
                    id = "1", userId = null, name = "María Rodríguez",
                    phone = "+51 912 345 678", profileLabel = "Perfil 2",
                    shareAmount = 10.98, currentStatus = PaymentStatus.OVERDUE,
                    joinedAt = 0L
                ),
                onClick = {}
            )
            MemberRow(
                member = Member(
                    id = "2", userId = null, name = "Lucía Paredes",
                    phone = "+51 956 789 012", profileLabel = "Perfil 4",
                    shareAmount = 10.98, currentStatus = PaymentStatus.PAID,
                    joinedAt = 0L
                ),
                onClick = {}
            )
        }
    }
}
