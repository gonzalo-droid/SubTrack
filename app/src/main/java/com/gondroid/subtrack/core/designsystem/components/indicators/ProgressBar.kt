package com.gondroid.subtrack.core.designsystem.components.indicators

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme

private val BarShape = RoundedCornerShape(3.dp)
private val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

// Default: amber → warm orange (iOS-style warning progress)
private val DefaultProgressBrush: Brush
    get() = Brush.linearGradient(listOf(AccentAmber, Color(0xFFFF9500)))

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    showShimmer: Boolean = false,
    trackColor: Color = Color.White.copy(alpha = 0.08f),
    progressColor: Brush? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600, easing = EmphasizedEasing),
        label = "progress_bar"
    )

    // Always computed, only used when showShimmer = true (stable composition)
    val shimmerSweep by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_sweep"
    )

    val fill = progressColor ?: DefaultProgressBrush

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(BarShape)
            .background(trackColor)
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(BarShape)
                    .background(fill)
                    .drawWithContent {
                        drawContent()
                        if (showShimmer) {
                            drawRect(
                                brush = Brush.linearGradient(
                                    colorStops = arrayOf(
                                        0f to Color.Transparent,
                                        0.5f to Color.White.copy(alpha = 0.3f),
                                        1f to Color.Transparent
                                    ),
                                    start = Offset(size.width * shimmerSweep, 0f),
                                    end = Offset(size.width * (shimmerSweep + 0.5f), 0f)
                                )
                            )
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ProgressBarPreview() {
    SubTrackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.base),
            modifier = Modifier.padding(Spacing.base)
        ) {
            ProgressBar(progress = 0.3f)
            ProgressBar(progress = 0.65f)
            ProgressBar(progress = 1f)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ProgressBarVariantsPreview() {
    SubTrackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.base),
            modifier = Modifier.padding(Spacing.base)
        ) {
            ProgressBar(progress = 0.4f, showShimmer = true)
            ProgressBar(
                progress = 0.7f,
                progressColor = Brush.linearGradient(listOf(AccentGreen, AccentBlue))
            )
            ProgressBar(
                progress = 0.55f,
                progressColor = Brush.linearGradient(listOf(AccentBlue, AccentBlue)),
                showShimmer = true
            )
        }
    }
}
