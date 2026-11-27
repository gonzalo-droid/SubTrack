package com.gondroid.subtrack.feature.profile.templates

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.template_action_delete
import com.gondroid.subtrack.resources.template_action_delete_disabled
import com.gondroid.subtrack.resources.template_action_duplicate
import com.gondroid.subtrack.resources.template_action_edit
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
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.Template

@Composable
fun TemplateCard(
    template: Template,
    onClick: () -> Unit,
    onActionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val toneColor = Color(template.tone.accentColor)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(
                if (template.isDefault)
                    Brush.linearGradient(listOf(AccentGreen.copy(alpha = 0.06f), AccentGreen.copy(alpha = 0.02f)))
                else
                    Brush.linearGradient(listOf(BgSurface, BgSurface))
            )
            .border(
                1.dp,
                if (template.isDefault) AccentGreen.copy(alpha = 0.2f) else BorderDefault,
                SubTrackShapes.l
            )
            .clickable(onClick = onClick)
            .padding(Spacing.m)
    ) {
        // Top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text(template.emoji, style = SubTrackType.headlineS)
                Text(template.name, style = SubTrackType.titleL, color = TextPrimary)
                if (template.isDefault) Badge(text = "DEFAULT", variant = BadgeVariant.ADMIN)
            }
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onActionsClick)
            )
        }

        Spacer(Modifier.height(Spacing.xs))

        // Message preview
        Text(
            text = "\"${template.messageBody}\"",
            style = SubTrackType.bodyXS.copy(fontStyle = FontStyle.Italic),
            color = TextSecondary,
            maxLines = 2
        )

        Spacer(Modifier.height(Spacing.s))
        HorizontalDivider(color = BorderDefault)
        Spacer(Modifier.height(Spacing.s))

        // Footer tone
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Box(modifier = Modifier.size(5.dp).clip(SubTrackShapes.circle).background(toneColor))
            Text(template.tone.displayName, style = SubTrackType.monoXS, color = TextTertiary)
            if (template.isUserCreated) {
                Spacer(Modifier.width(Spacing.s))
                Text("· tuya", style = SubTrackType.monoXS, color = TextTertiary)
            }
        }
    }
}

@Composable
internal fun TemplateActionsSheetContent(
    template: Template,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(BgSheet)
            .padding(horizontal = Spacing.base)
    ) {
        // Handle
        Box(
            modifier = Modifier
                .padding(vertical = Spacing.m)
                .size(width = 40.dp, height = 4.dp)
                .clip(SubTrackShapes.circle)
                .background(Color.White.copy(alpha = 0.15f))
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "${template.emoji} ${template.name}",
            style = SubTrackType.headlineS,
            color = TextPrimary
        )
        Spacer(Modifier.height(Spacing.m))

        ActionItem(
            icon = Icons.Outlined.Edit,
            iconBg = AccentBlueBg,
            iconTint = AccentBlue,
            label = stringResource(Res.string.template_action_edit),
            onClick = onEdit
        )
        HorizontalDivider(modifier = Modifier.padding(start = 52.dp), color = BorderDefault)
        ActionItem(
            icon = Icons.Outlined.ContentCopy,
            iconBg = BgSurfaceEl,
            iconTint = TextSecondary,
            label = stringResource(Res.string.template_action_duplicate),
            onClick = onDuplicate
        )
        HorizontalDivider(modifier = Modifier.padding(start = 52.dp), color = BorderDefault)

        if (template.isDefault) {
            ActionItem(
                icon = Icons.Outlined.Delete,
                iconBg = AccentRedBg.copy(alpha = 0.4f),
                iconTint = AccentRed.copy(alpha = 0.4f),
                label = stringResource(Res.string.template_action_delete_disabled),
                labelColor = TextTertiary,
                enabled = false,
                onClick = {}
            )
        } else {
            ActionItem(
                icon = Icons.Outlined.Delete,
                iconBg = AccentRedBg,
                iconTint = AccentRed,
                label = stringResource(Res.string.template_action_delete),
                labelColor = AccentRed,
                onClick = onDelete
            )
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
internal fun ActionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    labelColor: Color = TextPrimary,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp).clip(SubTrackShapes.base).background(iconBg)
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Text(label, style = SubTrackType.bodyM, color = labelColor)
    }
}

