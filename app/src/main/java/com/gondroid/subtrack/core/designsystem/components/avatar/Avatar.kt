package com.gondroid.subtrack.core.designsystem.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.core.designsystem.theme.SpaceGrotesk
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme

@Composable
fun Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val gradient = avatarGradientFor(name)
    val fontSize = (size.value * 0.37f).sp
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(SubTrackShapes.circle)
            .background(gradient)
    ) {
        Text(
            text = initialsFor(name),
            style = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                lineHeight = fontSize,
                color = Color.White
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AvatarPreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            Avatar(name = "Gonzalo Meza", size = 40.dp)
            Spacer(Modifier.width(Spacing.s))
            Avatar(name = "Ana Torres", size = 40.dp)
            Spacer(Modifier.width(Spacing.s))
            Avatar(name = "Carlos Pérez", size = 40.dp)
            Spacer(Modifier.width(Spacing.s))
            Avatar(name = "María García", size = 40.dp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AvatarSizesPreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            Avatar(name = "Gonzalo Meza", size = 24.dp)
            Spacer(Modifier.width(Spacing.s))
            Avatar(name = "Gonzalo Meza", size = 32.dp)
            Spacer(Modifier.width(Spacing.s))
            Avatar(name = "Gonzalo Meza", size = 40.dp)
            Spacer(Modifier.width(Spacing.s))
            Avatar(name = "Gonzalo Meza", size = 56.dp)
        }
    }
}
