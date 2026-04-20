package com.gondroid.subtrack.feature.dashboard

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.indicators.SkeletonBox
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme

@Composable
fun DashboardSkeleton(modifier: Modifier = Modifier) {
    val shimmer = rememberInfiniteTransition(label = "shimmer")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.base),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Spacer(Modifier.height(Spacing.l))

        // Header skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(
                    modifier = Modifier.height(10.dp).width(110.dp),
                    transition = shimmer
                )
                Spacer(Modifier.height(6.dp))
                SkeletonBox(
                    modifier = Modifier.height(22.dp).width(170.dp),
                    shape = SubTrackShapes.base,
                    transition = shimmer
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                SkeletonBox(
                    modifier = Modifier.size(36.dp),
                    shape = SubTrackShapes.m,
                    transition = shimmer
                )
                SkeletonBox(
                    modifier = Modifier.size(36.dp),
                    shape = SubTrackShapes.circle,
                    transition = shimmer
                )
            }
        }

        Spacer(Modifier.height(Spacing.xs))

        // Segmented selector
        SkeletonBox(
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = SubTrackShapes.l,
            transition = shimmer
        )

        // Dual hero
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            SkeletonBox(
                modifier = Modifier.weight(1f).height(120.dp),
                shape = SubTrackShapes.xxl,
                transition = shimmer
            )
            SkeletonBox(
                modifier = Modifier.weight(1f).height(120.dp),
                shape = SubTrackShapes.xxl,
                transition = shimmer
            )
        }

        // Total row
        SkeletonBox(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = SubTrackShapes.l,
            transition = shimmer
        )

        Spacer(Modifier.height(Spacing.xs))

        // Section title
        SkeletonBox(
            modifier = Modifier.width(140.dp).height(14.dp),
            transition = shimmer
        )

        // Upcoming cards
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
            repeat(3) {
                SkeletonBox(
                    modifier = Modifier.width(160.dp).height(168.dp),
                    shape = SubTrackShapes.l,
                    transition = shimmer
                )
            }
        }

        Spacer(Modifier.height(Spacing.xs))

        // Section title
        SkeletonBox(
            modifier = Modifier.width(100.dp).height(14.dp),
            transition = shimmer
        )

        // Debtor rows
        repeat(3) {
            SkeletonBox(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = SubTrackShapes.l,
                transition = shimmer
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun DashboardSkeletonPreview() {
    SubTrackTheme { DashboardSkeleton() }
}
