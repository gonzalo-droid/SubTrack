package com.gondroid.subtrack.feature.subscriptionlist.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.AvatarStack
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
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
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.BillingCycle

@Composable
fun SubscriptionRow(
    subscription: Subscription,
    isAdmin: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        ServiceLogo(
            serviceName = subscription.name,
            brandColor = subscription.brandColor.toComposeColor(),
            size = 44.dp,
            serviceId = subscription.serviceTemplateId
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Text(
                    text = subscription.name,
                    style = SubTrackType.titleL,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (subscription.isShared) {
                    Text(
                        text = if (isAdmin) stringResource(R.string.subscription_list_tag_admin)
                               else stringResource(R.string.subscription_list_tag_member),
                        style = SubTrackType.monoXS,
                        color = AccentGreen,
                        modifier = Modifier
                            .clip(SubTrackShapes.circle)
                            .background(AccentGreenBg)
                            .padding(horizontal = Spacing.xs, vertical = 2.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Text(
                    text = cycleLabel(subscription.cycle, subscription.cutoffDay),
                    style = SubTrackType.monoXS,
                    color = TextTertiary
                )
                if (subscription.isShared && subscription.members.isNotEmpty()) {
                    Text("·", style = SubTrackType.monoXS, color = TextTertiary)
                    AvatarStack(
                        names = subscription.members.map { it.name },
                        maxVisible = 3,
                        size = 16.dp
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "S/ ${"%.2f".format(subscription.totalAmount)}",
                style = SubTrackType.monoS,
                color = TextPrimary
            )
            Text(
                text = cycleShort(subscription.cycle),
                style = SubTrackType.monoXS,
                color = TextSecondary
            )
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun cycleLabel(cycle: BillingCycle, cutoffDay: Int): String = when (cycle) {
    BillingCycle.MONTHLY -> "Día $cutoffDay"
    BillingCycle.YEARLY -> "Anual · Día $cutoffDay"
    else -> "Día $cutoffDay"
}

private fun cycleShort(cycle: BillingCycle): String = when (cycle) {
    BillingCycle.MONTHLY -> "/mes"
    BillingCycle.YEARLY -> "/año"
    else -> ""
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SubscriptionRowPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base), verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
            SubscriptionRow(
                subscription = com.gondroid.subtrack.data.mock.MockData.subscriptions.first(),
                isAdmin = true,
                onClick = {}
            )
            SubscriptionRow(
                subscription = com.gondroid.subtrack.data.mock.MockData.subscriptions[2],
                isAdmin = true,
                onClick = {}
            )
        }
    }
}
