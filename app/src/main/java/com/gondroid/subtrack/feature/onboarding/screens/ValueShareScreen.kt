package com.gondroid.subtrack.feature.onboarding.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.animation.rememberFloatOffset
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import kotlinx.coroutines.delay

private val MEMBER_COLORS = listOf(
    Brush.linearGradient(listOf(Color(0xFFFF6B6B), Color(0xFFC92A2A))),
    Brush.linearGradient(listOf(Color(0xFFFFD93D), Color(0xFFF08C00))),
    Brush.linearGradient(listOf(Color(0xFFBF5AF2), Color(0xFF7048E8)))
)
private val MEMBER_INITIALS = listOf("M", "C", "S")

@Composable
fun ValueShareScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = buildAnnotatedString {
        append(stringResource(R.string.onboarding_share_desc_prefix))
        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.SemiBold)) {
            append(stringResource(R.string.onboarding_share_desc_bold))
        }
        append(stringResource(R.string.onboarding_share_desc_suffix))
    }

    ValueSlideLayout(
        progressCurrent = 1,
        eyebrow = stringResource(R.string.onboarding_value_eyebrow, 2),
        eyebrowColor = AccentGreen,
        title = stringResource(R.string.onboarding_share_title),
        description = description,
        primaryCta = stringResource(R.string.onboarding_next),
        onPrimaryClick = onNext,
        onSkip = onSkip,
        modifier = modifier,
        visualContent = {
            ShareVisual()
        }
    )
}

@Composable
private fun ShareVisual() {
    val arrowOffset by rememberFloatOffset(0)

    // Staggered member avatars
    val memberScales = (0..2).map { i ->
        var scale by remember { mutableFloatStateOf(0f) }
        LaunchedEffect(Unit) {
            delay((100L + i * 200L))
            scale = 1f
        }
        animateFloatAsState(
            targetValue = scale,
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
            label = "member_scale_$i"
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // WhatsApp bubble
        Box(
            modifier = Modifier
                .clip(SubTrackShapes.l)
                .background(Brush.linearGradient(listOf(Color(0xFF25D366), Color(0xFF128C7E))))
                .shadow(16.dp, SubTrackShapes.l, spotColor = Color(0xFF25D366).copy(alpha = 0.3f))
                .padding(horizontal = Spacing.m, vertical = Spacing.s)
        ) {
            Text(
                stringResource(R.string.onboarding_share_bubble),
                style = SubTrackType.bodyXS,
                color = Color.White
            )
        }

        // Arrow
        Text(
            "↓",
            style = SubTrackType.headlineL,
            color = AccentGreen,
            modifier = Modifier.offset(y = arrowOffset.dp)
        )

        // Member avatars (staggered)
        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
            MEMBER_INITIALS.forEachIndexed { i, initial ->
                val scale by memberScales[i]
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .size(44.dp)
                        .clip(SubTrackShapes.circle)
                        .background(MEMBER_COLORS[i])
                        .border(3.dp, Color(0xFF0A0A0C), SubTrackShapes.circle)
                ) {
                    Text(initial, style = SubTrackType.titleL, color = Color.White)
                }
            }
        }

        // Split calculator
        Row(
            modifier = Modifier
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderStrong, SubTrackShapes.l)
                .padding(horizontal = Spacing.m, vertical = Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            Text(
                stringResource(R.string.onboarding_share_calc_total),
                style = SubTrackType.monoXS,
                color = TextSecondary
            )
            Text("÷ 4", style = SubTrackType.monoXS, color = TextTertiary)
            Text("=", style = SubTrackType.monoXS, color = TextTertiary)
            Text(
                stringResource(R.string.onboarding_share_calc_result),
                style = SubTrackType.titleL,
                color = AccentGreen
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ValueSharePreview() {
    SubTrackTheme {
        ValueShareScreen(onNext = {}, onSkip = {}, modifier = Modifier.fillMaxSize())
    }
}
