package com.gondroid.subtrack.feature.subscriptiondetail.admin.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun CobranzaBar(
    pendingCount: Int,
    totalAmount: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shimmerOffset by remember { mutableFloatStateOf(-1f) }
    var shimmerActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3300L)
            shimmerActive = true
            var progress = -1f
            val steps = 40
            repeat(steps) {
                shimmerOffset = progress
                progress += 3f / steps
                delay(700L / steps)
            }
            shimmerOffset = -1f
            shimmerActive = false
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(SubTrackShapes.l)
            .border(1.dp, AccentGreen.copy(alpha = 0.25f), SubTrackShapes.l)
            .background(AccentGreenBg)
            .drawBehind {
                if (shimmerActive) {
                    val w = size.width
                    val h = size.height
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                AccentGreen.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerOffset * w - w * 0.4f, 0f),
                            end = Offset(shimmerOffset * w + w * 0.4f, h)
                        )
                    )
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.base),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.subscription_detail_collect_title, pendingCount),
                style = SubTrackType.headlineS,
                color = TextPrimary
            )
            Text(
                text = stringResource(
                    R.string.subscription_detail_collect_subtitle,
                    "%.2f".format(totalAmount)
                ),
                style = SubTrackType.monoXS,
                color = AccentGreen
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(SubTrackShapes.m)
                .background(AccentGreen.copy(alpha = 0.15f))
        ) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun CobranzaBarPreview() {
    SubTrackTheme {
        CobranzaBar(
            pendingCount = 2,
            totalAmount = 21.96,
            onClick = {},
            modifier = Modifier.padding(Spacing.base)
        )
    }
}
