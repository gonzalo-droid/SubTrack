package com.gondroid.subtrack.core.designsystem.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceHi
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bgColor by animateColorAsState(
        targetValue = if (isPressed && onClick != null) BgSurfaceHi else BgSurface,
        animationSpec = tween(durationMillis = 150),
        label = "surface_card_bg"
    )

    Column(
        modifier = modifier
            .clip(SubTrackShapes.l)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .background(bgColor)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ) else Modifier
            )
            .padding(contentPadding),
        content = content
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SurfaceCardStaticPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            SurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Text("Netflix", style = SubTrackType.headlineS)
                Spacer(Modifier.height(Spacing.xs))
                Text("S/ 49.90 · mensual", style = SubTrackType.bodyM, color = TextSecondary)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SurfaceCardClickablePreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            SurfaceCard(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Spotify", style = SubTrackType.headlineS)
                Spacer(Modifier.height(Spacing.xs))
                Text("S/ 25.90 · mensual — toca para ver hover", style = SubTrackType.bodyM, color = TextSecondary)
            }
        }
    }
}
