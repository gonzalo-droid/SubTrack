package com.gondroid.subtrack.core.designsystem.components.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.AccentPurpleBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType

enum class BadgeVariant {
    PRO, ADMIN, MEMBER, NEW, ERROR
}

private data class BadgeColors(val container: Color, val content: Color)

private fun BadgeVariant.colors() = when (this) {
    BadgeVariant.PRO -> BadgeColors(AccentAmberBg, AccentAmber)
    BadgeVariant.ADMIN -> BadgeColors(AccentGreenBg, AccentGreen)
    BadgeVariant.MEMBER -> BadgeColors(AccentBlueBg, AccentBlue)
    BadgeVariant.NEW -> BadgeColors(AccentPurpleBg, AccentPurple)
    BadgeVariant.ERROR -> BadgeColors(AccentRedBg, AccentRed)
}

@Composable
fun Badge(
    text: String,
    variant: BadgeVariant,
    modifier: Modifier = Modifier
) {
    val colors = variant.colors()
    Text(
        text = text.uppercase(),
        style = SubTrackType.monoXS,
        color = colors.content,
        modifier = modifier
            .clip(SubTrackShapes.circle)
            .background(colors.container)
            .padding(horizontal = Spacing.s, vertical = Spacing.xs)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun BadgePreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            Badge(text = "Pro", variant = BadgeVariant.PRO)
            Spacer(Modifier.width(Spacing.s))
            Badge(text = "Admin", variant = BadgeVariant.ADMIN)
            Spacer(Modifier.width(Spacing.s))
            Badge(text = "Miembro", variant = BadgeVariant.MEMBER)
            Spacer(Modifier.width(Spacing.s))
            Badge(text = "Nuevo", variant = BadgeVariant.NEW)
        }
    }
}
