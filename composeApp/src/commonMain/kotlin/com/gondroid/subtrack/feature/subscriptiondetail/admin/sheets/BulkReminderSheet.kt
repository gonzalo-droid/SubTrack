package com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets

import com.gondroid.subtrack.platform.ExternalActions

import org.koin.compose.koinInject

import com.gondroid.subtrack.domain.util.formatTwoDecimals

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.member_sheet_bulk_done
import com.gondroid.subtrack.resources.member_sheet_bulk_helper
import com.gondroid.subtrack.resources.member_sheet_bulk_next
import com.gondroid.subtrack.resources.member_sheet_bulk_owes
import com.gondroid.subtrack.resources.member_sheet_bulk_preview_label
import com.gondroid.subtrack.resources.member_sheet_bulk_send
import com.gondroid.subtrack.resources.member_sheet_bulk_subtitle
import com.gondroid.subtrack.resources.member_sheet_bulk_template_label
import com.gondroid.subtrack.resources.member_sheet_bulk_title

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
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
import com.gondroid.subtrack.core.util.WhatsAppHelper
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.model.enums.TemplateTone
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger
import androidx.compose.runtime.saveable.rememberSaveable

data class BulkReminderItem(
    val member: Member,
    val subscriptionId: String,
    val subscriptionName: String
)

