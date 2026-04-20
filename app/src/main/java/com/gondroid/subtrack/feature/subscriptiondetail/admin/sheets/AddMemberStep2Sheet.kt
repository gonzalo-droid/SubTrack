package com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
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
import com.gondroid.subtrack.domain.model.ContactInfo
import com.gondroid.subtrack.domain.model.NewMemberData

@Composable
fun AddMemberStep2Sheet(
    contact: ContactInfo,
    subscriptionName: String,
    subscriptionTotal: Double,
    currentMembersTotal: Double,
    typicalShareAmount: Double,
    onConfirm: (NewMemberData) -> Unit,
    onBack: () -> Unit
) {
    val equalSplit = subscriptionTotal / (currentMembersTotal / typicalShareAmount + 2)
    var shareAmount by remember { mutableDoubleStateOf(typicalShareAmount) }
    val adminCovers = maxOf(0.0, subscriptionTotal - currentMembersTotal - shareAmount)

    SheetContainer {
        Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
            SheetHandle()
            Spacer(Modifier.height(Spacing.m))

            Eyebrow(text = stringResource(R.string.member_sheet_add_step2_eyebrow))
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.member_sheet_add_step2_title, contact.name),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
            Spacer(Modifier.height(Spacing.m))

            StepProgressBar2(steps = 2, current = 1)
            Spacer(Modifier.height(Spacing.l))

            // Contact preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.base)
                    .background(BgSurfaceEl)
                    .padding(Spacing.m),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(name = contact.name, size = 40.dp)
                Spacer(Modifier.width(Spacing.m))
                Column {
                    Text(text = contact.name, style = SubTrackType.titleL, color = TextPrimary)
                    Text(text = contact.phone, style = SubTrackType.monoXS, color = TextTertiary)
                }
            }
            Spacer(Modifier.height(Spacing.m))

            // Amount input block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.l)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentGreen.copy(alpha = 0.08f), AccentGreen.copy(alpha = 0.02f))
                        )
                    )
                    .border(1.dp, AccentGreen.copy(alpha = 0.2f), SubTrackShapes.l)
                    .padding(Spacing.base),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.member_sheet_add_amount_label),
                    style = SubTrackType.monoXS,
                    color = TextTertiary
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "S/ ${"%.2f".format(shareAmount)}",
                    style = SubTrackType.displayM,
                    color = AccentGreen
                )
                Spacer(Modifier.height(Spacing.m))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                    AmountChip(
                        label = stringResource(R.string.member_sheet_add_split_chip, "%.2f".format(typicalShareAmount)),
                        selected = shareAmount == typicalShareAmount,
                        onClick = { shareAmount = typicalShareAmount }
                    )
                    AmountChip(
                        label = stringResource(R.string.member_sheet_add_typical_chip, "%.2f".format(equalSplit)),
                        selected = shareAmount == equalSplit,
                        onClick = { shareAmount = equalSplit }
                    )
                }
            }
            Spacer(Modifier.height(Spacing.m))

            // Math summary card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.base)
                    .background(BgSurface)
                    .border(1.dp, BorderDefault, SubTrackShapes.base)
                    .padding(Spacing.m),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                MathRow(
                    label = stringResource(R.string.member_sheet_add_math_total, subscriptionName),
                    value = "S/ ${"%.2f".format(subscriptionTotal)}",
                    valueColor = TextPrimary
                )
                MathRow(
                    label = stringResource(R.string.member_sheet_add_math_current),
                    value = "S/ ${"%.2f".format(currentMembersTotal)}",
                    valueColor = TextSecondary
                )
                MathRow(
                    label = stringResource(R.string.member_sheet_add_math_new),
                    value = "+ S/ ${"%.2f".format(shareAmount)}",
                    valueColor = AccentGreen
                )
                if (adminCovers > 0) {
                    HorizontalDivider(color = BorderDefault, thickness = 0.5.dp)
                    MathRow(
                        label = stringResource(R.string.member_sheet_add_math_you_cover),
                        value = "S/ ${"%.2f".format(adminCovers)}",
                        valueColor = AccentAmber
                    )
                }
            }

            // Admin cover note
            if (adminCovers > 0) {
                Spacer(Modifier.height(Spacing.s))
                Text(
                    text = stringResource(R.string.member_sheet_add_admin_cover_note, "%.2f".format(adminCovers)),
                    style = SubTrackType.bodyS,
                    color = AccentAmber,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SubTrackShapes.base)
                        .background(AccentAmberBg)
                        .padding(Spacing.m)
                )
            }

            Spacer(Modifier.height(Spacing.l))
            PrimaryButton(
                text = stringResource(R.string.member_sheet_add_confirm, contact.name),
                onClick = {
                    onConfirm(NewMemberData(name = contact.name, phone = contact.phone, shareAmount = shareAmount))
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun StepProgressBar2(steps: Int, current: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        repeat(steps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(SubTrackShapes.circle)
                    .background(if (index <= current) AccentGreen else BgSurfaceEl)
            )
        }
    }
}

@Composable
private fun AmountChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = SubTrackType.monoXS,
        color = if (selected) AccentGreen else TextSecondary,
        modifier = Modifier
            .clip(SubTrackShapes.circle)
            .background(if (selected) AccentGreenBg else BgSurfaceEl)
            .border(1.dp, if (selected) AccentGreen.copy(alpha = 0.3f) else BorderDefault, SubTrackShapes.circle)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.m, vertical = Spacing.xs)
    )
}

@Composable
private fun MathRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = SubTrackType.bodyS, color = TextSecondary)
        Text(text = value, style = SubTrackType.monoXS, color = valueColor)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AddMemberStep2Preview() {
    SubTrackTheme {
        Box(modifier = Modifier.background(BgSheet)) {
            AddMemberStep2Sheet(
                contact = ContactInfo("c1", "Andrés Torres", "+51 999 111 222"),
                subscriptionName = "Netflix",
                subscriptionTotal = 54.90,
                currentMembersTotal = 32.94,
                typicalShareAmount = 10.98,
                onConfirm = {},
                onBack = {}
            )
        }
    }
}
