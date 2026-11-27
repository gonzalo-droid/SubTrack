package com.gondroid.subtrack.feature.profile.templates

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.cd_new_template
import com.gondroid.subtrack.resources.common_back
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.common_error_generic
import com.gondroid.subtrack.resources.template_delete_confirm_btn
import com.gondroid.subtrack.resources.template_delete_confirm_desc
import com.gondroid.subtrack.resources.template_delete_confirm_title
import com.gondroid.subtrack.resources.template_deleted
import com.gondroid.subtrack.resources.template_duplicated
import com.gondroid.subtrack.resources.template_info_text
import com.gondroid.subtrack.resources.template_screen_title
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    onBack: () -> Unit,
    onEditTemplate: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TemplatesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionSheet by viewModel.actionSheet.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val messenger = LocalSnackbarMessenger.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TemplatesEvent.Duplicated -> messenger.show(Res.string.template_duplicated)
                is TemplatesEvent.Deleted -> messenger.show(Res.string.template_deleted)
                TemplatesEvent.Error -> messenger.show(Res.string.common_error_generic)
            }
        }
    }

    // Delete confirmation dialog
    if (actionSheet is TemplateActionSheet.DeleteConfirmation) {
        val t = (actionSheet as TemplateActionSheet.DeleteConfirmation).template
        AlertDialog(
            onDismissRequest = viewModel::closeActions,
            title = { Text(stringResource(Res.string.template_delete_confirm_title), color = TextPrimary) },
            text = { Text(stringResource(Res.string.template_delete_confirm_desc), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(t.id) }) {
                    Text(stringResource(Res.string.template_delete_confirm_btn), color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeActions) {
                    Text(stringResource(Res.string.common_cancel), color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }

    // Actions bottom sheet
    if (actionSheet is TemplateActionSheet.Actions) {
        val t = (actionSheet as TemplateActionSheet.Actions).template
        ModalBottomSheet(
            onDismissRequest = viewModel::closeActions,
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            TemplateActionsSheetContent(
                template = t,
                onEdit = { viewModel.closeActions(); onEditTemplate(t.id) },
                onDuplicate = { viewModel.duplicate(t.id) },
                onDelete = { viewModel.requestDelete(t.id) }
            )
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.base, vertical = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            STIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(Res.string.common_back), onClick = onBack)
            Text(stringResource(Res.string.template_screen_title), style = SubTrackType.headlineS, color = TextPrimary)
            STIconButton(
                icon = Icons.Outlined.Add,
                contentDescription = stringResource(Res.string.cd_new_template),
                onClick = { onEditTemplate(null) }
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = Spacing.base)
        ) {
            // Info card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SubTrackShapes.l)
                        .background(AccentBlueBg)
                        .border(1.dp, AccentBlue.copy(alpha = 0.2f), SubTrackShapes.l)
                        .padding(Spacing.m),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.HelpOutline, null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                    Text(
                        text = stringResource(Res.string.template_info_text),
                        style = SubTrackType.bodyXS,
                        color = TextSecondary
                    )
                }
                Spacer(Modifier.height(Spacing.s))
            }

            // Template list
            when (val state = uiState) {
                is TemplatesUiState.Loading -> {}
                is TemplatesUiState.Success -> {
                    items(state.templates, key = { it.id }) { template ->
                        TemplateCard(
                            template = template,
                            onClick = { onEditTemplate(template.id) },
                            onActionsClick = { viewModel.openActions(template.id) }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(Spacing.xl)) }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
internal fun TemplatesScreenPreview() {
    SubTrackTheme { TemplatesScreen(onBack = {}, onEditTemplate = {}) }
}