@Composable
fun BulkReminderSheet(
    pendingItems: List<BulkReminderItem>,
    templates: List<Template>,
    adminName: String,
    onDone: (sentCount: Int) -> Unit
) {
    val externalActions = koinInject<ExternalActions>()
    val messenger = LocalSnackbarMessenger.current
    var selectedMemberIds by remember { mutableStateOf(pendingItems.map { it.member.id }.toSet()) }
    var selectedTemplateId by remember { mutableStateOf(templates.firstOrNull { it.isDefault }?.id ?: templates.firstOrNull()?.id) }
    // Saveable so a rotation mid-send keeps the position and the count
    var sendingIndex by rememberSaveable { mutableIntStateOf(-1) }
    var sentCount by rememberSaveable { mutableIntStateOf(0) }

    // A failed open closes the sheet so the error snackbar isn't hidden behind it.
    fun send(phone: String, message: String): Boolean {
        val error = WhatsAppHelper.openWhatsApp(externalActions, phone, message)
        if (error == null) sentCount++ else {
            messenger.show(error)
            onDone(sentCount)
        }
        return error == null
    }

    val pendingMembers = pendingItems.map { it.member }
    val selectedMembers = pendingMembers.filter { it.id in selectedMemberIds }
    val selectedItems = pendingItems.filter { it.member.id in selectedMemberIds }
    val template = templates.find { it.id == selectedTemplateId }

    if (sendingIndex >= 0) {
        // Sending mode: show current member + next button
        val currentItem = selectedItems.getOrNull(sendingIndex)
        if (currentItem == null) {
            LaunchedEffect(Unit) { onDone(sentCount) }
            return
        }
        val current = currentItem.member
        SheetContainer {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.base)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SheetHandle()
                Spacer(Modifier.height(Spacing.m))
                Text(
                    text = stringResource(
                        Res.string.member_sheet_bulk_next,
                        sendingIndex + 1,
                        selectedItems.size
                    ),
                    style = SubTrackType.headlineS,
                    color = TextPrimary
                )
                Spacer(Modifier.height(Spacing.s))
                Avatar(name = current.name, size = 56.dp)
                Spacer(Modifier.height(Spacing.s))
                Text(text = current.name, style = SubTrackType.titleL, color = TextPrimary)
                Spacer(Modifier.height(Spacing.l))
                PrimaryButton(
                    text = if (sendingIndex + 1 < selectedItems.size)
                        stringResource(Res.string.member_sheet_bulk_next, sendingIndex + 2, selectedItems.size)
                    else
                        pluralStringResource(Res.plurals.member_sheet_bulk_done, selectedItems.size, selectedItems.size),
                    onClick = {
                        val nextItem = selectedItems.getOrNull(sendingIndex + 1)
                        if (nextItem != null) {
                            val next = nextItem.member
                            val msg = WhatsAppHelper.buildReminderMessage(
                                memberName = next.name,
                                serviceName = nextItem.subscriptionName,
                                amount = formatTwoDecimals(next.shareAmount),
                                adminName = adminName
                            )
                            if (send(next.phone, msg)) sendingIndex++
                        } else {
                            onDone(sentCount)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Spacing.xl))
            }
        }
        return
    }

    SheetContainer {
        Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
            SheetHandle()
            Spacer(Modifier.height(Spacing.m))

            Text(
                text = stringResource(Res.string.member_sheet_bulk_title),
                style = SubTrackType.headlineL,
                color = TextPrimary
            )
            Text(
                text = pluralStringResource(
                    Res.plurals.member_sheet_bulk_subtitle,
                    selectedMembers.size,
                    selectedMembers.size,
                    formatTwoDecimals(selectedMembers.sumOf { it.shareAmount })
                ),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
            Spacer(Modifier.height(Spacing.l))

            // Member checkboxes
            pendingMembers.forEach { member ->
                val checked = member.id in selectedMemberIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedMemberIds = if (checked)
                                selectedMemberIds - member.id
                            else
                                selectedMemberIds + member.id
                        }
                        .padding(vertical = Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(name = member.name, size = 36.dp)
                    Spacer(Modifier.width(Spacing.m))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = member.name, style = SubTrackType.titleL, color = TextPrimary)
                        Text(
                            text = stringResource(
                                Res.string.member_sheet_bulk_owes,
                                formatTwoDecimals(member.shareAmount)
                            ),
                            style = SubTrackType.monoXS,
                            color = TextTertiary
                        )
                    }
                    if (checked) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(22.dp)
                                .clip(SubTrackShapes.s)
                                .background(AccentGreen)
                        ) {
                            androidx.compose.material3.Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(SubTrackShapes.s)
                                .border(1.5.dp, BorderDefault, SubTrackShapes.s)
                        )
                    }
                }
            }
            Spacer(Modifier.height(Spacing.m))

            // Template selector
            Text(
                text = stringResource(Res.string.member_sheet_bulk_template_label),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
            Spacer(Modifier.height(Spacing.s))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(templates, key = { it.id }) { tmpl ->
                    val isSelected = tmpl.id == selectedTemplateId
                    Text(
                        text = "${tmpl.emoji} ${tmpl.name}",
                        style = SubTrackType.monoXS,
                        color = if (isSelected) AccentGreen else TextSecondary,
                        modifier = Modifier
                            .clip(SubTrackShapes.circle)
                            .background(if (isSelected) AccentGreenBg else BgSurfaceEl)
                            .border(1.dp, if (isSelected) AccentGreen.copy(alpha = 0.3f) else BorderDefault, SubTrackShapes.circle)
                            .clickable { selectedTemplateId = tmpl.id }
                            .padding(horizontal = Spacing.m, vertical = Spacing.xs)
                    )
                }
            }

            // Message preview
            if (template != null && selectedItems.isNotEmpty()) {
                val previewItem = selectedItems.first()
                val previewMember = previewItem.member
                val previewMsg = WhatsAppHelper.buildReminderMessage(
                    memberName = previewMember.name,
                    serviceName = previewItem.subscriptionName,
                    amount = formatTwoDecimals(previewMember.shareAmount),
                    adminName = adminName
                )
                Spacer(Modifier.height(Spacing.m))
                Text(
                    text = stringResource(Res.string.member_sheet_bulk_preview_label),
                    style = SubTrackType.monoXS,
                    color = TextTertiary
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = previewMsg,
                    style = SubTrackType.bodyS,
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SubTrackShapes.base)
                        .background(BgSurface)
                        .padding(Spacing.m)
                )
            }

            Spacer(Modifier.height(Spacing.s))
            Text(
                text = stringResource(Res.string.member_sheet_bulk_helper),
                style = SubTrackType.bodyXS,
                color = TextTertiary
            )
            Spacer(Modifier.height(Spacing.m))

            PrimaryButton(
                text = pluralStringResource(Res.plurals.member_sheet_bulk_send, selectedItems.size, selectedItems.size),
                onClick = {
                    if (selectedItems.isNotEmpty() && template != null) {
                        val firstItem = selectedItems.first()
                        val first = firstItem.member
                        val msg = WhatsAppHelper.buildReminderMessage(
                            memberName = first.name,
                            serviceName = firstItem.subscriptionName,
                            amount = formatTwoDecimals(first.shareAmount),
                            adminName = adminName
                        )
                        if (send(first.phone, msg)) sendingIndex = 0
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedItems.isNotEmpty()
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun BulkReminderSheetPreview() {
    SubTrackTheme {
        BulkReminderSheet(
            pendingItems = listOf(
                BulkReminderItem(
                    member = Member("m1", null, "María Rodríguez", "+51 912 345 678", "Perfil 2", 10.98, false, PaymentStatus.OVERDUE, 0L),
                    subscriptionId = "sub_netflix",
                    subscriptionName = "Netflix"
                ),
                BulkReminderItem(
                    member = Member("m2", null, "Roberto Vargas", "+51 978 901 234", "Perfil 3", 10.98, false, PaymentStatus.PENDING, 0L),
                    subscriptionId = "sub_netflix",
                    subscriptionName = "Netflix"
                )
            ),
            templates = listOf(
                Template("t1", "Amigable", "👋", TemplateTone.FRIENDLY, "Hola {nombre}! Recuerda pagar {servicio} — S/{monto}. ¡Gracias!", true)
            ),
            adminName = "Gonzalo",
            onDone = {}
        )
    }
}
