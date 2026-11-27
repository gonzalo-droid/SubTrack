package com.gondroid.subtrack.feature.profile.referral

import androidx.compose.ui.graphics.ImageBitmap
import com.gondroid.subtrack.domain.util.currentTimeMillis
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.referral_days_ago
import com.gondroid.subtrack.resources.referral_qr_desc
import com.gondroid.subtrack.resources.referral_qr_title
import com.gondroid.subtrack.resources.referral_recent_title
import com.gondroid.subtrack.resources.referral_status_active
import com.gondroid.subtrack.resources.referral_status_pending
import com.gondroid.subtrack.resources.referral_today
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.model.enums.ReferralStatus

@Composable
internal fun RecentReferralsCard(records: List<ReferralRecord>, modifier: Modifier = Modifier) {
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
internal fun ReferralRecordRow(record: ReferralRecord) {
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
internal fun QrSheetContent(code: String, qrBitmap: ImageBitmap?) {
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
internal fun relativeDate(timestamp: Long): String {
    val days = ((currentTimeMillis() - timestamp) / 86_400_000L).toInt()
    return if (days == 0) stringResource(Res.string.referral_today)
    else pluralStringResource(Res.plurals.referral_days_ago, days, days)
}

