package com.gondroid.subtrack.feature.createsubscription.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
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
import com.gondroid.subtrack.domain.model.enums.SplitType
import com.gondroid.subtrack.feature.createsubscription.WizardMemberData
import kotlin.math.abs

@Composable
fun SplitStep(
    totalAmount: Double,
    members: List<WizardMemberData>,
    splitType: SplitType,
    memberShares: Map<String, Double>,
    onSelectSplitType: (SplitType) -> Unit,
    onUpdateMemberShare: (String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val peopleCount = members.size + 1 // +1 for admin

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base)
    ) {
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(text = stringResource(R.string.create_subscription_step4_eyebrow))
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_step4_title),
            style = SubTrackType.displayS,
            color = TextPrimary
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_step4_subtitle, "%.2f".format(totalAmount), peopleCount),
            style = SubTrackType.bodyM,
            color = TextSecondary
        )
        Spacer(Modifier.height(Spacing.l))

        // EQUAL option
        SplitOptionCard(
            isSelected = splitType == SplitType.EQUAL,
            onClick = { onSelectSplitType(SplitType.EQUAL) }
        ) {
            SplitOptionHeader(
                isSelected = splitType == SplitType.EQUAL,
                title = stringResource(R.string.create_subscription_split_equal),
                subtitle = stringResource(
                    R.string.create_subscription_split_equal_preview,
                    "%.2f".format(totalAmount),
                    peopleCount,
                    "%.2f".format(if (peopleCount > 0) totalAmount / peopleCount else 0.0)
                )
            )
            AnimatedVisibility(visible = splitType == SplitType.EQUAL, enter = expandVertically(), exit = shrinkVertically()) {
                Column {
                    Spacer(Modifier.height(Spacing.m))
                    HorizontalDivider(color = BorderDefault)
                    Spacer(Modifier.height(Spacing.m))
                    val equalShare = if (peopleCount > 0) totalAmount / peopleCount else 0.0
                    members.forEachIndexed { index, member ->
                        SplitMemberRow(name = member.name, amount = "S/ %.2f".format(equalShare))
                        if (index < members.lastIndex) Spacer(Modifier.height(Spacing.s))
                    }
                    SplitMemberRow(name = stringResource(R.string.create_subscription_split_you), amount = "S/ %.2f".format(equalShare))
                }
            }
        }

        Spacer(Modifier.height(Spacing.m))

        // PERCENTAGE option
        SplitOptionCard(
            isSelected = splitType == SplitType.PERCENTAGE,
            onClick = { onSelectSplitType(SplitType.PERCENTAGE) }
        ) {
            SplitOptionHeader(
                isSelected = splitType == SplitType.PERCENTAGE,
                title = stringResource(R.string.create_subscription_split_percentage),
                subtitle = stringResource(R.string.create_subscription_split_percentage_desc)
            )
            AnimatedVisibility(visible = splitType == SplitType.PERCENTAGE, enter = expandVertically(), exit = shrinkVertically()) {
                val membersTotal = members.sumOf { memberShares[it.id] ?: 0.0 }
                val adminPct = (100.0 - membersTotal).coerceAtLeast(0.0)
                Column {
                    Spacer(Modifier.height(Spacing.m))
                    HorizontalDivider(color = BorderDefault)
                    Spacer(Modifier.height(Spacing.m))
                    members.forEach { member ->
                        val pct = memberShares[member.id] ?: 0.0
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Avatar(name = member.name, size = 24.dp)
                                Spacer(Modifier.width(Spacing.s))
                                Text(member.name, style = SubTrackType.bodyS, color = TextPrimary, modifier = Modifier.weight(1f))
                                Text("%.0f%%".format(pct), style = SubTrackType.monoS, color = AccentGreen)
                                Spacer(Modifier.width(Spacing.s))
                                Text("S/ %.2f".format(totalAmount * pct / 100.0), style = SubTrackType.monoXS, color = TextTertiary)
                            }
                            Slider(
                                value = pct.toFloat(),
                                onValueChange = { onUpdateMemberShare(member.id, it.toDouble()) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = AccentGreen,
                                    activeTrackColor = AccentGreen,
                                    inactiveTrackColor = BgSurfaceEl
                                )
                            )
                        }
                        Spacer(Modifier.height(Spacing.xs))
                    }
                    SplitMemberRow(
                        name = stringResource(R.string.create_subscription_split_you),
                        amount = "%.0f%% · S/ %.2f".format(adminPct, totalAmount * adminPct / 100.0)
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.m))

        // FIXED option
        SplitOptionCard(
            isSelected = splitType == SplitType.FIXED,
            onClick = { onSelectSplitType(SplitType.FIXED) }
        ) {
            SplitOptionHeader(
                isSelected = splitType == SplitType.FIXED,
                title = stringResource(R.string.create_subscription_split_fixed),
                subtitle = stringResource(R.string.create_subscription_split_fixed_desc)
            )
            AnimatedVisibility(visible = splitType == SplitType.FIXED, enter = expandVertically(), exit = shrinkVertically()) {
                val membersSum = members.sumOf { memberShares[it.id] ?: 0.0 }
                val adminAmount = (totalAmount - membersSum).coerceAtLeast(0.0)
                val diff = totalAmount - membersSum
                Column {
                    Spacer(Modifier.height(Spacing.m))
                    HorizontalDivider(color = BorderDefault)
                    Spacer(Modifier.height(Spacing.m))
                    members.forEach { member ->
                        val amount = memberShares[member.id] ?: 0.0
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                        ) {
                            Avatar(name = member.name, size = 24.dp)
                            Text(member.name, style = SubTrackType.bodyS, color = TextPrimary, modifier = Modifier.weight(1f))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(SubTrackShapes.s)
                                    .background(BgSurfaceEl)
                                    .border(1.dp, BorderDefault, SubTrackShapes.s)
                                    .padding(horizontal = Spacing.s, vertical = Spacing.xs)
                            ) {
                                Text("S/ ", style = SubTrackType.monoXS, color = TextTertiary)
                                BasicTextField(
                                    value = if (amount == 0.0) "" else "%.2f".format(amount),
                                    onValueChange = { v ->
                                        val d = v.toDoubleOrNull()
                                        if (d != null) onUpdateMemberShare(member.id, d)
                                        else if (v.isEmpty()) onUpdateMemberShare(member.id, 0.0)
                                    },
                                    textStyle = SubTrackType.monoXS.copy(color = TextPrimary),
                                    cursorBrush = SolidColor(AccentGreen),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(Spacing.s))
                    }
                    HorizontalDivider(color = BorderDefault)
                    Spacer(Modifier.height(Spacing.s))
                    SplitMemberRow(
                        name = stringResource(R.string.create_subscription_split_you),
                        amount = "S/ %.2f".format(adminAmount)
                    )
                    // Amber warning if sum doesn't match
                    if (abs(diff) > 0.01 && membersSum > 0) {
                        Spacer(Modifier.height(Spacing.s))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SubTrackShapes.s)
                                .background(AccentAmberBg)
                                .border(1.dp, AccentAmber.copy(alpha = 0.3f), SubTrackShapes.s)
                                .padding(Spacing.s),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (diff > 0) stringResource(R.string.create_subscription_split_diff_under, "%.2f".format(diff))
                                else stringResource(R.string.create_subscription_split_diff_over, "%.2f".format(-diff)),
                                style = SubTrackType.monoXS,
                                color = AccentAmber
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun SplitOptionCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(if (isSelected) AccentGreenBg.copy(alpha = 0.5f) else BgSurface)
            .border(1.dp, if (isSelected) AccentGreen.copy(alpha = 0.4f) else BorderDefault, SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.m)
    ) {
        content()
    }
}

@Composable
private fun SplitOptionHeader(
    isSelected: Boolean,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = SubTrackType.titleL, color = if (isSelected) AccentGreen else TextPrimary)
            Spacer(Modifier.height(Spacing.xs))
            Text(subtitle, style = SubTrackType.bodyXS, color = TextSecondary)
        }
        if (isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(20.dp)
                    .clip(SubTrackShapes.circle)
                    .background(AccentGreen)
            ) {
                androidx.compose.material3.Icon(Icons.Outlined.Check, null, tint = Color.Black, modifier = Modifier.size(12.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(SubTrackShapes.circle)
                    .border(1.5.dp, BorderDefault, SubTrackShapes.circle)
            )
        }
    }
}

@Composable
private fun SplitMemberRow(name: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = SubTrackType.bodyS, color = TextSecondary)
        Text(amount, style = SubTrackType.monoS, color = TextPrimary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SplitStepEqualPreview() {
    SubTrackTheme {
        SplitStep(
            totalAmount = 54.90,
            members = listOf(
                WizardMemberData(name = "María", phone = "+51 912 345 678"),
                WizardMemberData(name = "Carlos", phone = "+51 987 654 321"),
                WizardMemberData(name = "Sofía", phone = "+51 911 222 333")
            ),
            splitType = SplitType.EQUAL,
            memberShares = emptyMap(),
            onSelectSplitType = {},
            onUpdateMemberShare = { _, _ -> }
        )
    }
}
