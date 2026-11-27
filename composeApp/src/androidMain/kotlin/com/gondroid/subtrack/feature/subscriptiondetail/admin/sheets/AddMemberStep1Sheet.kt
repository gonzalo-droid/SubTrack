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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
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

private val mockContacts = listOf(
    ContactInfo("c1", "Andrés Torres", "+51 999 111 222", hasApp = true),
    ContactInfo("c2", "Patricia León", "+51 988 222 333", hasApp = false),
    ContactInfo("c3", "Eduardo Castillo", "+51 977 333 444", hasApp = true),
    ContactInfo("c4", "Valeria Ríos", "+51 966 444 555", hasApp = false)
)

@Composable
fun AddMemberStep1Sheet(
    onContactSelected: (ContactInfo) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf<ContactInfo?>(null) }

    SheetContainer {
        Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
            SheetHandle()
            Spacer(Modifier.height(Spacing.m))

            Eyebrow(text = stringResource(R.string.member_sheet_add_step1_eyebrow))
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.member_sheet_add_step1_title),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
            Spacer(Modifier.height(Spacing.m))

            // Step indicator
            StepProgressBar(steps = 2, current = 0)
            Spacer(Modifier.height(Spacing.l))

            // Quick access grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                QuickAccessButton(
                    label = stringResource(R.string.member_sheet_add_contacts_option),
                    icon = Icons.Outlined.Contacts,
                    color = AccentBlue,
                    bg = AccentBlueBg,
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
                QuickAccessButton(
                    label = stringResource(R.string.member_sheet_add_qr_option),
                    icon = Icons.Outlined.QrCode,
                    color = AccentGreen,
                    bg = AccentGreenBg,
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
            }
            Spacer(Modifier.height(Spacing.l))

            Text(
                text = stringResource(R.string.member_sheet_add_suggestions_title),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
            Spacer(Modifier.height(Spacing.s))

            mockContacts.forEach { contact ->
                ContactRow(
                    contact = contact,
                    isSelected = selected?.id == contact.id,
                    onClick = { selected = if (selected?.id == contact.id) null else contact }
                )
            }

            Spacer(Modifier.height(Spacing.l))
            PrimaryButton(
                text = stringResource(R.string.member_sheet_continue),
                onClick = { selected?.let(onContactSelected) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selected != null
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun StepProgressBar(steps: Int, current: Int) {
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
private fun QuickAccessButton(
    label: String,
    icon: ImageVector,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(SubTrackShapes.base)
            .background(bg)
            .border(1.dp, color.copy(alpha = 0.2f), SubTrackShapes.base)
            .clickable(onClick = onClick)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(SubTrackShapes.s)
                .background(color.copy(alpha = 0.15f))
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(Spacing.s))
        Text(text = label, style = SubTrackType.titleL, color = color)
    }
}

@Composable
private fun ContactRow(
    contact: ContactInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = contact.name, size = 40.dp)
        Spacer(Modifier.width(Spacing.m))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = contact.name, style = SubTrackType.titleL, color = TextPrimary)
            Text(text = contact.phone, style = SubTrackType.monoXS, color = TextTertiary)
        }
        if (isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(SubTrackShapes.circle)
                    .background(AccentGreen)
            ) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(SubTrackShapes.circle)
                    .border(1.5.dp, BorderDefault, SubTrackShapes.circle)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AddMemberStep1Preview() {
    SubTrackTheme {
        Box(modifier = Modifier.background(BgSheet)) {
            AddMemberStep1Sheet(onContactSelected = {}, onDismiss = {})
        }
    }
}
