package com.gondroid.subtrack.feature.profile.referral

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
import com.gondroid.subtrack.resources.referral_how_desc
import com.gondroid.subtrack.resources.referral_how_it_works
import com.gondroid.subtrack.resources.referral_share_wa_message
import com.gondroid.subtrack.resources.referral_title
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
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
internal fun ReferralContent(
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

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
internal fun ReferralContentPreview() {
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
