package com.gondroid.subtrack.feature.dashboard

import com.gondroid.subtrack.platform.ExternalActions
import org.koin.compose.koinInject
import kotlinx.datetime.LocalDate
import com.gondroid.subtrack.domain.util.formatTwoDecimals
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.dashboard_tab_all
import com.gondroid.subtrack.resources.dashboard_tab_personal
import com.gondroid.subtrack.resources.dashboard_tab_shared
import com.gondroid.subtrack.resources.subscription_list_fab_cd
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.components.input.SegmentedSelector
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.util.WhatsAppHelper
import com.gondroid.subtrack.domain.model.DebtorInfo
import com.gondroid.subtrack.domain.model.Insight
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger
import com.gondroid.subtrack.domain.model.enums.isOverdue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.outlined.Add
// ── Public entry point ────────────────────────────────────────────────────────

@Composable
fun DashboardScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val externalActions = koinInject<ExternalActions>()
    val messenger = LocalSnackbarMessenger.current

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
                        amount = formatTwoDecimals(debtor.shareAmount),
                        adminName = state.data.user.name.substringBefore(" ")
                    )
                    WhatsAppHelper.openWhatsApp(externalActions, phone, msg)?.let(messenger::show)
                    viewModel.onReminderSent(memberId, subscriptionId)
                }
            },
            modifier = modifier.fillMaxSize()
        )
    }
}

// ── Content (Success state) ───────────────────────────────────────────────────

@Composable
internal fun DashboardContent(
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

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Fixed header
            DashboardHeader(
                user = data.user,
                today = data.today,
                onNotificationsClick = onNavigateToNotifications,
                onAvatarClick = onNavigateToProfile
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = FabClearance),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                // ── Segmented selector ────────────────────────────────────────
                item {
                    val tabs = listOf(
                        FilterTab(stringResource(Res.string.dashboard_tab_all), data.personalCount + data.sharedCount),
                        FilterTab(stringResource(Res.string.dashboard_tab_personal), data.personalCount),
                        FilterTab(stringResource(Res.string.dashboard_tab_shared), data.sharedCount)
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

        FloatingActionButton(
            onClick = onNavigateToCreate,
            containerColor = AccentGreen,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Spacing.base, bottom = Spacing.base)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(Res.string.subscription_list_fab_cd))
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Loading")
@Composable
internal fun DashboardLoadingPreview() {
    SubTrackTheme { DashboardSkeleton(modifier = Modifier.fillMaxSize()) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Empty")
@Composable
internal fun DashboardEmptyPreview() {
    SubTrackTheme {
        DashboardEmptyState(onCreateSubscription = {}, modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success")
@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success — letra grande", fontScale = 1.5f)
@Composable
internal fun DashboardSuccessPreview() {
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
internal fun DashboardPersonalOnlyPreview() {
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

internal fun previewDashboardData(): DashboardData {
    val subs = com.gondroid.subtrack.data.demo.DemoData.subscriptions
    val personal = subs.filter { !it.isShared }
    val shared = subs.filter { it.isShared }
    val debtors = shared.flatMap { sub ->
        sub.members
            .filter { it.currentStatus.isOverdue }
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
        user = com.gondroid.subtrack.data.demo.DemoData.currentUser,
        today = LocalDate(2026, 4, 17),
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
