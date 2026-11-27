@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.gondroid.subtrack.feature.profile.templates

import com.gondroid.subtrack.core.designsystem.theme.BrandWhatsAppBubble
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.TemplateVariable
import com.gondroid.subtrack.domain.model.enums.TemplateTone

internal val COMMON_EMOJIS = listOf(
    "💬", "✅", "🔔", "💳", "📱", "🎵",
    "🎬", "☁️", "🎮", "📺", "🏠", "💪",
    "🌟", "🎁", "💡", "🔑", "📊", "🚀",
    "❤️", "🤝", "👋", "💰", "🙏", "⚡",
    "🎯", "📅", "⏰", "💎", "🛡️", "🌍"
)

@Composable
internal fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .padding(Spacing.m)
    ) {
        content()
    }
}

@Composable
internal fun ToneOption(
    tone: TemplateTone,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (TemplateTone) -> Unit
) {
    val toneColor = Color(tone.accentColor)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(SubTrackShapes.base)
            .background(if (isSelected) toneColor.copy(alpha = 0.12f) else BgSurfaceEl)
            .border(1.dp, if (isSelected) toneColor.copy(alpha = 0.4f) else BorderDefault, SubTrackShapes.base)
            .clickable { onClick(tone) }
            .padding(horizontal = Spacing.m, vertical = Spacing.s)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(SubTrackShapes.circle)
                    .background(toneColor)
            )
            Text(
                text = tone.displayName,
                style = SubTrackType.bodyS,
                color = if (isSelected) toneColor else TextSecondary
            )
        }
    }
}

@Composable
internal fun VariableChip(
    variable: TemplateVariable,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(SubTrackShapes.base)
            .background(BgSurfaceEl)
            .border(1.dp, BorderDefault, SubTrackShapes.base)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.s, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(variable.token, style = SubTrackType.mono.copy(fontSize = 10.sp), color = AccentGreen)
        Text("·", style = SubTrackType.monoXS, color = TextTertiary)
        Text(variable.displayLabel, style = SubTrackType.monoXS, color = TextSecondary)
    }
}

@Composable
internal fun WhatsAppBubble(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = 12.dp, bottomEnd = 2.dp
                    )
                )
                .background(BrandWhatsAppBubble)
                .padding(horizontal = Spacing.m, vertical = Spacing.s)
        ) {
            Text(
                text = text.ifBlank { "El mensaje aparecerá aquí…" },
                style = SubTrackType.bodyS,
                color = Color.White
            )
        }
    }
}

@Composable
internal fun EmojiPickerSheetContent(
    selected: String,
    onEmojiSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(BgSheet)
            .padding(horizontal = Spacing.base)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = Spacing.m)
                .size(width = 40.dp, height = 4.dp)
                .clip(SubTrackShapes.circle)
                .background(Color.White.copy(alpha = 0.15f))
                .align(Alignment.CenterHorizontally)
        )
        Text("Elige un emoji", style = SubTrackType.headlineS, color = TextPrimary)
        Spacer(Modifier.height(Spacing.m))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.height(200.dp)
        ) {
            items(COMMON_EMOJIS) { emoji ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(SubTrackShapes.base)
                        .background(if (emoji == selected) BgSurfaceEl else Color.Transparent)
                        .border(
                            1.dp,
                            if (emoji == selected) BorderStrong else Color.Transparent,
                            SubTrackShapes.base
                        )
                        .clickable { onEmojiSelected(emoji) }
                ) {
                    Text(emoji, fontSize = 22.sp, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(Spacing.xl))
    }
}

