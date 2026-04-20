package com.gondroid.subtrack.core.designsystem.components.indicators

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceHi
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = SubTrackShapes.s,
    shimmerDuration: Long = 1500L,
    transition: InfiniteTransition? = null
) {
    val infiniteTransition = transition ?: rememberInfiniteTransition(label = "skeleton")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(shimmerDuration.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_sweep"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(BgSurfaceHi)
            .drawBehind {
                val w = size.width
                val h = size.height
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerOffset * w - w * 0.5f, 0f),
                        end = Offset(shimmerOffset * w + w * 0.5f, h)
                    )
                )
            }
    )
}
