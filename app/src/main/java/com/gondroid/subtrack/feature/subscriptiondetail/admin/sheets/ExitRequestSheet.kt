package com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.SecondaryButton
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

// TODO: [fase-2] Activate when auth + notifications are implemented
@Composable
fun ExitRequestSheet(
    requestId: String,
    memberName: String,
    subscriptionName: String,
    memberMessage: String = "",
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    SheetContainer {
        Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
            SheetHandle()
            Spacer(Modifier.height(Spacing.m))

            Text(
                text = stringResource(R.string.member_sheet_exit_title),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
            Spacer(Modifier.height(Spacing.m))

            // Request hero
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.l)
                    .background(AccentBlueBg)
                    .border(1.dp, AccentBlue.copy(alpha = 0.2f), SubTrackShapes.l)
                    .padding(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(SubTrackShapes.circle)
                        .background(AccentBlue.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(Spacing.m))
                Column {
                    Text(
                        text = stringResource(R.string.member_sheet_exit_subtitle),
                        style = SubTrackType.headlineS,
                        color = TextPrimary
                    )
                    Text(
                        text = "$memberName · $subscriptionName",
                        style = SubTrackType.monoXS,
                        color = TextTertiary
                    )
                }
            }

            if (memberMessage.isNotBlank()) {
                Spacer(Modifier.height(Spacing.m))
                Text(
                    text = "\"$memberMessage\"",
                    style = SubTrackType.bodyM.copy(fontStyle = FontStyle.Italic),
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SubTrackShapes.base)
                        .background(AccentBlueBg)
                        .padding(Spacing.m)
                )
            }

            Spacer(Modifier.height(Spacing.l))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.member_sheet_exit_reject),
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = stringResource(R.string.member_sheet_exit_approve),
                    onClick = onApprove,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ExitRequestSheetPreview() {
    SubTrackTheme {
        ExitRequestSheet(
            requestId = "req_1",
            memberName = "María Rodríguez",
            subscriptionName = "Netflix",
            memberMessage = "Ya no uso Netflix, quiero salir del grupo.",
            onApprove = {}, onReject = {}, onDismiss = {}
        )
    }
}
