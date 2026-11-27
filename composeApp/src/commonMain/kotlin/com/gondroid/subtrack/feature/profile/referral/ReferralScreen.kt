package com.gondroid.subtrack.feature.profile.referral

import androidx.compose.ui.graphics.ImageBitmap

import com.gondroid.subtrack.platform.qrCodeImage

import org.jetbrains.compose.resources.getString

import kotlinx.coroutines.launch

import androidx.compose.ui.text.AnnotatedString

import androidx.compose.ui.platform.LocalClipboardManager

import androidx.compose.runtime.rememberCoroutineScope

import com.gondroid.subtrack.platform.ExternalActions

import org.koin.compose.koinInject

import com.gondroid.subtrack.domain.util.currentTimeMillis

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.cd_how_it_works
import com.gondroid.subtrack.resources.common_back
import com.gondroid.subtrack.resources.referral_code_copied
import com.gondroid.subtrack.resources.referral_code_label
import com.gondroid.subtrack.resources.referral_days_ago
import com.gondroid.subtrack.resources.referral_hero_subtitle
import com.gondroid.subtrack.resources.referral_hero_title
import com.gondroid.subtrack.resources.referral_how_desc
import com.gondroid.subtrack.resources.referral_how_it_works
import com.gondroid.subtrack.resources.referral_progress_desc
import com.gondroid.subtrack.resources.referral_progress_title
import com.gondroid.subtrack.resources.referral_qr_desc
import com.gondroid.subtrack.resources.referral_qr_title
import com.gondroid.subtrack.resources.referral_recent_title
import com.gondroid.subtrack.resources.referral_share_link
import com.gondroid.subtrack.resources.referral_share_qr
import com.gondroid.subtrack.resources.referral_share_wa
import com.gondroid.subtrack.resources.referral_share_wa_message
import com.gondroid.subtrack.resources.referral_stats_active
import com.gondroid.subtrack.resources.referral_stats_invited
import com.gondroid.subtrack.resources.referral_stats_months
import com.gondroid.subtrack.resources.referral_stats_see_all
import com.gondroid.subtrack.resources.referral_stats_title
import com.gondroid.subtrack.resources.referral_status_active
import com.gondroid.subtrack.resources.referral_status_pending
import com.gondroid.subtrack.resources.referral_title
import com.gondroid.subtrack.resources.referral_today

