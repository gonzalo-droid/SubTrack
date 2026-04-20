package com.gondroid.subtrack.feature.profile.templates

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.components.input.TemplateMessageEditor
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
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

private val COMMON_EMOJIS = listOf(
    "💬", "✅", "🔔", "💳", "📱", "🎵",
    "🎬", "☁️", "🎮", "📺", "🏠", "💪",
    "🌟", "🎁", "💡", "🔑", "📊", "🚀",
    "❤️", "🤝", "👋", "💰", "🙏", "⚡",
    "🎯", "📅", "⏰", "💎", "🛡️", "🌍"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditTemplateScreen(
    templateId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EditTemplateViewModel = viewModel(factory = EditTemplateViewModelFactory(templateId))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditTemplateEvent.Saved -> {
                    Toast.makeText(context, context.getString(R.string.edit_template_saved_toast), Toast.LENGTH_SHORT).show()
                    onBack()
                }
                is EditTemplateEvent.Deleted -> onBack()
                is EditTemplateEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
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
            title = { Text(stringResource(R.string.edit_template_discard_title), color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.edit_template_discard_confirm), color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.common_cancel), color = TextSecondary)
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
                contentDescription = "",
                onClick = ::requestBack
            )
            Text(
                text = if (templateId == null) stringResource(R.string.edit_template_title_new)
                       else stringResource(R.string.edit_template_title_edit),
                style = SubTrackType.headlineS,
                color = TextPrimary
            )
            val canSave = editing?.form?.isValid == true && uiState !is EditTemplateUiState.Saving
            TextButton(
                onClick = { viewModel.save() },
                enabled = canSave
            ) {
                Text(
                    text = stringResource(R.string.edit_template_save),
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
                        text = if (templateId == null) stringResource(R.string.edit_template_eyebrow_new)
                               else stringResource(R.string.edit_template_eyebrow_edit)
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
                        Eyebrow(text = stringResource(R.string.edit_template_name_label))
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
                                placeholder = stringResource(R.string.edit_template_name_placeholder),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Tone selector card
                item {
                    SectionCard {
                        Eyebrow(text = stringResource(R.string.edit_template_tone_label))
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
                            Eyebrow(text = stringResource(R.string.edit_template_message_label))
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
                            Eyebrow(text = stringResource(R.string.edit_template_variables_label))
                            Text(
                                stringResource(R.string.edit_template_variables_hint),
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
                                text = if (form.previewExpanded) stringResource(R.string.edit_template_preview_collapse)
                                       else stringResource(R.string.edit_template_preview_toggle)
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
                                stringResource(R.string.edit_template_delete),
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

@Composable
private fun SectionCard(
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
private fun ToneOption(
    tone: TemplateTone,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (TemplateTone) -> Unit
) {
    val toneColor = Color(tone.accentColor)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(SubTrackShapes.base)
            .background(if (isSelected) toneColor.copy(alpha = 0.12f) else BgSurfaceEl)
            .border(1.dp, if (isSelected) toneColor.copy(alpha = 0.4f) else BorderDefault, SubTrackShapes.base)
            .clickable { onClick(tone) }
            .padding(horizontal = Spacing.m, vertical = Spacing.s)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(SubTrackShapes.circle)
                    .background(toneColor)
            )
            Text(
                text = tone.displayName,
                style = SubTrackType.bodyS,
                color = if (isSelected) toneColor else TextSecondary
            )
        }
    }
}

@Composable
private fun VariableChip(
    variable: TemplateVariable,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(SubTrackShapes.base)
            .background(BgSurfaceEl)
            .border(1.dp, BorderDefault, SubTrackShapes.base)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.s, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(variable.token, style = SubTrackType.mono.copy(fontSize = 10.sp), color = AccentGreen)
        Text("·", style = SubTrackType.monoXS, color = TextTertiary)
        Text(variable.displayLabel, style = SubTrackType.monoXS, color = TextSecondary)
    }
}

@Composable
private fun WhatsAppBubble(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = 12.dp, bottomEnd = 2.dp
                    )
                )
                .background(Color(0xFF1FAD4E))
                .padding(horizontal = Spacing.m, vertical = Spacing.s)
        ) {
            Text(
                text = text.ifBlank { "El mensaje aparecerá aquí…" },
                style = SubTrackType.bodyS,
                color = Color.White
            )
        }
    }
}

@Composable
private fun EmojiPickerSheetContent(
    selected: String,
    onEmojiSelected: (String) -> Unit
) {
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
        Text("Elige un emoji", style = SubTrackType.headlineS, color = TextPrimary)
        Spacer(Modifier.height(Spacing.m))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.height(200.dp)
        ) {
            items(COMMON_EMOJIS) { emoji ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(SubTrackShapes.base)
                        .background(if (emoji == selected) BgSurfaceEl else Color.Transparent)
                        .border(
                            1.dp,
                            if (emoji == selected) BorderStrong else Color.Transparent,
                            SubTrackShapes.base
                        )
                        .clickable { onEmojiSelected(emoji) }
                ) {
                    Text(emoji, fontSize = 22.sp, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun EditTemplateScreenPreview() {
    SubTrackTheme { EditTemplateScreen(templateId = "template_1", onBack = {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun EditTemplateScreenNewPreview() {
    SubTrackTheme { EditTemplateScreen(templateId = null, onBack = {}) }
}
