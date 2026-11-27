package com.gondroid.subtrack.feature.subscriptiondetail.member

import com.gondroid.subtrack.domain.util.formatTwoDecimals
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.member_action_exit_confirm
import com.gondroid.subtrack.resources.member_action_exit_desc
import com.gondroid.subtrack.resources.member_action_exit_title
import com.gondroid.subtrack.resources.member_action_mark_paid_confirm
import com.gondroid.subtrack.resources.member_action_mark_paid_desc
import com.gondroid.subtrack.resources.member_action_mark_paid_note_label
import com.gondroid.subtrack.resources.member_action_mark_paid_note_placeholder
import com.gondroid.subtrack.resources.member_action_mark_paid_title
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

@Composable
internal fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .padding(Spacing.m)
    ) {
        content()
    }
}

@Composable
internal fun PlanInfoRow(label: String) {
    Text(label, style = SubTrackType.bodyXS, color = TextSecondary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MarkAsPaidSheetContent(
    shareAmount: Double,
    adminName: String,
    onConfirm: (String?) -> Unit,
    onCancel: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.base)
            .padding(bottom = Spacing.xl)
    ) {
        Text(stringResource(Res.string.member_action_mark_paid_title), style = SubTrackType.headlineL, color = TextPrimary)
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(Res.string.member_action_mark_paid_desc, formatTwoDecimals(shareAmount), adminName),
            style = SubTrackType.bodyS,
            color = TextSecondary
        )
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(text = stringResource(Res.string.member_action_mark_paid_note_label))
        Spacer(Modifier.height(Spacing.xs))
        BasicTextField(
            value = note,
            onValueChange = { note = it },
            textStyle = SubTrackType.bodyS.copy(color = TextPrimary),
            cursorBrush = SolidColor(AccentGreen),
            singleLine = true,
            decorationBox = { inner ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SubTrackShapes.m)
                        .background(BgSurfaceEl)
                        .border(1.dp, BorderDefault, SubTrackShapes.m)
                        .padding(Spacing.m)
                ) {
                    if (note.isEmpty()) {
                        Text(stringResource(Res.string.member_action_mark_paid_note_placeholder),
                            style = SubTrackType.bodyS, color = TextTertiary)
                    }
                    inner()
                }
            }
        )
        Spacer(Modifier.height(Spacing.m))
        PrimaryButton(
            text = stringResource(Res.string.member_action_mark_paid_confirm),
            onClick = { onConfirm(note.ifBlank { null }) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun RequestExitSheetContent(
    serviceName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.base)
            .padding(bottom = Spacing.xl)
    ) {
        Text(stringResource(Res.string.member_action_exit_title), style = SubTrackType.headlineL, color = TextPrimary)
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(Res.string.member_action_exit_desc, serviceName),
            style = SubTrackType.bodyS,
            color = TextSecondary
        )
        Spacer(Modifier.height(Spacing.m))
        PrimaryButton(
            text = stringResource(Res.string.member_action_exit_confirm),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(Res.string.common_cancel),
            style = SubTrackType.bodyS,
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCancel)
                .padding(vertical = Spacing.s),
        )
    }
}

