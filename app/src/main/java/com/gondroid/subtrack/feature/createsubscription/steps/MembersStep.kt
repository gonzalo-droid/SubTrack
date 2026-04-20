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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.SecondaryButton
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.components.input.ToggleSwitch
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
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
import com.gondroid.subtrack.feature.createsubscription.WizardMemberData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersStep(
    isShared: Boolean,
    members: List<WizardMemberData>,
    onToggleShared: (Boolean) -> Unit,
    onAddMember: (WizardMemberData) -> Unit,
    onRemoveMember: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            WizardAddMemberSheetContent(
                onMemberAdded = { member ->
                    onAddMember(member)
                    showAddSheet = false
                },
                onDismiss = { showAddSheet = false }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base)
    ) {
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(text = stringResource(R.string.create_subscription_step3_eyebrow))
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_step3_title),
            style = SubTrackType.displayS,
            color = TextPrimary
        )
        Spacer(Modifier.height(Spacing.l))

        // Shared toggle card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isShared) stringResource(R.string.create_subscription_shared_on)
                               else stringResource(R.string.create_subscription_shared_off),
                        style = SubTrackType.titleL,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = if (isShared) stringResource(R.string.create_subscription_shared_on_desc)
                               else stringResource(R.string.create_subscription_shared_off_desc),
                        style = SubTrackType.bodyXS,
                        color = TextSecondary
                    )
                }
                Spacer(Modifier.width(Spacing.m))
                ToggleSwitch(
                    checked = isShared,
                    onCheckedChange = onToggleShared,
                    contentDescription = stringResource(R.string.create_subscription_shared_toggle_cd)
                )
            }
        }

        // Members list (animated)
        AnimatedVisibility(
            visible = isShared,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Spacer(Modifier.height(Spacing.m))

                if (members.isEmpty()) {
                    // Empty state
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SubTrackShapes.l)
                            .background(BgSurface)
                            .border(1.dp, BorderDefault, SubTrackShapes.l)
                            .padding(Spacing.xl)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👥", style = SubTrackType.displayS)
                            Spacer(Modifier.height(Spacing.s))
                            Text(
                                stringResource(R.string.create_subscription_members_empty),
                                style = SubTrackType.bodyM,
                                color = TextTertiary
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SubTrackShapes.l)
                            .background(BgSurface)
                            .border(1.dp, BorderDefault, SubTrackShapes.l)
                    ) {
                        members.forEachIndexed { index, member ->
                            MemberRow(
                                member = member,
                                onRemove = { onRemoveMember(member.id) }
                            )
                            if (index < members.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(BorderDefault)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.m))
                SecondaryButton(
                    text = stringResource(R.string.create_subscription_add_member),
                    onClick = { showAddSheet = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun MemberRow(
    member: WizardMemberData,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.m, vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = member.name, size = 36.dp)
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.name, style = SubTrackType.titleL, color = TextPrimary)
            Text(member.phone, style = SubTrackType.monoXS, color = TextTertiary)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(SubTrackShapes.s)
                .background(BgSurfaceEl)
                .clickable(onClick = onRemove)
        ) {
            Icon(Icons.Outlined.Close, null, tint = TextTertiary, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun WizardAddMemberSheetContent(
    onMemberAdded: (WizardMemberData) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val isValid = name.isNotBlank() && phone.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(BgSheet)
            .padding(horizontal = Spacing.base)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = Spacing.m)
                .size(width = 40.dp, height = 4.dp)
                .clip(SubTrackShapes.circle)
                .background(Color.White.copy(alpha = 0.15f))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            stringResource(R.string.create_subscription_add_member_sheet_title),
            style = SubTrackType.headlineS,
            color = TextPrimary
        )
        Spacer(Modifier.height(Spacing.m))
        STTextField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(R.string.create_subscription_add_member_name_label),
            placeholder = stringResource(R.string.create_subscription_add_member_name_placeholder),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.m))
        STTextField(
            value = phone,
            onValueChange = { phone = it },
            label = stringResource(R.string.create_subscription_add_member_phone_label),
            placeholder = "+51 9XX XXX XXX",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.l))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.common_cancel), color = TextSecondary)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(2f)
                    .clip(SubTrackShapes.l)
                    .background(if (isValid) AccentGreen else AccentGreen.copy(alpha = 0.3f))
                    .clickable(enabled = isValid) {
                        onMemberAdded(WizardMemberData(name = name.trim(), phone = phone.trim()))
                    }
                    .padding(vertical = Spacing.m)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.Add, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        stringResource(R.string.create_subscription_add_member_confirm),
                        style = SubTrackType.titleL,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun MembersStepPreview() {
    SubTrackTheme {
        MembersStep(
            isShared = true,
            members = listOf(
                WizardMemberData(name = "María García", phone = "+51 912 345 678"),
                WizardMemberData(name = "Carlos Ríos", phone = "+51 987 654 321")
            ),
            onToggleShared = {},
            onAddMember = {},
            onRemoveMember = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun MembersStepOffPreview() {
    SubTrackTheme {
        MembersStep(
            isShared = false,
            members = emptyList(),
            onToggleShared = {},
            onAddMember = {},
            onRemoveMember = {}
        )
    }
}
