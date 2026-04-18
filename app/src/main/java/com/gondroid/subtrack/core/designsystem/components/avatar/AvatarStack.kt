package com.gondroid.subtrack.core.designsystem.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceHi
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

private val OverlapDp = 8.dp
private val BorderWidth = 2.dp

@Composable
fun AvatarStack(
    names: List<String>,
    modifier: Modifier = Modifier,
    maxVisible: Int = 3,
    size: Dp = 24.dp
) {
    val visible = names.take(maxVisible)
    val overflow = names.size - maxVisible

    Layout(
        content = {
            visible.forEach { name ->
                Avatar(
                    name = name,
                    size = size,
                    modifier = Modifier.border(BorderWidth, BgApp, SubTrackShapes.circle)
                )
            }
            if (overflow > 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(size)
                        .border(BorderWidth, BgApp, SubTrackShapes.circle)
                        .clip(SubTrackShapes.circle)
                        .background(BgSurfaceHi)
                ) {
                    Text(
                        text = "+$overflow",
                        style = SubTrackType.monoS,
                        color = TextSecondary
                    )
                }
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val itemCount = measurables.size
        if (itemCount == 0) return@Layout layout(0, 0) {}

        val sizePx = size.toPx().toInt()
        val overlapPx = OverlapDp.toPx().toInt()
        val stepPx = sizePx - overlapPx
        val totalWidth = sizePx + (itemCount - 1) * stepPx

        val placeables = measurables.map {
            it.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        layout(totalWidth, sizePx) {
            placeables.forEachIndexed { i, placeable ->
                placeable.placeRelative(x = i * stepPx, y = 0)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AvatarStackPreview() {
    SubTrackTheme {
        Box(modifier = Modifier.padding(Spacing.base)) {
            AvatarStack(
                names = listOf("Ana Torres", "Carlos Pérez", "María García"),
                maxVisible = 3,
                size = 32.dp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AvatarStackOverflowPreview() {
    SubTrackTheme {
        Box(modifier = Modifier.padding(Spacing.base)) {
            AvatarStack(
                names = listOf("Ana Torres", "Carlos Pérez", "María García", "Diego Quispe", "Lucía Vega"),
                maxVisible = 3,
                size = 32.dp
            )
        }
    }
}
