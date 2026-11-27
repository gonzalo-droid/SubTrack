package com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

@Composable
fun DecisionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) AccentGreen else BorderStrong
    val bg = if (selected) Brush.linearGradient(
        colors = listOf(AccentGreen.copy(alpha = 0.10f), AccentGreen.copy(alpha = 0.02f))
    ) else null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.base)
            .then(if (bg != null) Modifier.background(bg) else Modifier.background(BgSurfaceEl))
            .border(1.dp, borderColor, SubTrackShapes.base)
            .clickable(onClick = onClick)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Custom radio circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(16.dp)
                .clip(SubTrackShapes.circle)
                .border(1.5.dp, if (selected) AccentGreen else BorderStrong, SubTrackShapes.circle)
                .background(if (selected) AccentGreen else androidx.compose.ui.graphics.Color.Transparent)
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(SubTrackShapes.circle)
                        .background(androidx.compose.ui.graphics.Color.Black)
                )
            }
        }
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = SubTrackType.titleL, color = TextPrimary)
            Text(text = subtitle, style = SubTrackType.bodyS, color = TextSecondary)
        }
    }
}
