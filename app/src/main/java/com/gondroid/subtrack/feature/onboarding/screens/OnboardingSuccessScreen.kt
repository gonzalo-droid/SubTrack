package com.gondroid.subtrack.feature.onboarding.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.animation.rememberSparkleAlpha
import com.gondroid.subtrack.core.designsystem.animation.rememberSparkleScale
import com.gondroid.subtrack.core.designsystem.animation.successPopSpec
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.AccentPurpleBg
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

@Composable
fun OnboardingSuccessScreen(
    userName: String,
    createdServiceName: String?,
    onAddAnother: () -> Unit,
    onShareWithFriends: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
            .safeDrawingPadding()
            .padding(horizontal = Spacing.base),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        // Success circle + sparkles
        SuccessHero()

        Spacer(Modifier.height(Spacing.xl))

        // Text
        Text(
            stringResource(R.string.onboarding_success_title, userName),
            style = SubTrackType.displayS,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            if (createdServiceName != null)
                stringResource(R.string.onboarding_success_subtitle_with_sub, createdServiceName)
            else
                stringResource(R.string.onboarding_success_subtitle_empty),
            style = SubTrackType.bodyM,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        // Action cards
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
            SuccessActionCard(
                icon = Icons.Outlined.Star,
                iconBg = AccentBlueBg,
                iconTint = AccentBlue,
                label = stringResource(R.string.onboarding_success_add_another),
                desc = stringResource(R.string.onboarding_success_add_another_desc),
                onClick = onAddAnother
            )
            SuccessActionCard(
                icon = Icons.Outlined.Share,
                iconBg = AccentGreenBg,
                iconTint = AccentGreen,
                label = stringResource(R.string.onboarding_success_share),
                desc = stringResource(R.string.onboarding_success_share_desc),
                onClick = onShareWithFriends
            )
            SuccessActionCard(
                icon = Icons.Outlined.Check,
                iconBg = AccentPurpleBg,
                iconTint = AccentPurple,
                label = stringResource(R.string.onboarding_success_go_home),
                desc = stringResource(R.string.onboarding_success_go_home_desc),
                onClick = onGoHome
            )
        }

        Spacer(Modifier.height(Spacing.m))

        Text(
            stringResource(R.string.onboarding_success_skip_home),
            style = SubTrackType.bodyXS,
            color = TextTertiary,
            modifier = Modifier
                .clickable(onClick = onGoHome)
                .padding(vertical = Spacing.s)
        )

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun SuccessHero() {
    var targetScale by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) { targetScale = 1f }
    val scale by animateFloatAsState(targetValue = targetScale, animationSpec = successPopSpec, label = "success_pop")

    val sparkle1Scale by rememberSparkleScale(200)
    val sparkle1Alpha by rememberSparkleAlpha(200)
    val sparkle2Scale by rememberSparkleScale(600)
    val sparkle2Alpha by rememberSparkleAlpha(600)
    val sparkle3Scale by rememberSparkleScale(1000)
    val sparkle3Alpha by rememberSparkleAlpha(1000)

    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Sparkles
        Text("✦", style = SubTrackType.headlineL, color = AccentGreen,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp)
                .graphicsLayer { scaleX = sparkle1Scale; scaleY = sparkle1Scale; alpha = sparkle1Alpha })
        Text("✦", style = SubTrackType.bodyM, color = AccentGreen,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 16.dp)
                .graphicsLayer { scaleX = sparkle2Scale; scaleY = sparkle2Scale; alpha = sparkle2Alpha })
        Text("✦", style = SubTrackType.monoXS, color = AccentGreen,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
                .graphicsLayer { scaleX = sparkle3Scale; scaleY = sparkle3Scale; alpha = sparkle3Alpha })

        // Main circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .shadow(24.dp, SubTrackShapes.xxl, spotColor = AccentGreen.copy(alpha = 0.45f))
                .clip(SubTrackShapes.xxl)
                .background(Brush.linearGradient(listOf(AccentGreen, Color(0xFF1FA838))))
        ) {
            Icon(Icons.Outlined.Check, null, tint = Color.Black, modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun SuccessActionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    desc: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(SubTrackShapes.m)
                .background(iconBg)
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = SubTrackType.titleL, color = TextPrimary)
            Text(desc, style = SubTrackType.monoXS, color = TextTertiary)
        }
        Text("→", style = SubTrackType.bodyM, color = TextTertiary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050506)
@Composable
private fun OnboardingSuccessPreview() {
    SubTrackTheme {
        OnboardingSuccessScreen(
            userName = "Gonzalo",
            createdServiceName = "Netflix",
            onAddAnother = {},
            onShareWithFriends = {},
            onGoHome = {}
        )
    }
}
