package com.gondroid.subtrack.feature.subscriptiondetail.admin

import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import com.gondroid.subtrack.platform.ExternalActions
import org.koin.compose.koinInject
import com.gondroid.subtrack.domain.util.formatTwoDecimals
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.common_error_generic
import com.gondroid.subtrack.resources.member_sheet_add_step1_title
import com.gondroid.subtrack.resources.member_sheet_added_toast
import com.gondroid.subtrack.resources.member_sheet_archived_toast
import com.gondroid.subtrack.resources.member_sheet_bulk_done
import com.gondroid.subtrack.resources.member_sheet_forgiven_toast
import com.gondroid.subtrack.resources.member_sheet_paid_toast
import com.gondroid.subtrack.resources.member_sheet_updated_toast
import com.gondroid.subtrack.resources.subscription_delete_desc
import com.gondroid.subtrack.resources.subscription_delete_title
import com.gondroid.subtrack.resources.subscription_deleted_toast
import com.gondroid.subtrack.resources.subscription_detail_admin_tag
import com.gondroid.subtrack.resources.subscription_detail_back_cd
import com.gondroid.subtrack.resources.subscription_detail_history_months
import com.gondroid.subtrack.resources.subscription_detail_history_title
import com.gondroid.subtrack.resources.subscription_detail_members_count
import com.gondroid.subtrack.resources.subscription_detail_members_title
import com.gondroid.subtrack.resources.subscription_detail_plan_label
import com.gondroid.subtrack.resources.subscription_menu_delete
import com.gondroid.subtrack.resources.subscription_paused_tag
import com.gondroid.subtrack.resources.subscription_paused_toast
import com.gondroid.subtrack.resources.subscription_resumed_toast
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.WhatsAppHelper
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.data.demo.DemoData
import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.MemberPaymentStatus
import com.gondroid.subtrack.domain.model.MonthlyPaymentSummary
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.ArchivedDivider
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.ArchivedMemberRow
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.CobranzaBar
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.MemberRow
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.TimelineItem
import com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.AddMemberStep1Sheet
import com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.AddMemberStep2Sheet
import com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.BulkReminderSheet
import com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.EditMemberDataSheet
import com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.EditMemberSheet
import com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.ExitRequestSheet
import com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.RemoveMemberSheet
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.domain.model.enums.isUnpaid
import com.gondroid.subtrack.feature.subscriptiondetail.admin.components.TimelineLegend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDetailScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onMemberHistory: (memberId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminDetailViewModel = koinViewModel<AdminDetailViewModel>(
        key = subscriptionId,
        parameters = { parametersOf(subscriptionId) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
    val externalActions = koinInject<ExternalActions>()
    val messenger = LocalSnackbarMessenger.current
    var deleting by remember { mutableStateOf(false) }

    // Collect one-shot events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val msg = when (event) {
                is AdminDetailEvent.MemberAdded ->
                    getString(Res.string.member_sheet_added_toast, event.name)
                is AdminDetailEvent.MemberMarkedAsPaid ->
                    getString(Res.string.member_sheet_paid_toast, event.name)
                is AdminDetailEvent.MemberArchived ->
                    getString(Res.string.member_sheet_archived_toast, event.name)
                is AdminDetailEvent.MemberForgiven ->
                    getString(Res.string.member_sheet_forgiven_toast, event.name)
                is AdminDetailEvent.MemberUpdated ->
                    getString(Res.string.member_sheet_updated_toast, event.name)
                is AdminDetailEvent.BulkReminderSent ->
                    getPluralString(Res.plurals.member_sheet_bulk_done, event.count, event.count)
                is AdminDetailEvent.PausedChanged -> getString(
                    if (event.paused) Res.string.subscription_paused_toast else Res.string.subscription_resumed_toast
                )
                AdminDetailEvent.Deleted -> getString(Res.string.subscription_deleted_toast)
                AdminDetailEvent.Error -> getString(Res.string.common_error_generic)
            }
            messenger.show(msg)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        when (val state = uiState) {
            AdminDetailUiState.Loading -> AdminDetailSkeleton(modifier = Modifier.padding(top = Spacing.s))
            // Right after deleting, the router leaves this screen; don't flash "not found" meanwhile.
            AdminDetailUiState.NotFound -> if (!deleting) AdminDetailNotFound(onBack = onBack)
            is AdminDetailUiState.Success -> AdminDetailContent(
                data = state.data,
                onBack = onBack,
                onAddMember = viewModel::openAddMember,
                onMemberTap = viewModel::openEditMember,
                onArchivedTap = viewModel::openEditMember,
                onCobranzaTap = viewModel::openBulkReminder,
                onEdit = onEdit,
                onSetPaused = viewModel::setPaused,
                onDelete = {
                    deleting = true
                    viewModel.deleteSubscription()
                }
            )
        }

        // ── ModalBottomSheet host ─────────────────────────────────────────────
        if (activeSheet !is ActiveSheet.None) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = viewModel::closeSheet,
                sheetState = sheetState,
                containerColor = Color.Transparent,
                dragHandle = null,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                val successState = uiState as? AdminDetailUiState.Success
                val sub = successState?.data?.subscription

                when (val sheet = activeSheet) {
                    is ActiveSheet.AddMemberStep1 ->
                        AddMemberStep1Sheet(
                            onContactSelected = viewModel::onContactSelected,
                            onDismiss = viewModel::closeSheet
                        )

                    is ActiveSheet.AddMemberStep2 ->
                        AddMemberStep2Sheet(
                            contact = sheet.selectedContact,
                            subscriptionName = sub?.name ?: "",
                            subscriptionTotal = sub?.totalAmount ?: 0.0,
                            currentMembersTotal = successState?.data?.activeMembers
                                ?.sumOf { it.shareAmount } ?: 0.0,
                            typicalShareAmount = successState?.data?.activeMembers
                                ?.firstOrNull()?.shareAmount ?: 0.0,
                            onConfirm = viewModel::addMember,
                            onBack = viewModel::closeSheet
                        )

                    is ActiveSheet.EditMember -> {
                        val member = successState?.data?.activeMembers?.find { it.id == sheet.memberId }
                        if (member != null) {
                            EditMemberSheet(
                                member = member,
                                subscriptionName = sub?.name ?: "",
                                onSendReminder = {
                                    val msg = WhatsAppHelper.buildReminderMessage(
                                        memberName = member.name,
                                        serviceName = sub?.name ?: "",
                                        amount = formatTwoDecimals(member.shareAmount),
                                        adminName = successState?.data?.adminName.orEmpty()
                                    )
                                    WhatsAppHelper.openWhatsApp(externalActions, member.phone, msg)?.let(messenger::show)
                                    viewModel.closeSheet()
                                },
                                onMarkAsPaid = { viewModel.markAsPaid(member.id) },
                                onEditData = { viewModel.openEditMemberData(member.id) },
                                onViewHistory = {
                                    viewModel.closeSheet()
                                    onMemberHistory(member.id)
                                },
                                onRemove = {
                                    val hasPending = member.currentStatus.isUnpaid
                                    if (hasPending) {
                                        viewModel.openRemoveMember(member.id)
                                    } else {
                                        viewModel.archiveMember(member.id, com.gondroid.subtrack.domain.model.enums.DebtDecision.FORGIVE)
                                    }
                                },
                                onDismiss = viewModel::closeSheet
                            )
                        }
                    }

                    is ActiveSheet.EditMemberData -> {
                        val member = successState?.data?.activeMembers?.find { it.id == sheet.memberId }
                        if (member != null) {
                            EditMemberDataSheet(
                                member = member,
                                onConfirm = viewModel::updateMember,
                                onDismiss = viewModel::closeSheet
                            )
                        }
                    }

                    is ActiveSheet.RemoveMember -> {
                        val member = successState?.data?.activeMembers?.find { it.id == sheet.memberId }
                        if (member != null) {
                            RemoveMemberSheet(
                                member = member,
                                onConfirm = { decision -> viewModel.archiveMember(member.id, decision) },
                                onDismiss = viewModel::closeSheet
                            )
                        }
                    }

                    is ActiveSheet.ExitRequest ->
                        ExitRequestSheet(
                            requestId = sheet.requestId,
                            memberName = "",
                            subscriptionName = sub?.name ?: "",
                            onApprove = viewModel::closeSheet,
                            onReject = viewModel::closeSheet,
                            onDismiss = viewModel::closeSheet
                        )

                    is ActiveSheet.BulkReminder -> {
                        val pending = successState?.data?.pendingMembers.orEmpty()
                        BulkReminderSheet(
                            pendingItems = pending.map {
                                com.gondroid.subtrack.feature.subscriptiondetail.admin.sheets.BulkReminderItem(
                                    member = it,
                                    subscriptionId = sub?.id ?: "",
                                    subscriptionName = sub?.name ?: ""
                                )
                            },
                            templates = com.gondroid.subtrack.data.demo.DemoData.templates,
                            adminName = successState?.data?.adminName.orEmpty(),
                            onDone = { sent ->
                                viewModel.onBulkReminderSent(sent)
                                viewModel.closeSheet()
                            }
                        )
                    }

                    ActiveSheet.None -> Unit
                }
            }
        }
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
internal fun AdminDetailContent(
    data: AdminDetailData,
    onBack: () -> Unit,
    onAddMember: () -> Unit,
    onMemberTap: (String) -> Unit,
    onArchivedTap: (String) -> Unit,
    onCobranzaTap: () -> Unit,
    onEdit: () -> Unit,
    onSetPaused: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val sub = data.subscription
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(Res.string.subscription_delete_title, sub.name), color = TextPrimary) },
            text = { Text(stringResource(Res.string.subscription_delete_desc), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete()
                }) {
                    Text(stringResource(Res.string.subscription_menu_delete), color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(Res.string.common_cancel), color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }
    val brandColor = sub.brandColor.toComposeColor()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Spacing.huge)
    ) {
        // Top nav
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l, vertical = Spacing.s),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                STIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.subscription_detail_back_cd),
                    onClick = onBack
                )
                SubscriptionMenu(
                    isPaused = sub.isPaused,
                    onEdit = onEdit,
                    onSetPaused = onSetPaused,
                    onDelete = { showDeleteConfirm = true }
                )
            }
        }

        // Hero
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.base),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                ServiceLogo(serviceName = sub.name, brandColor = brandColor, size = 64.dp, serviceId = sub.serviceTemplateId)
                Text(
                    text = sub.name,
                    style = SubTrackType.displayS,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                val activeCount = data.activeMembers.size + 1
                Text(
                    text = pluralStringResource(
                        Res.plurals.subscription_detail_plan_label,
                        activeCount,
                        sub.category.name.lowercase().replaceFirstChar { it.uppercase() },
                        activeCount
                    ),
                    style = SubTrackType.monoXS,
                    color = TextTertiary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Badge(text = stringResource(Res.string.subscription_detail_admin_tag), variant = BadgeVariant.ADMIN)
                    if (sub.isPaused) {
                        Badge(text = stringResource(Res.string.subscription_paused_tag), variant = BadgeVariant.NEUTRAL)
                    }
                }
                AmountDisplay(amount = sub.totalAmount, size = AmountSize.MEDIUM)
                CutoffPill(cutoffDay = sub.cutoffDay)
            }
        }

        item { Spacer(Modifier.height(Spacing.l)) }

        // Members header with + button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.base),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.subscription_detail_members_title),
                    style = SubTrackType.headlineS,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            Res.string.subscription_detail_members_count,
                            data.activeMembers.size,
                            formatTwoDecimals(sub.totalAmount / (data.activeMembers.size + 1))
                        ),
                        style = SubTrackType.monoXS,
                        color = TextTertiary
                    )
                    Spacer(Modifier.width(Spacing.s))
                    STIconButton(
                        icon = Icons.Outlined.Add,
                        contentDescription = stringResource(Res.string.member_sheet_add_step1_title),
                        onClick = onAddMember
                    )
                }
            }
        }

        // Member rows
        items(data.activeMembers, key = { it.id }) { member ->
            MemberRow(
                member = member,
                onClick = { onMemberTap(member.id) },
                modifier = Modifier.padding(horizontal = Spacing.base)
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = (Spacing.base.value + 40 + Spacing.m.value).dp),
                color = BorderDefault,
                thickness = 0.5.dp
            )
        }

        // CobranzaBar
        if (data.pendingPaymentsCount > 0) {
            item {
                Spacer(Modifier.height(Spacing.m))
                CobranzaBar(
                    pendingCount = data.pendingPaymentsCount,
                    singlePendingName = data.singlePendingName,
                    totalAmount = data.pendingAmount,
                    onClick = onCobranzaTap,
                    modifier = Modifier.padding(horizontal = Spacing.base)
                )
            }
        }

        item { Spacer(Modifier.height(Spacing.l)) }

        // History
        item {
            SectionHeader(
                title = stringResource(Res.string.subscription_detail_history_title),
                trailing = pluralStringResource(Res.plurals.subscription_detail_history_months, data.monthlyHistory.size, data.monthlyHistory.size),
                modifier = Modifier.padding(horizontal = Spacing.base)
            )
        }

        items(data.monthlyHistory, key = { it.monthKey }) { summary ->
            TimelineItem(summary = summary, modifier = Modifier.padding(horizontal = Spacing.base))
        }
        if (data.monthlyHistory.isNotEmpty()) {
            item {
                TimelineLegend(Modifier.padding(horizontal = Spacing.base).padding(top = Spacing.xs))
            }
        }

        // Archived section
        if (data.archivedMembers.isNotEmpty()) {
            item { Spacer(Modifier.height(Spacing.l)) }
            item {
                ArchivedDivider(
                    count = data.archivedMembers.size,
                    modifier = Modifier.padding(horizontal = Spacing.base)
                )
            }
            item {
                Spacer(Modifier.height(Spacing.s))
                ArchivedHelpCard(modifier = Modifier.padding(horizontal = Spacing.base))
            }
            items(data.archivedMembers, key = { "archived_${it.id}" }) { member ->
                Spacer(Modifier.height(Spacing.s))
                ArchivedMemberRow(
                    member = member,
                    onClick = { onArchivedTap(member.id) },
                    modifier = Modifier.padding(horizontal = Spacing.base)
                )
            }
        }
    }
}

