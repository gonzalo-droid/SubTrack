package com.gondroid.subtrack.core.designsystem.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.Elevation
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

private val TrackWidth = 36.dp
private val TrackHeight = 22.dp
private val ThumbSize = 18.dp
private val ThumbPadding = 2.dp
private val ThumbOffsetOff = ThumbPadding
private val ThumbOffsetOn = TrackWidth - ThumbSize - ThumbPadding

@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = ""
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) AccentGreen else Color.White.copy(alpha = 0.1f),
        animationSpec = tween(durationMillis = 200),
        label = "toggle_track_color"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) ThumbOffsetOn else ThumbOffsetOff,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "toggle_thumb_offset"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
            .graphicsLayer { alpha = if (enabled) 1f else 0.4f }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) }
            )
            .semantics {
                this.contentDescription = contentDescription
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
            }
    ) {
        Box(
            modifier = Modifier
                .size(TrackWidth, TrackHeight)
                .clip(SubTrackShapes.circle)
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = thumbOffset)
                    .shadow(elevation = Elevation.subtle, shape = SubTrackShapes.circle)
                    .size(ThumbSize)
                    .clip(SubTrackShapes.circle)
                    .background(Color.White)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ToggleSwitchPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ToggleSwitch(checked = true, onCheckedChange = {}, contentDescription = "Activado")
                Spacer(Modifier.width(Spacing.base))
                Text("Activado", style = SubTrackType.bodyM, color = TextPrimary)
            }
            Spacer(Modifier.height(Spacing.s))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ToggleSwitch(checked = false, onCheckedChange = {})
                Spacer(Modifier.width(Spacing.base))
                Text("Desactivado", style = SubTrackType.bodyM, color = TextSecondary)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ToggleSwitchDisabledPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ToggleSwitch(checked = true, onCheckedChange = {}, enabled = false)
                Spacer(Modifier.width(Spacing.base))
                Text("Activado (disabled)", style = SubTrackType.bodyM, color = TextSecondary)
            }
            Spacer(Modifier.height(Spacing.s))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ToggleSwitch(checked = false, onCheckedChange = {}, enabled = false)
                Spacer(Modifier.width(Spacing.base))
                Text("Desactivado (disabled)", style = SubTrackType.bodyM, color = TextSecondary)
            }
        }
    }
}
