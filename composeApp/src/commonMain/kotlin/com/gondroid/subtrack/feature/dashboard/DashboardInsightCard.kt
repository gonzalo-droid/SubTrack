package com.gondroid.subtrack.feature.dashboard

import com.gondroid.subtrack.domain.util.formatTwoDecimals
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.dashboard_empty_cta
import com.gondroid.subtrack.resources.dashboard_empty_subtitle
import com.gondroid.subtrack.resources.dashboard_empty_title
import com.gondroid.subtrack.resources.dashboard_insight_cancel_hint
import com.gondroid.subtrack.resources.dashboard_insight_unused
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.Insight
import androidx.compose.material3.Icon

@Composable
internal fun DashboardInsightCard(
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
            .clickable(role = Role.Button) { onNavigateToDetail(insight.subscriptionId) }
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
                text = pluralStringResource(
                    Res.plurals.dashboard_insight_unused,
                    insight.daysSinceActivity,
                    insight.subscriptionName,
                    insight.daysSinceActivity
                ),
                style = SubTrackType.bodyS,
                color = TextPrimary
            )
            Text(
                text = stringResource(
                    Res.string.dashboard_insight_cancel_hint,
                    formatTwoDecimals(insight.amount)
                ),
                style = SubTrackType.monoXS,
                color = AccentBlue
            )
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
internal fun SectionHeader(
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
                .background(BorderDefault)
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
internal fun DashboardEmptyState(
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
                text = stringResource(Res.string.dashboard_empty_title),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
            Text(
                text = stringResource(Res.string.dashboard_empty_subtitle),
                style = SubTrackType.bodyM,
                color = TextSecondary
            )
            Spacer(Modifier.height(Spacing.s))
            PrimaryButton(
                text = stringResource(Res.string.dashboard_empty_cta),
                onClick = onCreateSubscription,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ── Utilities ─────────────────────────────────────────────────────────────────

internal const val LARGE_FONT_SCALE = 1.3f

/** Bottom room so the last row clears the FAB: 56dp FAB + 16dp margin + 16dp gap. */
internal val FabClearance = Spacing.huge + Spacing.xxl + Spacing.s

// ── Previews ──────────────────────────────────────────────────────────────────