// ── Sub-components ────────────────────────────────────────────────────────────

internal fun previewData(includeArchived: Boolean = true): AdminDetailData {
    val sub = DemoData.subscriptions.first { it.id == "sub_netflix" }
    val active = sub.members
    val archived = if (includeArchived) listOf(DemoData.memCarlos) else emptyList()
    val history = listOf(
        MonthlyPaymentSummary("2026-02", 21.96,
            active.map { MemberPaymentStatus(it.id, it.name, if (it.id == "mem_maria") PaymentStatus.LATE else PaymentStatus.PAID) }),
        MonthlyPaymentSummary("2026-03", 32.94,
            active.map { MemberPaymentStatus(it.id, it.name, PaymentStatus.PAID) }),
        MonthlyPaymentSummary("2026-04", 10.98,
            active.map { MemberPaymentStatus(it.id, it.name, it.currentStatus) })
    )
    return buildAdminDetailData(sub, active, archived, history, adminName = "Gonzalo")
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success – con archivados")
@Composable
internal fun AdminDetailSuccessPreview() {
    SubTrackTheme {
        AdminDetailContent(
            data = previewData(includeArchived = true),
            onBack = {}, onAddMember = {}, onMemberTap = {}, onArchivedTap = {}, onCobranzaTap = {},
            onEdit = {}, onSetPaused = {}, onDelete = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success – sin archivados")
@Composable
internal fun AdminDetailNoArchivedPreview() {
    SubTrackTheme {
        AdminDetailContent(
            data = previewData(includeArchived = false),
            onBack = {}, onAddMember = {}, onMemberTap = {}, onArchivedTap = {}, onCobranzaTap = {},
            onEdit = {}, onSetPaused = {}, onDelete = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Loading")
@Composable
internal fun AdminDetailLoadingPreview() {
    SubTrackTheme { AdminDetailSkeleton() }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "NotFound")
@Composable
internal fun AdminDetailNotFoundPreview() {
    SubTrackTheme { AdminDetailNotFound(onBack = {}) }
}
