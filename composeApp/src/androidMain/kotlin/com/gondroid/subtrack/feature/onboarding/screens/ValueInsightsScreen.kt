package com.gondroid.subtrack.feature.onboarding.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.delay

private data class InsightCard(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val textRes: Int,
    val amount: String,
    val amountColor: Color,
    val delayMs: Long
)

@Composable
fun ValueInsightsScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = buildAnnotatedString {
        append(stringResource(R.string.onboarding_insights_desc_prefix))
        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.SemiBold)) {
            append(stringResource(R.string.onboarding_insights_desc_bold))
        }
        append(stringResource(R.string.onboarding_insights_desc_suffix))
    }

    ValueSlideLayout(
        progressCurrent = 2,
        eyebrow = stringResource(R.string.onboarding_value_eyebrow, 3),
        eyebrowColor = AccentAmber,
        title = stringResource(R.string.onboarding_insights_title),
        description = description,
        primaryCta = stringResource(R.string.onboarding_insights_cta),
        primaryCtaAccent = AccentGreen,
        onPrimaryClick = onNext,
        onSkip = onSkip,
        modifier = modifier,
        visualContent = {
            InsightsVisual()
        }
    )
}

@Composable
private fun InsightsVisual() {
    val cards = listOf(
        InsightCard(Icons.Outlined.KeyboardArrowUp, AccentRedBg, AccentRed, R.string.onboarding_insights_card1, "S/ 168.70", AccentRed, 100L),
        InsightCard(Icons.Outlined.Warning, AccentAmberBg, AccentAmber, R.string.onboarding_insights_card2, "S/ 54.90", AccentAmber, 300L),
        InsightCard(Icons.Outlined.Check, AccentGreenBg, AccentGreen, R.string.onboarding_insights_card3, "S/ 41.18", AccentGreen, 500L),
    )

    val visible = remember { mutableStateOf(List(cards.size) { false }) }
    LaunchedEffect(Unit) {
        cards.forEachIndexed { i, card ->
            delay(card.delayMs)
            visible.value = visible.value.toMutableList().also { it[i] = true }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        modifier = Modifier.padding(horizontal = Spacing.s)
    ) {
        cards.forEachIndexed { i, card ->
            AnimatedVisibility(
                visible = visible.value[i],
                enter = slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(400)
                ) + fadeIn(tween(400))
            ) {
                InsightAlertCard(card = card)
            }
        }
    }
}

@Composable
private fun InsightAlertCard(card: InsightCard) {
    Row(
        modifier = Modifier
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .padding(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .clip(SubTrackShapes.s)
                .background(card.iconBg)
        ) {
            Icon(card.icon, null, tint = card.iconTint, modifier = Modifier.size(16.dp))
        }
        Text(
            stringResource(card.textRes),
            style = SubTrackType.bodyXS,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(card.amount, style = SubTrackType.titleL, color = card.amountColor)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ValueInsightsPreview() {
    SubTrackTheme {
        ValueInsightsScreen(onNext = {}, onSkip = {}, modifier = Modifier.fillMaxSize())
    }
}
