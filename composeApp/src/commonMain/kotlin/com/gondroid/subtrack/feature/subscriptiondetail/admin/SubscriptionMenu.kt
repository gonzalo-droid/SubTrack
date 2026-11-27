package com.gondroid.subtrack.feature.subscriptiondetail.admin

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.common_back
import com.gondroid.subtrack.resources.date_day_of_month
import com.gondroid.subtrack.resources.month_names
import com.gondroid.subtrack.resources.subscription_detail_archived_help
import com.gondroid.subtrack.resources.subscription_detail_cutoff_day
import com.gondroid.subtrack.resources.subscription_detail_cutoff_today
import com.gondroid.subtrack.resources.subscription_detail_not_found_subtitle
import com.gondroid.subtrack.resources.subscription_detail_not_found_title
import com.gondroid.subtrack.resources.subscription_detail_overflow_cd
import com.gondroid.subtrack.resources.subscription_menu_delete
import com.gondroid.subtrack.resources.subscription_menu_edit
import com.gondroid.subtrack.resources.subscription_menu_pause
import com.gondroid.subtrack.resources.subscription_menu_resume
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.util.daysUntilCutoff
import com.gondroid.subtrack.domain.util.nextCutoff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl

@Composable
internal fun SubscriptionMenu(
    isPaused: Boolean,
    onEdit: () -> Unit,
    onSetPaused: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        STIconButton(
            icon = Icons.Outlined.MoreVert,
            contentDescription = stringResource(Res.string.subscription_detail_overflow_cd),
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = BgSurfaceEl
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.subscription_menu_edit), color = TextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = TextSecondary) },
                onClick = { expanded = false; onEdit() }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(if (isPaused) Res.string.subscription_menu_resume else Res.string.subscription_menu_pause),
                        color = TextPrimary
                    )
                },
                leadingIcon = {
                    Icon(
                        if (isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                onClick = { expanded = false; onSetPaused(!isPaused) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.subscription_menu_delete), color = AccentRed) },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = AccentRed) },
                onClick = { expanded = false; onDelete() }
            )
        }
    }
}

@Composable
internal fun CutoffPill(cutoffDay: Int) {
    val next = nextCutoff(cutoffDay)
    val dateStr = stringResource(
        Res.string.date_day_of_month,
        next.day,
        stringArrayResource(Res.array.month_names)[next.month.ordinal]
    )
    val (bg, color, text) = if (daysUntilCutoff(cutoffDay) == 0) {
        Triple(AccentAmberBg, AccentAmber, stringResource(Res.string.subscription_detail_cutoff_today, dateStr))
    } else {
        Triple(
            BorderDefault,
            TextSecondary,
            stringResource(Res.string.subscription_detail_cutoff_day, cutoffDay, dateStr)
        )
    }
    Text(
        text = text,
        style = SubTrackType.monoXS,
        color = color,
        modifier = Modifier
            .clip(SubTrackShapes.circle)
            .background(bg)
            .padding(horizontal = Spacing.m, vertical = Spacing.xs)
    )
}

@Composable
internal fun SectionHeader(title: String, trailing: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = SubTrackType.headlineS, color = TextPrimary)
        Text(text = trailing, style = SubTrackType.monoXS, color = TextTertiary)
    }
}

@Composable
internal fun ArchivedHelpCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(AccentAmberBg)
            .border(1.dp, AccentAmber.copy(alpha = 0.2f), SubTrackShapes.l)
            .padding(Spacing.m),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = stringResource(Res.string.subscription_detail_archived_help),
            style = SubTrackType.bodyS,
            color = AccentAmber
        )
    }
}

@Composable
internal fun AdminDetailNotFound(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.subscription_detail_not_found_title),
            style = SubTrackType.headlineL,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = stringResource(Res.string.subscription_detail_not_found_subtitle),
            style = SubTrackType.bodyM,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.xl))
        PrimaryButton(
            text = stringResource(Res.string.common_back),
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

