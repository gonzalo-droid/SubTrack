package com.gondroid.subtrack.feature.subscriptiondetail.member

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.components.text.StatusPill
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgPage
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
import com.gondroid.subtrack.core.util.WhatsAppHelper
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.domain.model.AdminPublicInfo
import com.gondroid.subtrack.domain.model.MemberSubscriptionView
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(
    subscriptionId: String,
    memberId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: MemberDetailViewModel = viewModel(
        key = "$subscriptionId/$memberId",
        factory = MemberDetailViewModelFactory(memberId, subscriptionId)
    )
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val activeSheet by vm.activeSheet.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is MemberDetailEvent.PaymentMarked ->
                    Toast.makeText(context, "Pago registrado", Toast.LENGTH_SHORT).show()
                is MemberDetailEvent.ExitRequested ->
                    Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                is MemberDetailEvent.Error ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (val state = uiState) {
        is MemberDetailUiState.Loading -> {}
        is MemberDetailUiState.NotFound -> { onBack() }
        is MemberDetailUiState.Success -> {
            MemberDetailContent(
                view = state.view,
                recentPayments = state.recentPayments,
                onBack = onBack,
                onMarkAsPaid = vm::openMarkAsPaid,
                onRequestExit = vm::openRequestExit,
                modifier = modifier
            )

            // Sheets
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            if (activeSheet != MemberActionSheet.None) {
                ModalBottomSheet(
                    onDismissRequest = vm::closeSheet,
                    sheetState = sheetState,
                    containerColor = BgSheet
                ) {
                    when (activeSheet) {
                        is MemberActionSheet.MarkAsPaid -> MarkAsPaidSheetContent(
                            shareAmount = state.view.myShareAmount,
                            adminName = state.view.adminInfo.name,
                            onConfirm = { note -> vm.markAsPaid(note) },
                            onCancel = vm::closeSheet
                        )
                        is MemberActionSheet.RequestExit -> RequestExitSheetContent(
                            serviceName = state.view.serviceName,
                            onConfirm = vm::requestExit,
                            onCancel = vm::closeSheet
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberDetailContent(
    view: MemberSubscriptionView,
    recentPayments: List<Payment>,
    onBack: () -> Unit,
    onMarkAsPaid: () -> Unit,
    onRequestExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        // Top bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.base, vertical = Spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                STIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.common_back),
                    onClick = onBack
                )
                Spacer(Modifier.width(Spacing.s))
                Text(view.serviceName, style = SubTrackType.titleL, color = TextPrimary, modifier = Modifier.weight(1f))
            }
        }

        // Hero card
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.base)
                    .fillMaxWidth()
                    .clip(SubTrackShapes.l)
                    .background(BgSurface)
                    .border(1.dp, BorderDefault, SubTrackShapes.l)
                    .padding(Spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ServiceLogo(
                    serviceName = view.serviceName,
                    brandColor = view.brandColor.toComposeColor(),
                    size = 56.dp,
                    serviceId = view.serviceTemplateId
                )
                Spacer(Modifier.height(Spacing.m))
                Eyebrow(text = stringResource(R.string.member_detail_my_share))
                Spacer(Modifier.height(Spacing.xs))
                AmountDisplay(
                    amount = view.myShareAmount,
                    currencySymbol = if (view.currency == "USD") "$" else "S/",
                    size = AmountSize.LARGE
                )
                Spacer(Modifier.height(Spacing.s))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(status = view.myCurrentStatus)
                    Text(
                        when (view.cycle) {
                            BillingCycle.MONTHLY -> stringResource(R.string.member_detail_cycle_monthly)
                            BillingCycle.YEARLY -> stringResource(R.string.member_detail_cycle_yearly)
                            else -> view.cycle.name.lowercase()
                        },
                        style = SubTrackType.monoXS,
                        color = TextTertiary
                    )
                }
            }
            Spacer(Modifier.height(Spacing.m))
        }

        // Pending exit request banner
        if (view.pendingExitRequest != null) {
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = Spacing.base)
                        .fillMaxWidth()
                        .clip(SubTrackShapes.m)
                        .background(AccentAmberBg)
                        .border(1.dp, AccentAmber.copy(alpha = 0.3f), SubTrackShapes.m)
                        .padding(Spacing.m),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Warning, null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                    Text(
                        stringResource(R.string.member_detail_exit_pending_banner),
                        style = SubTrackType.bodyXS,
                        color = AccentAmber
                    )
                }
                Spacer(Modifier.height(Spacing.m))
            }
        }

        // Estado card
        item {
            SectionCard(modifier = Modifier.padding(horizontal = Spacing.base)) {
                Eyebrow(text = stringResource(R.string.member_detail_status_section))
                Spacer(Modifier.height(Spacing.s))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "S/ %.2f".format(view.myShareAmount),
                            style = SubTrackType.headlineL,
                            color = when (view.myCurrentStatus) {
                                PaymentStatus.PAID -> AccentGreen
                                PaymentStatus.OVERDUE -> AccentRed
                                else -> AccentAmber
                            }
                        )
                        Text(
                            "Corte día ${view.cutoffDay}",
                            style = SubTrackType.bodyXS,
                            color = TextTertiary
                        )
                    }
                    if (view.myCurrentStatus != PaymentStatus.PAID) {
                        Text(
                            stringResource(R.string.member_detail_mark_paid),
                            style = SubTrackType.monoXS,
                            color = AccentGreen,
                            modifier = Modifier
                                .clip(SubTrackShapes.s)
                                .background(AccentGreen.copy(alpha = 0.12f))
                                .clickable(onClick = onMarkAsPaid)
                                .padding(horizontal = Spacing.s, vertical = Spacing.xs)
                        )
                    }
                }

                // Mini timeline — last 3 payments
                if (recentPayments.isNotEmpty()) {
                    Spacer(Modifier.height(Spacing.s))
                    HorizontalDivider(color = BorderDefault)
                    Spacer(Modifier.height(Spacing.s))
                    val last3 = recentPayments.take(3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        last3.forEach { payment ->
                            val color = when (payment.status) {
                                PaymentStatus.PAID -> AccentGreen
                                PaymentStatus.LATE -> AccentAmber
                                PaymentStatus.OVERDUE -> AccentRed
                                PaymentStatus.PENDING -> TextTertiary
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(payment.monthKey.takeLast(2) + "/" + payment.monthKey.take(4).takeLast(2),
                                    style = SubTrackType.monoXS, color = TextTertiary)
                                Spacer(Modifier.height(2.dp))
                                Text("●", style = SubTrackType.monoXS, color = color)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(Spacing.m))
        }

        // Admin contact card
        item {
            SectionCard(modifier = Modifier.padding(horizontal = Spacing.base)) {
                Eyebrow(text = stringResource(R.string.member_detail_admin_section))
                Spacer(Modifier.height(Spacing.s))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        Avatar(name = view.adminInfo.name, size = 36.dp)
                        Text(view.adminInfo.name, style = SubTrackType.bodyM, color = TextPrimary)
                    }
                    if (view.adminInfo.phone.isNotBlank()) {
                        Text(
                            stringResource(R.string.member_detail_contact_admin),
                            style = SubTrackType.monoXS,
                            color = AccentGreen,
                            modifier = Modifier
                                .clip(SubTrackShapes.s)
                                .background(AccentGreen.copy(alpha = 0.12f))
                                .clickable {
                                    val msg = WhatsAppHelper.buildMemberQueryMessage(
                                        adminName = view.adminInfo.name,
                                        serviceName = view.serviceName
                                    )
                                    WhatsAppHelper.openWhatsApp(context, view.adminInfo.phone, msg)
                                }
                                .padding(horizontal = Spacing.s, vertical = Spacing.xs)
                        )
                    }
                }
            }
            Spacer(Modifier.height(Spacing.m))
        }

        // Stats mini card
        item {
            val onTimeCount = recentPayments.count { it.status == PaymentStatus.PAID }
            val joinedFormatted = remember(view.myJoinedAt) {
                SimpleDateFormat("MMM yyyy", Locale.forLanguageTag("es")).format(Date(view.myJoinedAt))
            }
            SectionCard(modifier = Modifier.padding(horizontal = Spacing.base)) {
                Eyebrow(text = stringResource(R.string.member_detail_stats_section))
                Spacer(Modifier.height(Spacing.s))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        stringResource(R.string.member_detail_member_since, joinedFormatted),
                        style = SubTrackType.bodyXS,
                        color = TextSecondary
                    )
                    Text(
                        stringResource(R.string.member_detail_on_time_payments, onTimeCount),
                        style = SubTrackType.monoXS,
                        color = AccentGreen
                    )
                }
            }
            Spacer(Modifier.height(Spacing.m))
        }

        // Payment history
        if (recentPayments.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = Spacing.base)) {
                    Eyebrow(text = stringResource(R.string.member_detail_history_section))
                    Spacer(Modifier.height(Spacing.s))
                }
            }
            items(recentPayments) { payment ->
                PaymentHistoryRow(payment = payment, modifier = Modifier.padding(horizontal = Spacing.base))
                HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(horizontal = Spacing.base))
            }
            item { Spacer(Modifier.height(Spacing.m)) }
        }

        // Plan info
        item {
            SectionCard(modifier = Modifier.padding(horizontal = Spacing.base)) {
                Eyebrow(text = stringResource(R.string.member_detail_plan_section))
                Spacer(Modifier.height(Spacing.s))
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    PlanInfoRow(label = stringResource(R.string.member_detail_plan_cutoff, view.cutoffDay))
                    PlanInfoRow(label = stringResource(R.string.member_detail_plan_cycle,
                        when (view.cycle) {
                            BillingCycle.MONTHLY -> "Mensual"
                            BillingCycle.YEARLY -> "Anual"
                            else -> view.cycle.name
                        }
                    ))
                    PlanInfoRow(label = stringResource(R.string.member_detail_plan_currency, view.currency))
                }
            }
            Spacer(Modifier.height(Spacing.m))
        }

        // Exit action
        item {
            if (view.pendingExitRequest == null) {
                Text(
                    stringResource(R.string.member_detail_exit_action),
                    style = SubTrackType.bodyS,
                    color = AccentRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onRequestExit)
                        .padding(horizontal = Spacing.base, vertical = Spacing.m)
                )
            }
            Spacer(Modifier.height(Spacing.xl))
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
private fun PlanInfoRow(label: String) {
    Text(label, style = SubTrackType.bodyXS, color = TextSecondary)
}

