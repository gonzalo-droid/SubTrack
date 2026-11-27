package com.gondroid.subtrack.feature.profile

import com.gondroid.subtrack.core.designsystem.theme.AccentAmberDeep
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.legal_logos_body
import com.gondroid.subtrack.resources.legal_logos_simpleicons
import com.gondroid.subtrack.resources.legal_logos_title
import com.gondroid.subtrack.resources.profile_referral_months
import com.gondroid.subtrack.resources.profile_referral_title
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

@Composable
internal fun ReferralEntryCard(
    monthsEarned: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(Brush.linearGradient(listOf(AccentAmber.copy(alpha = 0.12f), AccentAmber.copy(alpha = 0.04f))))
            .border(1.dp, AccentAmber.copy(alpha = 0.25f), SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.base),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(SubTrackShapes.base)
                .background(Brush.linearGradient(listOf(AccentAmber, AccentAmberDeep)))
        ) {
            Icon(Icons.Outlined.Star, null, tint = Color.Black, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(Res.string.profile_referral_title), style = SubTrackType.titleL, color = TextPrimary)
            Text(
                text = pluralStringResource(Res.plurals.profile_referral_months, monthsEarned, monthsEarned),
                style = SubTrackType.monoXS,
                color = AccentAmber
            )
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
internal fun LegalLogosSheet(onDismiss: () -> Unit) {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BgSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.base)
                .padding(bottom = Spacing.xl)
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(Res.string.legal_logos_title),
                style = SubTrackType.headlineM,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.s))
            Text(
                text = stringResource(Res.string.legal_logos_body),
                style = SubTrackType.bodyS,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(Spacing.m))
            Text(
                text = stringResource(Res.string.legal_logos_simpleicons),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
        }
    }
}

