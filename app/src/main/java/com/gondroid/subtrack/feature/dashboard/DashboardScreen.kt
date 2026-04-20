package com.gondroid.subtrack.feature.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import com.gondroid.subtrack.core.designsystem.icons.CustomIcons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.avatar.AvatarStack
import com.gondroid.subtrack.core.designsystem.components.avatar.LogoSize
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.cards.HeroCard
import com.gondroid.subtrack.core.designsystem.components.cards.SurfaceCard
import com.gondroid.subtrack.core.designsystem.components.input.SegmentedSelector
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
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
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.WhatsAppHelper
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.domain.model.DebtorInfo
import com.gondroid.subtrack.domain.model.Insight
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import java.util.Calendar

// ── Public entry point ────────────────────────────────────────────────────────

@Composable
fun DashboardScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (val state = uiState) {
        is DashboardUiState.Loading -> DashboardSkeleton(modifier = modifier.fillMaxSize())
        is DashboardUiState.Empty -> DashboardEmptyState(
            onCreateSubscription = onNavigateToCreate,
            modifier = modifier.fillMaxSize()
        )
        is DashboardUiState.Success -> DashboardContent(
            data = state.data,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToProfile = onNavigateToProfile,
            onReminderTap = { memberId, subscriptionId, phone ->
                val debtor = state.data.debtors.find { it.memberId == memberId }
                val sub = state.data.sharedSubscriptions.find { it.id == subscriptionId }
                if (debtor != null && sub != null) {
                    val msg = WhatsAppHelper.buildReminderMessage(
                        memberName = debtor.memberName,
                        serviceName = sub.name,
                        amount = "%.2f".format(debtor.shareAmount),
                        adminName = state.data.user.name.substringBefore(" ")
                    )
                    WhatsAppHelper.openWhatsApp(context, phone, msg)
                    viewModel.onReminderSent(memberId, subscriptionId)
                }
            },
            modifier = modifier.fillMaxSize()
        )
    }
}

// ── Content (Success state) ───────────────────────────────────────────────────

