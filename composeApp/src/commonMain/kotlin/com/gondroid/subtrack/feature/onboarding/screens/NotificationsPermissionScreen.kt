package com.gondroid.subtrack.feature.onboarding.screens

import com.gondroid.subtrack.resources.onboarding_notif_preview_time

import com.gondroid.subtrack.platform.rememberNotificationPermissionRequest

import org.jetbrains.compose.resources.StringResource

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.onboarding_notif_cta
import com.gondroid.subtrack.resources.onboarding_notif_desc
import com.gondroid.subtrack.resources.onboarding_notif_eyebrow
import com.gondroid.subtrack.resources.onboarding_notif_preview1
import com.gondroid.subtrack.resources.onboarding_notif_preview2
import com.gondroid.subtrack.resources.onboarding_notif_preview3
import com.gondroid.subtrack.resources.onboarding_notif_title
import com.gondroid.subtrack.resources.onboarding_skip_for_now

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.semantics.Role
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.indicators.ProgressDots
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import kotlinx.coroutines.delay

private data class NotifPreview(val textRes: StringResource, val scale: Float, val alpha: Float, val delayMs: Long)

private val NOTIF_PREVIEWS = listOf(
    NotifPreview(Res.string.onboarding_notif_preview1, 1f, 1f, 200L),
    NotifPreview(Res.string.onboarding_notif_preview2, 0.96f, 0.7f, 400L),
    NotifPreview(Res.string.onboarding_notif_preview3, 0.92f, 0.4f, 600L),
)

@Composable
fun NotificationsPermissionScreen(
    onGrant: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    /** Onboarding progress (0-based); null when shown outside the onboarding. */
    progressStep: Int? = null
) {
    val requestPermission = rememberNotificationPermissionRequest { granted -> if (granted) onGrant() else onSkip() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
            .safeDrawingPadding()
            .padding(horizontal = Spacing.base),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar: the only skip action lives under the CTA
        if (progressStep != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.m)
            ) {
                ProgressDots(totalSteps = 2, currentStep = progressStep)
            }
        }

        Spacer(Modifier.weight(1f))

        // Notification previews
        NotifPreviewStack()

        Spacer(Modifier.weight(1f))

        // Text
        Text(
            stringResource(Res.string.onboarding_notif_eyebrow).uppercase(),
            style = SubTrackType.monoXS,
            color = AccentAmber
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(Res.string.onboarding_notif_title),
            style = SubTrackType.displayS,
            color = TextPrimary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(Res.string.onboarding_notif_desc),
            style = SubTrackType.bodyM,
            color = TextSecondary
        )

        Spacer(Modifier.height(Spacing.xl))

        PrimaryButton(
            text = stringResource(Res.string.onboarding_notif_cta),
            onClick = requestPermission,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(Res.string.onboarding_skip_for_now),
            style = SubTrackType.bodyS,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onSkip)
                .minimumInteractiveComponentSize()
                .padding(vertical = Spacing.s)
        )

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun NotifPreviewStack() {
    val visibleStates = remember { mutableStateOf(List(NOTIF_PREVIEWS.size) { false }) }
    LaunchedEffect(Unit) {
        NOTIF_PREVIEWS.forEachIndexed { i, preview ->
            delay(preview.delayMs)
            visibleStates.value = visibleStates.value.toMutableList().also { it[i] = true }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = Spacing.s),
        verticalArrangement = Arrangement.spacedBy((-12).dp)
    ) {
        NOTIF_PREVIEWS.forEachIndexed { i, preview ->
            AnimatedVisibility(
                visible = visibleStates.value[i],
                enter = slideInVertically(
                    initialOffsetY = { -30 },
                    animationSpec = tween(500)
                ) + fadeIn(tween(500))
            ) {
                NotifPreviewCard(
                    textRes = preview.textRes,
                    alpha = preview.alpha
                )
            }
        }
    }
}

@Composable
private fun NotifPreviewCard(textRes: StringResource, alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(Color.White.copy(alpha = 0.08f * alpha))
            .border(1.dp, BorderStrong.copy(alpha = alpha), SubTrackShapes.l)
            .padding(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(26.dp)
                .clip(SubTrackShapes.s)
                .background(Brush.linearGradient(listOf(AccentGreen, AccentGreen.copy(alpha = 0.7f))))
        ) {
            Icon(Icons.Outlined.Notifications, null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SubTrack", style = SubTrackType.monoXS.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ), color = TextPrimary.copy(alpha = alpha))
                Text(stringResource(Res.string.onboarding_notif_preview_time), style = SubTrackType.monoXS, color = TextTertiary.copy(alpha = alpha))
            }
            Text(
                stringResource(textRes),
                style = SubTrackType.bodyXS,
                color = TextSecondary.copy(alpha = alpha)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050506)
@Composable
private fun NotificationsPreview() {
    SubTrackTheme {
        NotificationsPermissionScreen(onGrant = {}, onSkip = {})
    }
}
