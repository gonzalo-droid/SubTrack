package com.gondroid.subtrack.core.designsystem.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

private val IconButtonShape = RoundedCornerShape(11.dp)

@Composable
fun STIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBadge: Boolean = false
) {
    Box(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(IconButtonShape)
                .border(1.dp, BorderDefault, IconButtonShape)
                .background(BgSurface)
                .clickable(role = Role.Button, onClick = onClick)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-3).dp)
                    .size(8.dp)
                    .clip(SubTrackShapes.circle)
                    .background(AccentRed)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun STIconButtonPreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            STIconButton(icon = Icons.Outlined.Add, contentDescription = "Agregar", onClick = {})
            Spacer(Modifier.width(Spacing.s))
            STIconButton(icon = Icons.Outlined.Share, contentDescription = "Compartir", onClick = {})
            Spacer(Modifier.width(Spacing.s))
            STIconButton(
                icon = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                onClick = {},
                showBadge = true
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun STIconButtonBadgePreview() {
    SubTrackTheme {
        Box(modifier = Modifier.padding(Spacing.l)) {
            STIconButton(
                icon = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones pendientes",
                onClick = {},
                showBadge = true
            )
        }
    }
}