@Composable
private fun DashboardContent(
    data: DashboardData,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onReminderTap: (memberId: String, subscriptionId: String, phone: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by rememberSaveable { mutableIntStateOf(0) }

    val showPersonal = selectedFilter != 2
    val showShared = selectedFilter != 1

    val filteredUpcoming = remember(data.upcomingCutoffs, selectedFilter) {
        when (selectedFilter) {
            1 -> data.upcomingCutoffs.filter { !it.isShared }
            2 -> data.upcomingCutoffs.filter { it.isShared }
            else -> data.upcomingCutoffs
        }
    }

    data class FilterTab(val label: String, val count: Int)

    Column(modifier = modifier) {
        // Fixed header
        DashboardHeader(
            user = data.user,
            dayLabel = data.dayLabel,
            onNotificationsClick = onNavigateToNotifications,
            onAvatarClick = onNavigateToProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Spacing.huge),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            // ── Segmented selector ────────────────────────────────────────
            item {
                val tabs = listOf(
                    FilterTab(stringResource(R.string.dashboard_tab_all), data.personalCount + data.sharedCount),
                    FilterTab(stringResource(R.string.dashboard_tab_personal), data.personalCount),
                    FilterTab(stringResource(R.string.dashboard_tab_shared), data.sharedCount)
                )
                SegmentedSelector(
                    options = tabs,
                    selectedIndex = selectedFilter,
                    onSelectionChange = { selectedFilter = it },
                    labelProvider = { it.label },
                    countProvider = { it.count },
                    modifier = Modifier.padding(horizontal = Spacing.base).fillMaxWidth()
                )
            }

            // ── Dual hero row ─────────────────────────────────────────────
            item {
                DualHeroRow(
                    data = data,
                    selectedFilter = selectedFilter,
                    onSelectFilter = { selectedFilter = it },
                    modifier = Modifier.padding(horizontal = Spacing.base)
                )
            }

            // ── Total summary ─────────────────────────────────────────────
            item {
                TotalSummaryRow(
                    totalAmount = data.totalMonthlySpend,
                    netAmount = data.personalAmount,
                    modifier = Modifier.padding(horizontal = Spacing.base)
                )
            }

            // ── Upcoming cutoffs ──────────────────────────────────────────
            if (filteredUpcoming.isNotEmpty()) {
                item {
                    UpcomingSection(
                        subscriptions = filteredUpcoming,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }

            // ── Personal subscriptions ────────────────────────────────────
            item {
                AnimatedVisibility(
                    visible = showPersonal && data.personalSubscriptions.isNotEmpty(),
                    enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                    exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                ) {
                    PersonalSubscriptionsSection(
                        subscriptions = data.personalSubscriptions,
                        onNavigateToDetail = onNavigateToDetail,
                        modifier = Modifier.padding(horizontal = Spacing.base)
                    )
                }
            }

            // ── Debtors ───────────────────────────────────────────────────
            item {
                AnimatedVisibility(
                    visible = showShared && data.debtors.isNotEmpty(),
                    enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                    exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                ) {
                    DebtorsSection(
                        debtors = data.debtors,
                        sharedSubscriptions = data.sharedSubscriptions,
                        onNavigateToDetail = onNavigateToDetail,
                        onReminderTap = onReminderTap,
                        modifier = Modifier.padding(horizontal = Spacing.base)
                    )
                }
            }

            // ── Insight card ──────────────────────────────────────────────
            data.insight?.let { insight ->
                item {
                    DashboardInsightCard(
                        insight = insight,
                        onNavigateToDetail = onNavigateToDetail,
                        modifier = Modifier.padding(horizontal = Spacing.base)
                    )
                }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader(
    user: com.gondroid.subtrack.domain.model.User,
    dayLabel: String,
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
            Eyebrow(text = dayLabel)
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.dashboard_greeting, user.name.substringBefore(" ")),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
        }
        STIconButton(
            icon = Icons.Outlined.Notifications,
            contentDescription = stringResource(R.string.dashboard_notifications_cd),
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
private fun DualHeroRow(
    data: DashboardData,
    selectedFilter: Int,
    onSelectFilter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        // Personal hero
        Box(
            modifier = Modifier
                .weight(1f)
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
                    text = stringResource(R.string.dashboard_hero_personal),
                    color = AccentBlue.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(Spacing.s))
                AmountDisplay(amount = data.personalAmount, size = AmountSize.MEDIUM)
                Spacer(Modifier.height(Spacing.xs))
                val upcomingPersonal = data.upcomingCutoffs.count { !it.isShared }
                Text(
                    text = stringResource(
                        R.string.dashboard_hero_services_upcoming,
                        data.personalCount,
                        upcomingPersonal
                    ),
                    style = SubTrackType.bodyS,
                    color = TextSecondary
                )
            }
        }

        // Shared hero
        Box(
            modifier = Modifier
                .weight(1f)
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
                    text = stringResource(R.string.dashboard_hero_shared),
                    color = AccentGreen.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(Spacing.s))
                AmountDisplay(amount = data.sharedAmount, size = AmountSize.MEDIUM)
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = buildAnnotatedString {
                        append(
                            stringResource(
                                R.string.dashboard_hero_services_debtors_prefix,
                                data.sharedCount
                            )
                        )
                        withStyle(
                            androidx.compose.ui.text.SpanStyle(color = AccentAmber)
                        ) { append("${data.debtorCount}") }
                        append(stringResource(R.string.dashboard_hero_debtors_suffix))
                    },
                    style = SubTrackType.bodyS,
                    color = TextSecondary
                )
            }
        }
    }
}

// ── Total summary row ─────────────────────────────────────────────────────────

@Composable
private fun TotalSummaryRow(
    totalAmount: Double,
    netAmount: Double,
    modifier: Modifier = Modifier
) {
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
                text = stringResource(R.string.dashboard_total_monthly),
                style = SubTrackType.titleM,
                color = TextSecondary
            )
            Spacer(Modifier.weight(1f))
            AmountDisplay(amount = totalAmount, size = AmountSize.SMALL)
            Spacer(Modifier.width(Spacing.s))
            HorizontalDivider(
                modifier = Modifier
                    .height(16.dp)
                    .width(1.dp),
                color = BorderDefault
            )
            Spacer(Modifier.width(Spacing.s))
            Badge(
                text = stringResource(R.string.dashboard_net_badge),
                variant = BadgeVariant.ADMIN
            )
            Spacer(Modifier.width(6.dp))
            AmountDisplay(amount = netAmount, size = AmountSize.SMALL, color = AccentGreen)
        }
    }
}