import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.indicators.ProgressBar
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.WhatsAppHelper
import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.model.enums.ReferralStatus
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ReferralViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val externalActions = koinInject<ExternalActions>()
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val messenger = LocalSnackbarMessenger.current

    var showHowItWorks by remember { mutableStateOf(false) }
    var showQrSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ReferralUiState.Loading -> {}
            is ReferralUiState.Success -> {
                val info = state.data.info
                val referralLink = "https://subtrack.app/r/${info.code}"

                ReferralContent(
                    info = info,
                    recentReferrals = state.data.recentReferrals,
                    onBack = onBack,
                    onHowItWorks = { showHowItWorks = true },
                    onCopyCode = {
                        clipboard.setText(AnnotatedString(info.code))
                        messenger.show(Res.string.referral_code_copied)
                    },
                    onShareWhatsApp = {
                        scope.launch {
                            val msg = getString(Res.string.referral_share_wa_message, info.code)
                            WhatsAppHelper.shareToWhatsApp(externalActions, msg)?.let { messenger.show(it) }
                        }
                    },
                    onShareLink = { externalActions.share(referralLink) },
                    onShowQr = { showQrSheet = true }
                )

                if (showHowItWorks) {
                    ModalBottomSheet(
                        onDismissRequest = { showHowItWorks = false },
                        sheetState = sheetState,
                        containerColor = BgSheet,
                        dragHandle = null,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.base)) {
                            Spacer(Modifier.height(Spacing.m))
                            Text(stringResource(Res.string.referral_how_it_works), style = SubTrackType.headlineS, color = TextPrimary)
                            Spacer(Modifier.height(Spacing.m))
                            Text(stringResource(Res.string.referral_how_desc), style = SubTrackType.bodyM, color = TextSecondary)
                            Spacer(Modifier.height(Spacing.xl))
                        }
                    }
                }

                if (showQrSheet) {
                    val qrBitmap = remember(info.code) { qrCodeImage(referralLink, 512) }
                    ModalBottomSheet(
                        onDismissRequest = { showQrSheet = false },
                        sheetState = sheetState,
                        containerColor = BgSheet,
                        dragHandle = null,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        QrSheetContent(code = info.code, qrBitmap = qrBitmap)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferralContent(
    info: ReferralInfo,
    recentReferrals: List<ReferralRecord>,
    onBack: () -> Unit,
    onHowItWorks: () -> Unit,
    onCopyCode: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onShareLink: () -> Unit,
    onShowQr: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.base, vertical = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            STIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(Res.string.common_back), onClick = onBack)
            Text(stringResource(Res.string.referral_title), style = SubTrackType.headlineS, color = TextPrimary)
            STIconButton(icon = Icons.Outlined.HelpOutline, contentDescription = stringResource(Res.string.cd_how_it_works), onClick = onHowItWorks)
        }

        // Hero
        ReferralHero(code = info.code, onCopyCode = onCopyCode)

        // Share buttons
        Spacer(Modifier.height(Spacing.m))
        ShareRow(
            onWhatsApp = onShareWhatsApp,
            onLink = onShareLink,
            onQr = onShowQr,
            modifier = Modifier.padding(horizontal = Spacing.base)
        )

        Spacer(Modifier.height(Spacing.base))

        // Stats
        StatsCard(info = info, modifier = Modifier.padding(horizontal = Spacing.base))

        Spacer(Modifier.height(Spacing.base))

        // Progress
        ProgressCard(info = info, modifier = Modifier.padding(horizontal = Spacing.base))

        Spacer(Modifier.height(Spacing.base))

        // Recent referrals
        RecentReferralsCard(records = recentReferrals, modifier = Modifier.padding(horizontal = Spacing.base))

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun ReferralHero(code: String, onCopyCode: () -> Unit) {
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
                .background(Brush.linearGradient(listOf(AccentAmber, Color(0xFFFF9500))))
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
private fun ShareRow(
    onWhatsApp: () -> Unit,
    onLink: () -> Unit,
    onQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
        ShareButton(
            icon = Icons.Outlined.IosShare,
            label = stringResource(Res.string.referral_share_wa),
            iconTint = Color(0xFF25D366),
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
private fun ShareButton(
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
private fun StatsCard(info: ReferralInfo, modifier: Modifier = Modifier) {
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
private fun StatBlock(
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
private fun ProgressCard(info: ReferralInfo, modifier: Modifier = Modifier) {
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
            progressColor = Brush.linearGradient(listOf(AccentAmber, Color(0xFFFF9500)))
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = pluralStringResource(Res.plurals.referral_progress_desc, remaining, remaining, info.nextMilestoneReward).uppercase(),
            style = SubTrackType.monoXS,
            color = TextTertiary
        )
    }
}

@Composable
private fun RecentReferralsCard(records: List<ReferralRecord>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.referral_recent_title),
            style = SubTrackType.monoXS,
            color = TextTertiary,
            modifier = Modifier.padding(bottom = Spacing.s)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
        ) {
            records.forEachIndexed { index, record ->
                ReferralRecordRow(record = record)
                if (index < records.lastIndex) {
                    androidx.compose.material3.HorizontalDivider(color = BorderDefault)
                }
            }
        }
    }
}

@Composable
private fun ReferralRecordRow(record: ReferralRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.m, vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        Avatar(name = record.referredName, size = 30.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(record.referredName, style = SubTrackType.bodyS, color = TextPrimary)
            Text(relativeDate(record.referredAt), style = SubTrackType.monoXS, color = TextTertiary)
        }
        Box(
            modifier = Modifier
                .clip(SubTrackShapes.circle)
                .background(if (record.status == ReferralStatus.ACTIVE) AccentGreenBg else AccentAmberBg)
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(
                text = if (record.status == ReferralStatus.ACTIVE)
                    stringResource(Res.string.referral_status_active)
                else
                    stringResource(Res.string.referral_status_pending),
                style = SubTrackType.monoXS,
                color = if (record.status == ReferralStatus.ACTIVE) AccentGreen else AccentAmber
            )
        }
    }
}

@Composable
private fun QrSheetContent(code: String, qrBitmap: ImageBitmap?) {
    Column(
        modifier = Modifier
            .padding(Spacing.base)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(Spacing.m))
        Text(stringResource(Res.string.referral_qr_title), style = SubTrackType.headlineS, color = TextPrimary)
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = stringResource(Res.string.referral_qr_desc),
            style = SubTrackType.bodyS,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.base))
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(SubTrackShapes.l)
                .background(Color.White)
                .padding(Spacing.s)
        ) {
            if (qrBitmap != null) Image(
                bitmap = qrBitmap,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(Spacing.m))
        Text(code, style = SubTrackType.monoXS, color = TextTertiary)
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun relativeDate(timestamp: Long): String {
    val days = ((currentTimeMillis() - timestamp) / 86_400_000L).toInt()
    return if (days == 0) stringResource(Res.string.referral_today)
    else pluralStringResource(Res.plurals.referral_days_ago, days, days)
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ReferralContentPreview() {
    SubTrackTheme {
        ReferralContent(
            info = ReferralInfo(
                code = "GONZALO42", totalInvited = 7, activeReferrals = 5,
                pendingReferrals = 1, monthsProEarned = 5, nextMilestone = 12,
                nextMilestoneReward = "1 año Pro completo"
            ),
            recentReferrals = listOf(
                ReferralRecord("r1", "Carlos Mendoza", currentTimeMillis() - 86400000L, ReferralStatus.ACTIVE),
                ReferralRecord("r2", "Sofía Gómez", currentTimeMillis() - 259200000L, ReferralStatus.PENDING)
            ),
            onBack = {}, onHowItWorks = {}, onCopyCode = {},
            onShareWhatsApp = {}, onShareLink = {}, onShowQr = {}
        )
    }
}
