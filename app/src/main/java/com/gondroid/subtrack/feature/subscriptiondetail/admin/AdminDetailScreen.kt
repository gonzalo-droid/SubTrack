package com.gondroid.subtrack.feature.subscriptiondetail.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.WhatsAppHelper
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.data.mock.MockData
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDetailScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminDetailViewModel = viewModel(factory = AdminDetailViewModelFactory(subscriptionId))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Collect one-shot events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val msg = when (event) {
                is AdminDetailEvent.MemberAdded ->
                    context.getString(R.string.member_sheet_added_toast, event.name)
                is AdminDetailEvent.MemberMarkedAsPaid ->
                    context.getString(R.string.member_sheet_paid_toast, event.name)
                is AdminDetailEvent.MemberArchived ->
                    context.getString(R.string.member_sheet_archived_toast, event.name)
                is AdminDetailEvent.MemberForgiven ->
                    context.getString(R.string.member_sheet_forgiven_toast, event.name)
                is AdminDetailEvent.MemberUpdated ->
                    context.getString(R.string.member_sheet_updated_toast, event.name)
                is AdminDetailEvent.BulkReminderStarted ->
                    context.getString(R.string.member_sheet_bulk_started_toast, 0)
                is AdminDetailEvent.Error -> event.message
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        when (val state = uiState) {
            AdminDetailUiState.Loading -> AdminDetailSkeleton(modifier = Modifier.padding(top = Spacing.s))
            AdminDetailUiState.NotFound -> AdminDetailNotFound(onBack = onBack)
            is AdminDetailUiState.Success -> AdminDetailContent(
                data = state.data,
                onBack = onBack,
                onAddMember = viewModel::openAddMember,
                onMemberTap = viewModel::openEditMember,
                onArchivedTap = viewModel::openEditMember,
                onCobranzaTap = viewModel::openBulkReminder
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
                                        amount = "%.2f".format(member.shareAmount),
                                        adminName = successState?.data?.subscription?.ownerId ?: ""
                                    )
                                    WhatsAppHelper.openWhatsApp(context, member.phone, msg)
                                    viewModel.closeSheet()
                                },
                                onMarkAsPaid = { viewModel.markAsPaid(member.id) },
                                onEditData = { viewModel.openEditMemberData(member.id) },
                                onViewHistory = {
                                    // TODO: [milestone-7] Navigate to member history screen
                                    Toast.makeText(context, "Próximamente: historial", Toast.LENGTH_SHORT).show()
                                },
                                onRemove = {
                                    val hasPending = member.currentStatus == PaymentStatus.OVERDUE ||
                                            member.currentStatus == PaymentStatus.PENDING
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
                        val pending = successState?.data?.activeMembers?.filter {
                            it.currentStatus == PaymentStatus.OVERDUE ||
                                    it.currentStatus == PaymentStatus.PENDING
                        } ?: emptyList()
                        BulkReminderSheet(
                            pendingMembers = pending,
                            templates = com.gondroid.subtrack.data.mock.MockData.templates,
                            adminName = "Gonzalo",
                            subscriptionName = sub?.name ?: "",
                            onDone = {
                                viewModel.onBulkReminderSent()
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
private fun AdminDetailContent(
    data: AdminDetailData,
    onBack: () -> Unit,
    onAddMember: () -> Unit,
    onMemberTap: (String) -> Unit,
    onArchivedTap: (String) -> Unit,
    onCobranzaTap: () -> Unit
) {
    val context = LocalContext.current
    val sub = data.subscription
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
                    contentDescription = stringResource(R.string.subscription_detail_back_cd),
                    onClick = onBack
                )
                STIconButton(
                    icon = Icons.Outlined.MoreVert,
                    contentDescription = stringResource(R.string.subscription_detail_overflow_cd),
                    onClick = {
                        Toast.makeText(context, "Próximamente: opciones", Toast.LENGTH_SHORT).show()
                    }
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
                ServiceLogo(serviceName = sub.name, brandColor = brandColor, size = 64.dp)
                Text(
                    text = sub.name,
                    style = SubTrackType.displayS,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                val activeCount = data.activeMembers.size + 1
                Text(
                    text = stringResource(
                        R.string.subscription_detail_plan_label,
                        sub.category.name.lowercase().replaceFirstChar { it.uppercase() },
                        activeCount
                    ),
                    style = SubTrackType.monoXS,
                    color = TextTertiary
                )
                Badge(text = stringResource(R.string.subscription_detail_admin_tag), variant = BadgeVariant.ADMIN)
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
                    text = stringResource(R.string.subscription_detail_members_title),
                    style = SubTrackType.headlineS,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            R.string.subscription_detail_members_count,
                            data.activeMembers.size,
                            "%.2f".format(sub.totalAmount / (data.activeMembers.size + 1))
                        ),
                        style = SubTrackType.monoXS,
                        color = TextTertiary
                    )
                    Spacer(Modifier.width(Spacing.s))
                    STIconButton(
                        icon = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.member_sheet_add_step1_title),
                        onClick = onAddMember,
                        modifier = Modifier.size(28.dp)
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
                title = stringResource(R.string.subscription_detail_history_title),
                trailing = stringResource(R.string.subscription_detail_history_months, data.monthlyHistory.size),
                modifier = Modifier.padding(horizontal = Spacing.base)
            )
        }

        items(data.monthlyHistory, key = { it.monthKey }) { summary ->
            TimelineItem(summary = summary, modifier = Modifier.padding(horizontal = Spacing.base))
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

@Composable
private fun CutoffPill(cutoffDay: Int) {
    val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
    val monthNames = listOf(
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    )
    val monthIndex = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
    val dateStr = "$cutoffDay de ${monthNames[monthIndex]}"
    val (bg, color, text) = if (cutoffDay == today) {
        Triple(AccentAmberBg, AccentAmber, stringResource(R.string.subscription_detail_cutoff_today, dateStr))
    } else {
        Triple(
            Color.White.copy(alpha = 0.06f),
            TextSecondary,
            stringResource(R.string.subscription_detail_cutoff_day, cutoffDay, dateStr)
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
private fun SectionHeader(title: String, trailing: String, modifier: Modifier = Modifier) {
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
private fun ArchivedHelpCard(modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.subscription_detail_archived_help),
            style = SubTrackType.bodyS,
            color = AccentAmber
        )
    }
}

@Composable
private fun AdminDetailNotFound(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.subscription_detail_not_found_title),
            style = SubTrackType.headlineL,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = stringResource(R.string.subscription_detail_not_found_subtitle),
            style = SubTrackType.bodyM,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.xl))
        PrimaryButton(
            text = stringResource(R.string.common_back),
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private fun previewData(includeArchived: Boolean = true): AdminDetailData {
    val sub = MockData.subscriptions.first { it.id == "sub_netflix" }
    val active = sub.members
    val archived = if (includeArchived) listOf(MockData.memCarlos) else emptyList()
    val history = listOf(
        MonthlyPaymentSummary("2026-02", 21.96,
            active.map { MemberPaymentStatus(it.id, it.name, if (it.id == "mem_maria") PaymentStatus.LATE else PaymentStatus.PAID) }),
        MonthlyPaymentSummary("2026-03", 32.94,
            active.map { MemberPaymentStatus(it.id, it.name, PaymentStatus.PAID) }),
        MonthlyPaymentSummary("2026-04", 10.98,
            active.map { MemberPaymentStatus(it.id, it.name, it.currentStatus) })
    )
    return buildAdminDetailData(sub, active, archived, history)
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success – con archivados")
@Composable
private fun AdminDetailSuccessPreview() {
    SubTrackTheme {
        AdminDetailContent(
            data = previewData(includeArchived = true),
            onBack = {}, onAddMember = {}, onMemberTap = {}, onArchivedTap = {}, onCobranzaTap = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Success – sin archivados")
@Composable
private fun AdminDetailNoArchivedPreview() {
    SubTrackTheme {
        AdminDetailContent(
            data = previewData(includeArchived = false),
            onBack = {}, onAddMember = {}, onMemberTap = {}, onArchivedTap = {}, onCobranzaTap = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Loading")
@Composable
private fun AdminDetailLoadingPreview() {
    SubTrackTheme { AdminDetailSkeleton() }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "NotFound")
@Composable
private fun AdminDetailNotFoundPreview() {
    SubTrackTheme { AdminDetailNotFound(onBack = {}) }
}