// ── Upcoming cutoffs section ──────────────────────────────────────────────────

@Composable
private fun UpcomingSection(
    subscriptions: List<Subscription>,
    onNavigateToDetail: (String) -> Unit
) {
    Column {
        SectionHeader(
            title = stringResource(R.string.dashboard_upcoming_title),
            count = subscriptions.size,
            actionLabel = stringResource(R.string.dashboard_upcoming_see_all),
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

@Composable
private fun UpcomingCutoffCard(
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
        daysUntil == 0 -> stringResource(R.string.dashboard_cutoff_today)
        daysUntil == 1 -> stringResource(R.string.dashboard_cutoff_tomorrow)
        else -> stringResource(R.string.dashboard_cutoff_days, daysUntil)
    }

    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(SubTrackShapes.l)
            .border(1.dp, borderColor, SubTrackShapes.l)
            .background(BgSurface)
            .clickable(onClick = onClick)
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
                    if (subscription.isShared) R.string.dashboard_cutoff_type_shared
                    else R.string.dashboard_cutoff_type_personal
                ).uppercase(),
                style = SubTrackType.monoXS,
                color = if (subscription.isShared) AccentGreen else AccentBlue
            )
        }
        Spacer(Modifier.height(Spacing.s))
        ServiceLogo(
            serviceName = subscription.name,
            brandColor = subscription.brandColor.toComposeColor(),
            size = LogoSize.Medium.dp
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
private fun PersonalSubscriptionsSection(
    subscriptions: List<Subscription>,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = subscriptions.take(3)
    val hasMore = subscriptions.size > 3

    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.dashboard_personal_title),
            count = subscriptions.size,
            actionLabel = stringResource(R.string.dashboard_personal_see_all),
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
private fun PersonalSubRow(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val billingDetail = when (subscription.cycle) {
        BillingCycle.MONTHLY -> stringResource(R.string.dashboard_billing_monthly, subscription.cutoffDay)
        BillingCycle.YEARLY -> stringResource(R.string.dashboard_billing_yearly, subscription.cutoffDay)
        BillingCycle.CUSTOM -> stringResource(R.string.dashboard_billing_custom, subscription.cutoffDay)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ServiceLogo(
            serviceName = subscription.name,
            brandColor = subscription.brandColor.toComposeColor(),
            size = LogoSize.Medium.dp
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
private fun DebtorsSection(
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
            title = stringResource(R.string.dashboard_debtors_title),
            count = debtors.size,
            actionLabel = stringResource(R.string.dashboard_debtors_see_all),
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
private fun DebtorRow(
    debtor: DebtorInfo,
    cutoffDay: Int,
    onClick: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = debtor.memberName, size = 36.dp)
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = debtor.memberName, style = SubTrackType.titleL, color = TextPrimary)
            Text(
                text = stringResource(
                    R.string.dashboard_debtor_detail,
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
            contentDescription = stringResource(R.string.dashboard_reminder_cd),
            onClick = onWhatsApp,
            iconTint = Color(0xFF25D366),
            containerColor = Color(0xFF25D366).copy(alpha = 0.12f)
        )
    }
}

// ── Insight card ──────────────────────────────────────────────────────────────

@Composable
private fun DashboardInsightCard(
    insight: Insight,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .border(1.dp, AccentBlue.copy(alpha = 0.2f), SubTrackShapes.l)
            .background(AccentBlueBg)
            .clickable { onNavigateToDetail(insight.subscriptionId) }
            .padding(Spacing.m),
        verticalAlignment = Alignment.Top
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(18.dp).padding(top = 1.dp)
        )
        Spacer(Modifier.width(Spacing.s))
        Column {
            Text(
                text = stringResource(
                    R.string.dashboard_insight_unused,
                    insight.subscriptionName,
                    insight.daysSinceActivity
                ),
                style = SubTrackType.bodyS,
                color = TextPrimary
            )
            Text(
                text = stringResource(
                    R.string.dashboard_insight_cancel_hint,
                    "%.2f".format(insight.amount)
                ),
                style = SubTrackType.monoXS,
                color = AccentBlue
            )
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    showAction: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = SubTrackType.headlineS, color = TextPrimary)
        Spacer(Modifier.width(Spacing.xs))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(SubTrackShapes.circle)
                .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.06f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(text = "$count", style = SubTrackType.monoXS, color = TextTertiary)
        }
        Spacer(Modifier.weight(1f))
        if (showAction) {
            Text(
                text = actionLabel,
                style = SubTrackType.monoS,
                color = AccentBlue,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAction
                )
            )
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun DashboardEmptyState(
    onCreateSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
            modifier = Modifier.padding(Spacing.xl).widthIn(max = 280.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_empty_title),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.dashboard_empty_subtitle),
                style = SubTrackType.bodyM,
                color = TextSecondary
            )
            Spacer(Modifier.height(Spacing.s))
            PrimaryButton(
                text = stringResource(R.string.dashboard_empty_cta),
                onClick = onCreateSubscription,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ── Utilities ─────────────────────────────────────────────────────────────────

private fun daysUntilCutoff(cutoffDay: Int): Int {
    val cal = Calendar.getInstance()
    val today = cal.get(Calendar.DAY_OF_MONTH)
    return if (cutoffDay >= today) {
        cutoffDay - today
    } else {
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        daysInMonth - today + cutoffDay
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Loading")
@Composable
private fun DashboardLoadingPreview() {
    SubTrackTheme { DashboardSkeleton(modifier = Modifier.fillMaxSize()) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Empty")
@Composable
private fun DashboardEmptyPreview() {
    SubTrackTheme {
        DashboardEmptyState(onCreateSubscription = {}, modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success")
@Composable
private fun DashboardSuccessPreview() {
    SubTrackTheme {
        val data = previewDashboardData()
        DashboardContent(
            data = data,
            onNavigateToDetail = {},
            onNavigateToCreate = {},
            onNavigateToNotifications = {},
            onNavigateToProfile = {},
            onReminderTap = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success — solo personales")
@Composable
private fun DashboardPersonalOnlyPreview() {
    SubTrackTheme {
        val data = previewDashboardData().copy(
            sharedSubscriptions = emptyList(),
            debtors = emptyList(),
            debtorCount = 0,
            sharedAmount = 0.0,
            sharedCount = 0
        )
        DashboardContent(
            data = data,
            onNavigateToDetail = {},
            onNavigateToCreate = {},
            onNavigateToNotifications = {},
            onNavigateToProfile = {},
            onReminderTap = { _, _, _ -> }
        )
    }
}

private fun previewDashboardData(): DashboardData {
    val subs = com.gondroid.subtrack.data.mock.MockData.subscriptions
    val personal = subs.filter { !it.isShared }
    val shared = subs.filter { it.isShared }
    val debtors = shared.flatMap { sub ->
        sub.members
            .filter {
                it.currentStatus == com.gondroid.subtrack.domain.model.enums.PaymentStatus.OVERDUE ||
                it.currentStatus == com.gondroid.subtrack.domain.model.enums.PaymentStatus.LATE
            }
            .map { member ->
                DebtorInfo(
                    memberId = member.id,
                    memberName = member.name,
                    memberPhone = member.phone,
                    hasApp = member.userId != null,
                    subscriptionId = sub.id,
                    subscriptionName = sub.name,
                    shareAmount = member.shareAmount,
                    status = member.currentStatus,
                    monthKey = "2026-04"
                )
            }
    }
    return DashboardData(
        user = com.gondroid.subtrack.data.mock.MockData.currentUser,
        dayLabel = "Viernes · Abril",
        totalMonthlySpend = subs.sumOf { it.totalAmount },
        personalAmount = personal.sumOf { it.totalAmount },
        sharedAmount = shared.sumOf { it.totalAmount },
        personalCount = personal.size,
        sharedCount = shared.size,
        debtorCount = debtors.size,
        upcomingCutoffs = emptyList(),
        personalSubscriptions = personal,
        sharedSubscriptions = shared,
        debtors = debtors,
        insight = com.gondroid.subtrack.domain.model.Insight(
            type = com.gondroid.subtrack.domain.model.InsightType.UNUSED_SUBSCRIPTION,
            subscriptionId = "sub_notion",
            subscriptionName = "Notion Plus",
            amount = 32.50,
            daysSinceActivity = 47
        )
    )
}
