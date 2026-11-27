package com.gondroid.subtrack.feature.dashboard

import com.gondroid.subtrack.resources.month_names
import com.gondroid.subtrack.resources.weekday_names
import org.jetbrains.compose.resources.stringArrayResource
import kotlinx.datetime.LocalDate
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.dashboard_greeting
import com.gondroid.subtrack.resources.dashboard_hero_counts_format
import com.gondroid.subtrack.resources.dashboard_hero_debtors_suffix
import com.gondroid.subtrack.resources.dashboard_hero_personal
import com.gondroid.subtrack.resources.dashboard_hero_services
import com.gondroid.subtrack.resources.dashboard_hero_services_debtors_prefix
import com.gondroid.subtrack.resources.dashboard_hero_shared
import com.gondroid.subtrack.resources.dashboard_hero_upcoming
import com.gondroid.subtrack.resources.dashboard_net_badge
import com.gondroid.subtrack.resources.dashboard_notifications_cd
import com.gondroid.subtrack.resources.dashboard_total_monthly
import com.gondroid.subtrack.resources.dashboard_upcoming_see_all
import com.gondroid.subtrack.resources.dashboard_upcoming_title
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.cards.HeroCard
import com.gondroid.subtrack.core.designsystem.components.cards.SurfaceCard
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.util.daysUntilCutoff
import androidx.compose.material3.Icon

@Composable
internal fun DashboardHeader(
    user: com.gondroid.subtrack.domain.model.User,
    today: LocalDate,
    onNotificationsClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.base, vertical = Spacing.l),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Eyebrow(
                text = "${stringArrayResource(Res.array.weekday_names)[today.dayOfWeek.ordinal]} · " +
                    stringArrayResource(Res.array.month_names)[today.month.ordinal]
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(Res.string.dashboard_greeting, user.name.substringBefore(" ")),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
        }
        STIconButton(
            icon = Icons.Outlined.Notifications,
            contentDescription = stringResource(Res.string.dashboard_notifications_cd),
            onClick = onNotificationsClick,
            showBadge = true
        )
        Spacer(Modifier.width(Spacing.s))
        Avatar(
            name = user.name,
            size = 36.dp,
            modifier = Modifier.clip(SubTrackShapes.circle).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onAvatarClick
            )
        )
    }
}

// ── Dual hero ─────────────────────────────────────────────────────────────────

@Composable
internal fun DualHeroRow(
    data: DashboardData,
    selectedFilter: Int,
    onSelectFilter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // With large fonts (fontScale >= 1.3) two columns truncate the amounts: stack them.
    if (LocalDensity.current.fontScale >= LARGE_FONT_SCALE) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            PersonalHero(data, selectedFilter, onSelectFilter, Modifier.fillMaxWidth())
            SharedHero(data, selectedFilter, onSelectFilter, Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            PersonalHero(data, selectedFilter, onSelectFilter, Modifier.weight(1f))
            SharedHero(data, selectedFilter, onSelectFilter, Modifier.weight(1f))
        }
    }
}

@Composable
internal fun PersonalHero(
    data: DashboardData,
    selectedFilter: Int,
    onSelectFilter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(SubTrackShapes.xxl)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSelectFilter(if (selectedFilter == 1) 0 else 1) }
    ) {
        HeroCard(
            accentColor = AccentBlue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Eyebrow(
                text = stringResource(Res.string.dashboard_hero_personal),
                color = AccentBlue.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(Spacing.s))
            AmountDisplay(amount = data.personalAmount, size = AmountSize.MEDIUM)
            Spacer(Modifier.height(Spacing.xs))
            val upcomingPersonal = data.upcomingCutoffs.count { !it.isShared }
            Text(
                text = stringResource(
                    Res.string.dashboard_hero_counts_format,
                    pluralStringResource(Res.plurals.dashboard_hero_services, data.personalCount, data.personalCount),
                    pluralStringResource(Res.plurals.dashboard_hero_upcoming, upcomingPersonal, upcomingPersonal)
                ),
                style = SubTrackType.bodyS,
                color = TextSecondary
            )
        }
    }
}

@Composable
internal fun SharedHero(
    data: DashboardData,
    selectedFilter: Int,
    onSelectFilter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(SubTrackShapes.xxl)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSelectFilter(if (selectedFilter == 2) 0 else 2) }
    ) {
        HeroCard(
            accentColor = AccentGreen,
            modifier = Modifier.fillMaxWidth()
        ) {
            Eyebrow(
                text = stringResource(Res.string.dashboard_hero_shared),
                color = AccentGreen.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(Spacing.s))
            AmountDisplay(amount = data.sharedAmount, size = AmountSize.MEDIUM)
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = buildAnnotatedString {
                    append(
                        pluralStringResource(
                            Res.plurals.dashboard_hero_services_debtors_prefix,
                            data.sharedCount,
                            data.sharedCount
                        )
                    )
                    withStyle(
                        androidx.compose.ui.text.SpanStyle(color = AccentAmber)
                    ) { append("${data.debtorCount}") }
                    append(pluralStringResource(Res.plurals.dashboard_hero_debtors_suffix, data.debtorCount))
                },
                style = SubTrackType.bodyS,
                color = TextSecondary
            )
        }
    }
}

// ── Total summary row ─────────────────────────────────────────────────────────

@Composable
internal fun TotalSummaryRow(
    totalAmount: Double,
    netAmount: Double,
    modifier: Modifier = Modifier
) {
    val largeFont = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    SurfaceCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.List,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(Spacing.s))
            Text(
                text = stringResource(Res.string.dashboard_total_monthly),
                style = SubTrackType.titleM,
                color = TextSecondary
            )
            Spacer(Modifier.weight(1f))
            AmountDisplay(amount = totalAmount, size = AmountSize.SMALL)
            if (!largeFont) {
                Spacer(Modifier.width(Spacing.s))
                HorizontalDivider(
                    modifier = Modifier
                        .height(16.dp)
                        .width(1.dp),
                    color = BorderDefault
                )
                Spacer(Modifier.width(Spacing.s))
                NetAmount(netAmount)
            }
        }
        if (largeFont) {
            Spacer(Modifier.height(Spacing.s))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) { NetAmount(netAmount) }
        }
    }
}

@Composable
internal fun NetAmount(netAmount: Double) {
    Badge(
        text = stringResource(Res.string.dashboard_net_badge),
        variant = BadgeVariant.ADMIN
    )
    Spacer(Modifier.width(6.dp))
    AmountDisplay(amount = netAmount, size = AmountSize.SMALL, color = AccentGreen)
}

// ── Upcoming cutoffs section ──────────────────────────────────────────────────

@Composable
internal fun UpcomingSection(
    subscriptions: List<Subscription>,
    onNavigateToDetail: (String) -> Unit
) {
    Column {
        SectionHeader(
            title = stringResource(Res.string.dashboard_upcoming_title),
            count = subscriptions.size,
            actionLabel = stringResource(Res.string.dashboard_upcoming_see_all),
            onAction = {},
            modifier = Modifier.padding(horizontal = Spacing.base)
        )
        Spacer(Modifier.height(Spacing.m))
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.base),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            items(subscriptions, key = { it.id }) { sub ->
                UpcomingCutoffCard(
                    subscription = sub,
                    daysUntil = daysUntilCutoff(sub.cutoffDay),
                    onClick = { onNavigateToDetail(sub.id) }
                )
            }
        }
    }
}

