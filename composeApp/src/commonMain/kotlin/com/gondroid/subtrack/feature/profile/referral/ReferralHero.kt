package com.gondroid.subtrack.feature.profile.referral

import com.gondroid.subtrack.core.designsystem.theme.BrandWhatsApp
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberDeep
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.referral_code_label
import com.gondroid.subtrack.resources.referral_hero_subtitle
import com.gondroid.subtrack.resources.referral_hero_title
import com.gondroid.subtrack.resources.referral_progress_desc
import com.gondroid.subtrack.resources.referral_progress_title
import com.gondroid.subtrack.resources.referral_share_link
import com.gondroid.subtrack.resources.referral_share_qr
import com.gondroid.subtrack.resources.referral_share_wa
import com.gondroid.subtrack.resources.referral_stats_active
import com.gondroid.subtrack.resources.referral_stats_invited
import com.gondroid.subtrack.resources.referral_stats_months
import com.gondroid.subtrack.resources.referral_stats_see_all
import com.gondroid.subtrack.resources.referral_stats_title
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.indicators.ProgressBar
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.ReferralInfo

@Composable
internal fun ReferralHero(code: String, onCopyCode: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "gift_float")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "gift_rotation"
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -4f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "gift_offsetY"
    )

    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.base)
            .fillMaxWidth()
            .clip(SubTrackShapes.xl)
            .background(
                Brush.linearGradient(
                    listOf(AccentAmber.copy(alpha = 0.15f), AccentAmber.copy(alpha = 0.04f), BgSurface)
                )
            )
            .border(1.dp, AccentAmber.copy(alpha = 0.3f), SubTrackShapes.xl)
            .padding(vertical = 22.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .clip(SubTrackShapes.l)
                .background(Brush.linearGradient(listOf(AccentAmber, AccentAmberDeep)))
                .rotate(rotation)
                .offset { IntOffset(0, offsetY.dp.roundToPx()) }
        ) {
            Icon(Icons.Outlined.Star, null, tint = Color.Black, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(Spacing.m))
        Text(
            text = stringResource(Res.string.referral_hero_title),
            style = SubTrackType.headlineL,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = stringResource(Res.string.referral_hero_subtitle),
            style = SubTrackType.bodyS,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.base))

        // Code display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(Color.Black.copy(alpha = 0.4f))
                .border(1.dp, AccentAmber.copy(alpha = 0.4f).let {
                    androidx.compose.foundation.BorderStroke(1.dp, it)
                    it
                }, SubTrackShapes.l)
                .border(width = 1.dp, color = AccentAmber.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp))
                .padding(Spacing.m),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.referral_code_label),
                    style = SubTrackType.monoXS,
                    color = AccentAmber
                )
                Text(
                    text = code,
                    style = SubTrackType.headlineS.copy(letterSpacing = androidx.compose.ui.unit.TextUnit(0.1f, androidx.compose.ui.unit.TextUnitType.Em)),
                    color = TextPrimary
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(SubTrackShapes.base)
                    .background(TextPrimary)
                    .clickable(onClick = onCopyCode)
            ) {
                Icon(Icons.Outlined.ContentCopy, null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
internal fun ShareRow(
    onWhatsApp: () -> Unit,
    onLink: () -> Unit,
    onQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
        ShareButton(
            icon = Icons.Outlined.IosShare,
            label = stringResource(Res.string.referral_share_wa),
            iconTint = BrandWhatsApp,
            onClick = onWhatsApp,
            modifier = Modifier.weight(1f)
        )
        ShareButton(
            icon = Icons.Outlined.IosShare,
            label = stringResource(Res.string.referral_share_link),
            iconTint = AccentGreen,
            onClick = onLink,
            modifier = Modifier.weight(1f)
        )
        ShareButton(
            icon = Icons.Outlined.QrCode,
            label = stringResource(Res.string.referral_share_qr),
            iconTint = AccentPurple,
            onClick = onQr,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
internal fun ShareButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(SubTrackShapes.l)
            .background(BgSurfaceEl.copy(alpha = 0.5f))
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.m),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        Text(label, style = SubTrackType.monoXS, color = TextSecondary)
    }
}

@Composable
internal fun StatsCard(info: ReferralInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .padding(Spacing.base)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(Res.string.referral_stats_title), style = SubTrackType.titleL, color = TextPrimary)
            Text(stringResource(Res.string.referral_stats_see_all), style = SubTrackType.bodyXS, color = AccentGreen)
        }
        Spacer(Modifier.height(Spacing.m))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
            StatBlock(value = "${info.totalInvited}", label = stringResource(Res.string.referral_stats_invited), modifier = Modifier.weight(1f))
            StatBlock(value = "${info.activeReferrals}", label = stringResource(Res.string.referral_stats_active), valueColor = AccentGreen, modifier = Modifier.weight(1f))
            StatBlock(value = "${info.monthsProEarned}", label = stringResource(Res.string.referral_stats_months), valueColor = AccentAmber, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
internal fun StatBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Column(
        modifier = modifier
            .clip(SubTrackShapes.base)
            .background(Color.White.copy(alpha = 0.02f))
            .border(1.dp, BorderDefault, SubTrackShapes.base)
            .padding(vertical = Spacing.m),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = SubTrackType.headlineL, color = valueColor)
        Spacer(Modifier.height(4.dp))
        Text(label.uppercase(), style = SubTrackType.monoXS, color = TextTertiary, textAlign = TextAlign.Center)
    }
}

@Composable
internal fun ProgressCard(info: ReferralInfo, modifier: Modifier = Modifier) {
    val progress = info.activeReferrals.toFloat() / info.nextMilestone.toFloat()
    val remaining = info.nextMilestone - info.activeReferrals

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .padding(Spacing.base)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(Res.string.referral_progress_title), style = SubTrackType.bodyS, color = TextPrimary)
            Text(
                text = "${info.activeReferrals} / ${info.nextMilestone}",
                style = SubTrackType.monoXS,
                color = TextSecondary
            )
        }
        Spacer(Modifier.height(Spacing.m))
        ProgressBar(
            progress = progress,
            showShimmer = true,
            progressColor = Brush.linearGradient(listOf(AccentAmber, AccentAmberDeep))
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = pluralStringResource(Res.plurals.referral_progress_desc, remaining, remaining, info.nextMilestoneReward).uppercase(),
            style = SubTrackType.monoXS,
            color = TextTertiary
        )
    }
}

