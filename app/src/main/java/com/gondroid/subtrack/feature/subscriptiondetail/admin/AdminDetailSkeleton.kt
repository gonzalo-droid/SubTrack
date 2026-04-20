package com.gondroid.subtrack.feature.subscriptiondetail.admin

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
fun AdminDetailSkeleton(modifier: Modifier = Modifier) {
    val shimmer = rememberInfiniteTransition(label = "shimmer")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.base),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Spacer(Modifier.height(Spacing.s))

        // Top nav
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(modifier = Modifier.size(36.dp), shape = SubTrackShapes.m, transition = shimmer)
            SkeletonBox(modifier = Modifier.size(36.dp), shape = SubTrackShapes.m, transition = shimmer)
        }

        Spacer(Modifier.height(Spacing.s))

        // Hero
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            SkeletonBox(modifier = Modifier.size(64.dp), shape = SubTrackShapes.xl, transition = shimmer)
            SkeletonBox(modifier = Modifier.height(18.dp).width(140.dp), shape = SubTrackShapes.base, transition = shimmer)
            SkeletonBox(modifier = Modifier.height(10.dp).width(100.dp), transition = shimmer)
            SkeletonBox(modifier = Modifier.height(22.dp).width(60.dp), shape = SubTrackShapes.circle, transition = shimmer)
            SkeletonBox(modifier = Modifier.height(36.dp).width(120.dp), shape = SubTrackShapes.base, transition = shimmer)
            SkeletonBox(modifier = Modifier.height(28.dp).width(160.dp), shape = SubTrackShapes.circle, transition = shimmer)
        }

        Spacer(Modifier.height(Spacing.xs))

        // Section header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SkeletonBox(modifier = Modifier.height(12.dp).width(80.dp), transition = shimmer)
            SkeletonBox(modifier = Modifier.height(12.dp).width(100.dp), transition = shimmer)
        }

        // Member rows (4)
        repeat(4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(modifier = Modifier.size(40.dp), shape = SubTrackShapes.circle, transition = shimmer)
                Spacer(Modifier.width(Spacing.m))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    SkeletonBox(modifier = Modifier.height(12.dp).width(110.dp), transition = shimmer)
                    SkeletonBox(modifier = Modifier.height(9.dp).width(60.dp), transition = shimmer)
                }
                SkeletonBox(modifier = Modifier.height(18.dp).width(56.dp), shape = SubTrackShapes.base, transition = shimmer)
                Spacer(Modifier.width(Spacing.s))
                SkeletonBox(modifier = Modifier.height(20.dp).width(60.dp), shape = SubTrackShapes.circle, transition = shimmer)
            }
        }

        // CobranzaBar
        SkeletonBox(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = SubTrackShapes.l,
            transition = shimmer
        )

        Spacer(Modifier.height(Spacing.xs))

        // History section header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SkeletonBox(modifier = Modifier.height(12.dp).width(60.dp), transition = shimmer)
            SkeletonBox(modifier = Modifier.height(12.dp).width(50.dp), transition = shimmer)
        }

        // Timeline rows (3)
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(modifier = Modifier.height(10.dp).width(40.dp), transition = shimmer)
                Spacer(Modifier.width(Spacing.s))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    repeat(3) {
                        SkeletonBox(modifier = Modifier.size(20.dp), shape = SubTrackShapes.circle, transition = shimmer)
                    }
                }
                SkeletonBox(modifier = Modifier.height(10.dp).width(50.dp), transition = shimmer)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AdminDetailSkeletonPreview() {
    SubTrackTheme { AdminDetailSkeleton() }
}
