package com.gondroid.subtrack.feature.subscriptionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.SubscriptionStats
import com.gondroid.subtrack.domain.usecase.subscription.SubscriptionFilter
import com.gondroid.subtrack.feature.subscriptionlist.components.SubscriptionRow

@Composable
fun SubscriptionListScreen(
    onNavigateToDetail: (String) -> Unit,
    onCreateSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SubscriptionListViewModel = viewModel(
        factory = SubscriptionListViewModelFactory()
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().background(BgApp)) {
        when (val state = uiState) {
            SubscriptionListUiState.Loading -> Unit
            is SubscriptionListUiState.Success -> {
                val currentUserId = "usr_gonzalo"
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        StatsCard(stats = state.stats, modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.m))
                    }

                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BgApp)
                                .padding(horizontal = Spacing.base, vertical = Spacing.s),
                            verticalArrangement = Arrangement.spacedBy(Spacing.s)
                        ) {
                            STTextField(
                                value = state.searchQuery,
                                onValueChange = viewModel::updateSearchQuery,
                                label = "",
                                placeholder = stringResource(R.string.subscription_list_search_hint),
                                leadingIcon = Icons.Outlined.Search,
                                modifier = Modifier.fillMaxWidth()
                            )
                            FilterTabs(
                                selected = state.selectedFilter,
                                onSelect = viewModel::selectFilter
                            )
                        }
                    }

                    if (state.subscriptions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(Spacing.xxl),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.subscription_list_empty),
                                    style = SubTrackType.bodyM,
                                    color = TextTertiary
                                )
                            }
                        }
                    } else {
                        items(state.subscriptions, key = { it.id }) { sub ->
                            SubscriptionRow(
                                subscription = sub,
                                isAdmin = sub.ownerId == currentUserId,
                                onClick = { onNavigateToDetail(sub.id) },
                                modifier = Modifier.padding(horizontal = Spacing.base)
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateSubscription,
            containerColor = AccentGreen,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Spacing.base, bottom = Spacing.base)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.subscription_list_fab_cd))
        }
    }
}

@Composable
private fun StatsCard(
    stats: SubscriptionStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .padding(Spacing.m),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatItem(
            label = stringResource(R.string.subscription_list_stat_total),
            value = "${stats.totalCount}"
        )
        StatDivider()
        StatItem(
            label = stringResource(R.string.subscription_list_stat_personal),
            value = "${stats.personalCount}"
        )
        StatDivider()
        StatItem(
            label = stringResource(R.string.subscription_list_stat_shared),
            value = "${stats.sharedCount}"
        )
        StatDivider()
        StatItem(
            label = stringResource(R.string.subscription_list_stat_monthly),
            value = "S/ ${"%.0f".format(stats.totalMonthlyAmount)}"
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = SubTrackType.headlineS, color = TextPrimary)
        Text(label, style = SubTrackType.monoXS, color = TextTertiary)
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(BorderDefault)
    )
}

@Composable
private fun FilterTabs(
    selected: SubscriptionFilter,
    onSelect: (SubscriptionFilter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        FilterTab(
            label = stringResource(R.string.subscription_list_filter_all),
            selected = selected == SubscriptionFilter.ALL,
            onClick = { onSelect(SubscriptionFilter.ALL) }
        )
        FilterTab(
            label = stringResource(R.string.subscription_list_filter_personal),
            selected = selected == SubscriptionFilter.PERSONAL,
            onClick = { onSelect(SubscriptionFilter.PERSONAL) }
        )
        FilterTab(
            label = stringResource(R.string.subscription_list_filter_shared),
            selected = selected == SubscriptionFilter.SHARED,
            onClick = { onSelect(SubscriptionFilter.SHARED) }
        )
    }
}

@Composable
private fun FilterTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = SubTrackType.monoXS,
        color = if (selected) AccentGreen else TextSecondary,
        modifier = Modifier
            .clip(SubTrackShapes.circle)
            .background(if (selected) AccentGreenBg else BgSurfaceEl)
            .border(1.dp, if (selected) AccentGreen.copy(alpha = 0.3f) else BorderDefault, SubTrackShapes.circle)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.m, vertical = Spacing.xs)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SubscriptionListScreenPreview() {
    SubTrackTheme { SubscriptionListScreen(onNavigateToDetail = {}, onCreateSubscription = {}) }
}
