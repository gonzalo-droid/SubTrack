@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.gondroid.subtrack.feature.profile.templates

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.common_back
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.common_error_generic
import com.gondroid.subtrack.resources.edit_template_delete
import com.gondroid.subtrack.resources.edit_template_discard_confirm
import com.gondroid.subtrack.resources.edit_template_discard_title
import com.gondroid.subtrack.resources.edit_template_eyebrow_edit
import com.gondroid.subtrack.resources.edit_template_eyebrow_new
import com.gondroid.subtrack.resources.edit_template_message_label
import com.gondroid.subtrack.resources.edit_template_name_label
import com.gondroid.subtrack.resources.edit_template_name_placeholder
import com.gondroid.subtrack.resources.edit_template_preview_collapse
import com.gondroid.subtrack.resources.edit_template_preview_toggle
import com.gondroid.subtrack.resources.edit_template_save
import com.gondroid.subtrack.resources.edit_template_saved_toast
import com.gondroid.subtrack.resources.edit_template_title_edit
import com.gondroid.subtrack.resources.edit_template_title_new
import com.gondroid.subtrack.resources.edit_template_tone_label
import com.gondroid.subtrack.resources.edit_template_variables_hint
import com.gondroid.subtrack.resources.edit_template_variables_label
import com.gondroid.subtrack.resources.template_action_delete_disabled
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.components.input.TemplateMessageEditor
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
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
import com.gondroid.subtrack.core.util.TemplateRenderer
import com.gondroid.subtrack.domain.model.TemplateVariable
import com.gondroid.subtrack.domain.model.enums.TemplateTone
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditTemplateScreen(
    templateId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<EditTemplateViewModel>(
        key = templateId ?: "new",
        parameters = { parametersOf(templateId) }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDiscardDialog by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    val emojiSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var messageFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    val editing = uiState as? EditTemplateUiState.Editing

    // Sync local TextFieldValue when ViewModel changes from outside (insert variable, init)
    LaunchedEffect(editing?.form?.messageBody, editing?.form?.cursorPosition) {
        val form = editing?.form ?: return@LaunchedEffect
        if (messageFieldValue.text != form.messageBody) {
            messageFieldValue = TextFieldValue(
                text = form.messageBody,
                selection = TextRange(form.cursorPosition.coerceIn(0, form.messageBody.length))
            )
        }
    }

    val messenger = LocalSnackbarMessenger.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditTemplateEvent.Saved -> {
                    messenger.show(Res.string.edit_template_saved_toast)
                    onBack()
                }
                is EditTemplateEvent.Deleted -> onBack()
                EditTemplateEvent.Error -> messenger.show(Res.string.common_error_generic)
                EditTemplateEvent.CannotDeleteDefault -> messenger.show(Res.string.template_action_delete_disabled)
            }
        }
    }

    fun requestBack() {
        val form = editing?.form
        val hasContent = form != null && (form.name.isNotBlank() || form.messageBody.isNotBlank())
        if (hasContent) showDiscardDialog = true else onBack()
    }

    BackHandler { requestBack() }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(Res.string.edit_template_discard_title), color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(stringResource(Res.string.edit_template_discard_confirm), color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(Res.string.common_cancel), color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }

    if (showEmojiPicker) {
        ModalBottomSheet(
            onDismissRequest = { showEmojiPicker = false },
            sheetState = emojiSheetState,
            containerColor = Color.Transparent,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            EmojiPickerSheetContent(
                selected = editing?.form?.emoji ?: "💬",
                onEmojiSelected = { emoji ->
                    viewModel.updateEmoji(emoji)
                    showEmojiPicker = false
                }
            )
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.base, vertical = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            STIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(Res.string.common_back),
                onClick = ::requestBack
            )
            Text(
                text = if (templateId == null) stringResource(Res.string.edit_template_title_new)
                       else stringResource(Res.string.edit_template_title_edit),
                style = SubTrackType.headlineS,
                color = TextPrimary
            )
            val canSave = editing?.form?.isValid == true && uiState !is EditTemplateUiState.Saving
            TextButton(
                onClick = { viewModel.save() },
                enabled = canSave
            ) {
                Text(
                    text = stringResource(Res.string.edit_template_save),
                    style = SubTrackType.titleL,
                    color = if (canSave) AccentBlue else TextTertiary
                )
            }
        }

        if (editing != null) {
            val form = editing.form
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.m),
                modifier = Modifier.padding(horizontal = Spacing.base)
            ) {
                item {
                    Eyebrow(
                        text = if (templateId == null) stringResource(Res.string.edit_template_eyebrow_new)
                               else stringResource(Res.string.edit_template_eyebrow_edit)
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = "${form.emoji} ${form.name.ifBlank { if (templateId == null) "Nueva plantilla" else "Editar plantilla" }}",
                        style = SubTrackType.headlineL,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(Spacing.xs))
                }

                // Name + Emoji card
                item {
                    SectionCard {
                        Eyebrow(text = stringResource(Res.string.edit_template_name_label))
                        Spacer(Modifier.height(Spacing.s))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(SubTrackShapes.base)
                                    .background(BgSurfaceEl)
                                    .border(1.dp, BorderDefault, SubTrackShapes.base)
                                    .clickable { showEmojiPicker = true }
                            ) {
                                Text(form.emoji, fontSize = 22.sp)
                            }
                            STTextField(
                                value = form.name,
                                onValueChange = viewModel::updateName,
                                label = "",
                                placeholder = stringResource(Res.string.edit_template_name_placeholder),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Tone selector card
                item {
                    SectionCard {
                        Eyebrow(text = stringResource(Res.string.edit_template_tone_label))
                        Spacer(Modifier.height(Spacing.s))
                        val tones = TemplateTone.entries
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                                ToneOption(tones[0], form.tone == tones[0], Modifier.weight(1f)) { viewModel.updateTone(it) }
                                ToneOption(tones[1], form.tone == tones[1], Modifier.weight(1f)) { viewModel.updateTone(it) }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                                ToneOption(tones[2], form.tone == tones[2], Modifier.weight(1f)) { viewModel.updateTone(it) }
                                ToneOption(tones[3], form.tone == tones[3], Modifier.weight(1f)) { viewModel.updateTone(it) }
                            }
                        }
                    }
                }

                // Message editor card
                item {
                    SectionCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Eyebrow(text = stringResource(Res.string.edit_template_message_label))
                            Text("${form.charCount}/500", style = SubTrackType.monoXS, color = TextTertiary)
                        }
                        Spacer(Modifier.height(Spacing.s))
                        TemplateMessageEditor(
                            value = messageFieldValue,
                            onValueChange = { new ->
                                messageFieldValue = new
                                viewModel.updateMessageBody(new.text)
                                viewModel.updateCursor(new.selection.start)
                            }
                        )
                    }
                }

                // Variables palette card
                item {
                    SectionCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Eyebrow(text = stringResource(Res.string.edit_template_variables_label))
                            Text(
                                stringResource(Res.string.edit_template_variables_hint),
                                style = SubTrackType.monoXS,
                                color = TextTertiary
                            )
                        }
                        Spacer(Modifier.height(Spacing.s))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            TemplateVariable.entries.forEach { variable ->
                                VariableChip(variable = variable) { viewModel.insertVariable(variable) }
                            }
                        }
                    }
                }

                // Preview toggle card
                item {
                    SectionCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.togglePreview() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Eyebrow(
                                text = if (form.previewExpanded) stringResource(Res.string.edit_template_preview_collapse)
                                       else stringResource(Res.string.edit_template_preview_toggle)
                            )
                            Icon(
                                imageVector = if (form.previewExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (form.previewExpanded) {
                            Spacer(Modifier.height(Spacing.m))
                            WhatsAppBubble(
                                text = TemplateRenderer.renderWithExamples(form.messageBody)
                            )
                        }
                    }
                }

                // Delete (edit mode only, not for defaults)
                if (templateId != null && !form.isDefault) {
                    item {
                        HorizontalDivider(color = BorderDefault)
                        Spacer(Modifier.height(Spacing.m))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SubTrackShapes.l)
                                .background(AccentRedBg.copy(alpha = 0.5f))
                                .border(1.dp, AccentRed.copy(alpha = 0.2f), SubTrackShapes.l)
                                .clickable { viewModel.delete() }
                                .padding(Spacing.m),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Delete, null, tint = AccentRed, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(Spacing.s))
                            Text(
                                stringResource(Res.string.edit_template_delete),
                                style = SubTrackType.bodyM,
                                color = AccentRed
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(Spacing.xl)) }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
internal fun EditTemplateScreenPreview() {
    SubTrackTheme { EditTemplateScreen(templateId = "template_1", onBack = {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
internal fun EditTemplateScreenNewPreview() {
    SubTrackTheme { EditTemplateScreen(templateId = null, onBack = {}) }
}