@Composable
private fun PaymentHistoryRow(payment: Payment, modifier: Modifier = Modifier) {
    val dateStr = payment.paidAt?.let {
        SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es")).format(Date(it))
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.s),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(payment.monthKey, style = SubTrackType.monoXS, color = TextSecondary)
            if (dateStr != null) {
                Text(
                    stringResource(R.string.member_history_paid_on, dateStr),
                    style = SubTrackType.bodyXS,
                    color = TextTertiary
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            Text("S/ %.2f".format(payment.amount), style = SubTrackType.monoS, color = TextPrimary)
            StatusPill(status = payment.status)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarkAsPaidSheetContent(
    shareAmount: Double,
    adminName: String,
    onConfirm: (String?) -> Unit,
    onCancel: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.base)
            .padding(bottom = Spacing.xl)
    ) {
        Text(stringResource(R.string.member_action_mark_paid_title), style = SubTrackType.headlineL, color = TextPrimary)
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.member_action_mark_paid_desc, "%.2f".format(shareAmount), adminName),
            style = SubTrackType.bodyS,
            color = TextSecondary
        )
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(text = stringResource(R.string.member_action_mark_paid_note_label))
        Spacer(Modifier.height(Spacing.xs))
        BasicTextField(
            value = note,
            onValueChange = { note = it },
            textStyle = SubTrackType.bodyS.copy(color = TextPrimary),
            cursorBrush = SolidColor(AccentGreen),
            singleLine = true,
            decorationBox = { inner ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SubTrackShapes.m)
                        .background(BgSurfaceEl)
                        .border(1.dp, BorderDefault, SubTrackShapes.m)
                        .padding(Spacing.m)
                ) {
                    if (note.isEmpty()) {
                        Text(stringResource(R.string.member_action_mark_paid_note_placeholder),
                            style = SubTrackType.bodyS, color = TextTertiary)
                    }
                    inner()
                }
            }
        )
        Spacer(Modifier.height(Spacing.m))
        PrimaryButton(
            text = stringResource(R.string.member_action_mark_paid_confirm),
            onClick = { onConfirm(note.ifBlank { null }) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RequestExitSheetContent(
    serviceName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.base)
            .padding(bottom = Spacing.xl)
    ) {
        Text(stringResource(R.string.member_action_exit_title), style = SubTrackType.headlineL, color = TextPrimary)
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.member_action_exit_desc, serviceName),
            style = SubTrackType.bodyS,
            color = TextSecondary
        )
        Spacer(Modifier.height(Spacing.m))
        PrimaryButton(
            text = stringResource(R.string.member_action_exit_confirm),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(R.string.common_cancel),
            style = SubTrackType.bodyS,
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCancel)
                .padding(vertical = Spacing.s),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050506)
@Composable
private fun MemberDetailPreview() {
    SubTrackTheme {
        MemberDetailContent(
            view = MemberSubscriptionView(
                subscriptionId = "sub_disney",
                serviceName = "Disney+",
                brandColor = "#113CCF",
                cycle = BillingCycle.MONTHLY,
                cutoffDay = 5,
                currency = "PEN",
                myMemberId = "mem_gonzalo_disney",
                myShareAmount = 12.25,
                myCurrentStatus = PaymentStatus.PENDING,
                myJoinedAt = 1759305600000L,
                adminInfo = AdminPublicInfo("María Rodríguez", "+51 912 345 678")
            ),
            recentPayments = emptyList(),
            onBack = {},
            onMarkAsPaid = {},
            onRequestExit = {}
        )
    }
}
