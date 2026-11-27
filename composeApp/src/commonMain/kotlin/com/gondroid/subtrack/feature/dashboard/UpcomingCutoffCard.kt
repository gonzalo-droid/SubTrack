package com.gondroid.subtrack.feature.dashboard

import com.gondroid.subtrack.core.designsystem.theme.BrandWhatsApp
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.dashboard_billing_custom
import com.gondroid.subtrack.resources.dashboard_billing_monthly
import com.gondroid.subtrack.resources.dashboard_billing_yearly
import com.gondroid.subtrack.resources.dashboard_cutoff_days
import com.gondroid.subtrack.resources.dashboard_cutoff_today
import com.gondroid.subtrack.resources.dashboard_cutoff_tomorrow
import com.gondroid.subtrack.resources.dashboard_cutoff_type_personal
import com.gondroid.subtrack.resources.dashboard_cutoff_type_shared
import com.gondroid.subtrack.resources.dashboard_debtor_detail
import com.gondroid.subtrack.resources.dashboard_debtors_see_all
import com.gondroid.subtrack.resources.dashboard_debtors_title
import com.gondroid.subtrack.resources.dashboard_personal_see_all
import com.gondroid.subtrack.resources.dashboard_personal_title
import com.gondroid.subtrack.resources.dashboard_reminder_cd
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.outlined.List
import com.gondroid.subtrack.core.designsystem.icons.CustomIcons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.avatar.AvatarStack
import com.gondroid.subtrack.core.designsystem.components.avatar.LogoSize
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.domain.model.DebtorInfo
import com.gondroid.subtrack.domain.model.Insight
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.BillingCycle

@Composable
internal fun UpcomingCutoffCard(
    subscription: Subscription,
    daysUntil: Int,
    onClick: () -> Unit
) {
    val urgencyColor = when {
        daysUntil == 0 -> AccentRed
        daysUntil <= 2 -> AccentAmber
        else -> null
    }
    val borderColor = urgencyColor?.copy(alpha = 0.4f) ?: BorderDefault
    val urgencyText = when {
        daysUntil == 0 -> stringResource(Res.string.dashboard_cutoff_today)
        daysUntil == 1 -> stringResource(Res.string.dashboard_cutoff_tomorrow)
        else -> pluralStringResource(Res.plurals.dashboard_cutoff_days, daysUntil, daysUntil)
    }

    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(SubTrackShapes.l)
            .border(1.dp, borderColor, SubTrackShapes.l)
            .background(BgSurface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(Spacing.m)
    ) {
        // Type chip
        Box(
            modifier = Modifier
                .clip(SubTrackShapes.circle)
                .background(
                    if (subscription.isShared) AccentGreenBg else AccentBlueBg
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = stringResource(
                    if (subscription.isShared) Res.string.dashboard_cutoff_type_shared
                    else Res.string.dashboard_cutoff_type_personal
                ).uppercase(),
                style = SubTrackType.monoXS,
                color = if (subscription.isShared) AccentGreen else AccentBlue
            )
        }
        Spacer(Modifier.height(Spacing.s))
        ServiceLogo(
            serviceName = subscription.name,
            brandColor = subscription.brandColor.toComposeColor(),
            size = LogoSize.Medium.dp,
            serviceId = subscription.serviceTemplateId
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = subscription.name,
            style = SubTrackType.titleL,
            color = TextPrimary,
            maxLines = 1
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = urgencyText,
            style = SubTrackType.monoXS,
            color = urgencyColor ?: TextTertiary
        )
        Spacer(Modifier.height(Spacing.s))
        AmountDisplay(amount = subscription.totalAmount, size = AmountSize.SMALL)
        if (subscription.isShared && subscription.members.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.s))
            AvatarStack(
                names = subscription.members.map { it.name },
                maxVisible = 3,
                size = 20.dp
            )
        }
    }
}

