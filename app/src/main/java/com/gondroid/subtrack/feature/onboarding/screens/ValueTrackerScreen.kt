package com.gondroid.subtrack.feature.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.animation.rememberFloatOffset
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

private data class OrbitCard(
    val logoChar: String,
    val logoColor: Color,
    val name: String,
    val amount: String,
    val delayMs: Int
)

private val ORBIT_CARDS = listOf(
    OrbitCard("N", Color(0xFFE50914), "Netflix", "S/ 54.90", 0),
    OrbitCard("S", Color(0xFF1DB954), "Spotify", "S/ 26.90", 500),
    OrbitCard("G", Color(0xFF10A37F), "ChatGPT", "S/ 74.90", 1000),
    OrbitCard("H", Color(0xFF9A1FD4), "HBO Max", "S/ 39.90", 1500),
)

@Composable
fun ValueTrackerScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = buildAnnotatedString {
        append(stringResource(R.string.onboarding_tracker_desc_prefix))
        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.SemiBold)) {
            append(stringResource(R.string.onboarding_tracker_desc_bold))
        }
        append(stringResource(R.string.onboarding_tracker_desc_suffix))
    }

    ValueSlideLayout(
        progressCurrent = 0,
        eyebrow = stringResource(R.string.onboarding_value_eyebrow, 1),
        eyebrowColor = AccentBlue,
        title = stringResource(R.string.onboarding_tracker_title),
        description = description,
        primaryCta = stringResource(R.string.onboarding_next),
        onPrimaryClick = onNext,
        onSkip = onSkip,
        modifier = modifier,
        visualContent = {
            TrackerVisual()
        }
    )
}

@Composable
private fun TrackerVisual() {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Center icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .shadow(24.dp, SubTrackShapes.xxl, spotColor = AccentBlue.copy(alpha = 0.4f))
                .clip(SubTrackShapes.xxl)
                .background(Brush.linearGradient(listOf(Color(0xFF0A84FF), Color(0xFF64D2FF))))
        ) {
            Icon(Icons.Outlined.List, null, tint = Color.White, modifier = Modifier.size(32.dp))
        }

        // Orbit cards
        val positions = listOf(
            Pair((-80).dp, (-100).dp),
            Pair(60.dp, (-60).dp),
            Pair((-90).dp, 60.dp),
            Pair(40.dp, 90.dp)
        )
        ORBIT_CARDS.forEachIndexed { i, card ->
            val floatY by rememberFloatOffset(card.delayMs)
            OrbitServiceCard(
                card = card,
                modifier = Modifier.offset(x = positions[i].first, y = positions[i].second + floatY.dp)
            )
        }
    }
}

@Composable
private fun OrbitServiceCard(card: OrbitCard, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(SubTrackShapes.m)
            .background(BgSurface)
            .border(1.dp, BorderStrong, SubTrackShapes.m)
            .padding(horizontal = Spacing.s, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(SubTrackShapes.xs)
                .background(card.logoColor)
        ) {
            Text(card.logoChar, style = SubTrackType.monoXS, color = Color.White)
        }
        Column {
            Text(card.name, style = SubTrackType.monoXS.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Text(card.amount, style = SubTrackType.monoXS, color = TextTertiary)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ValueTrackerPreview() {
    SubTrackTheme {
        ValueTrackerScreen(onNext = {}, onSkip = {}, modifier = Modifier.fillMaxSize())
    }
}