// ── Personal subscriptions section ───────────────────────────────────────────

@Composable
internal fun PersonalSubscriptionsSection(
    subscriptions: List<Subscription>,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = subscriptions.take(3)
    val hasMore = subscriptions.size > 3

    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(Res.string.dashboard_personal_title),
            count = subscriptions.size,
            actionLabel = stringResource(Res.string.dashboard_personal_see_all),
            onAction = {},
            showAction = hasMore
        )
        Spacer(Modifier.height(Spacing.m))
        visible.forEachIndexed { index, sub ->
            PersonalSubRow(
                subscription = sub,
                onClick = { onNavigateToDetail(sub.id) }
            )
            if (index < visible.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 52.dp),
                    color = BorderDefault,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
internal fun PersonalSubRow(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val billingDetail = when (subscription.cycle) {
        BillingCycle.MONTHLY -> stringResource(Res.string.dashboard_billing_monthly, subscription.cutoffDay)
        BillingCycle.YEARLY -> stringResource(Res.string.dashboard_billing_yearly, subscription.cutoffDay)
        BillingCycle.CUSTOM -> stringResource(Res.string.dashboard_billing_custom, subscription.cutoffDay)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ServiceLogo(
            serviceName = subscription.name,
            brandColor = subscription.brandColor.toComposeColor(),
            size = LogoSize.Medium.dp,
            serviceId = subscription.serviceTemplateId
        )
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = subscription.name, style = SubTrackType.titleL, color = TextPrimary)
            Text(text = billingDetail, style = SubTrackType.monoXS, color = TextTertiary)
        }
        AmountDisplay(amount = subscription.totalAmount, size = AmountSize.SMALL)
    }
}

// ── Debtors section ───────────────────────────────────────────────────────────

@Composable
internal fun DebtorsSection(
    debtors: List<DebtorInfo>,
    sharedSubscriptions: List<Subscription>,
    onNavigateToDetail: (String) -> Unit,
    onReminderTap: (memberId: String, subscriptionId: String, phone: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = debtors.take(3)
    val hasMore = debtors.size > 3

    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(Res.string.dashboard_debtors_title),
            count = debtors.size,
            actionLabel = stringResource(Res.string.dashboard_debtors_see_all),
            onAction = {},
            showAction = hasMore
        )
        Spacer(Modifier.height(Spacing.m))
        visible.forEachIndexed { index, debtor ->
            val sub = sharedSubscriptions.find { it.id == debtor.subscriptionId }
            DebtorRow(
                debtor = debtor,
                cutoffDay = sub?.cutoffDay ?: 0,
                onClick = { onNavigateToDetail(debtor.subscriptionId) },
                onWhatsApp = {
                    onReminderTap(debtor.memberId, debtor.subscriptionId, debtor.memberPhone)
                }
            )
            if (index < visible.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 52.dp),
                    color = BorderDefault,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
internal fun DebtorRow(
    debtor: DebtorInfo,
    cutoffDay: Int,
    onClick: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = debtor.memberName, size = 36.dp)
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = debtor.memberName, style = SubTrackType.titleL, color = TextPrimary)
            Text(
                text = stringResource(
                    Res.string.dashboard_debtor_detail,
                    debtor.subscriptionName,
                    cutoffDay
                ),
                style = SubTrackType.monoXS,
                color = AccentAmber
            )
        }
        AmountDisplay(
            amount = debtor.shareAmount,
            size = AmountSize.SMALL,
            color = AccentAmber
        )
        Spacer(Modifier.width(Spacing.s))
        STIconButton(
            icon = CustomIcons.WhatsApp,
            contentDescription = stringResource(Res.string.dashboard_reminder_cd),
            onClick = onWhatsApp,
            iconTint = BrandWhatsApp,
            containerColor = BrandWhatsApp.copy(alpha = 0.12f)
        )
    }
}

// ── Insight card ──────────────────────────────────────────────────────────────

