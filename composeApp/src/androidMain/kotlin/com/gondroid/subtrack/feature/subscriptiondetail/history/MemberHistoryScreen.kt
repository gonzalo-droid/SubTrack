package com.gondroid.subtrack.feature.subscriptiondetail.history

import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.list.SettingsGroup
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.feature.subscriptiondetail.components.PaymentHistoryRow
import androidx.compose.ui.unit.dp

@Composable
fun MemberHistoryScreen(
    subscriptionId: String,
    memberId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<MemberHistoryViewModel>(
        key = "$subscriptionId/$memberId",
        parameters = { parametersOf(subscriptionId, memberId) }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().background(BgApp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.s)
        ) {
            STIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.common_back),
                onClick = onBack
            )
            Text(
                stringResource(R.string.member_history_title),
                style = SubTrackType.headlineL,
                color = TextPrimary,
                modifier = Modifier.semantics { heading() }
            )
        }
        when (val state = uiState) {
            MemberHistoryUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentGreen)
            }
            MemberHistoryUiState.NotFound -> LaunchedEffect(Unit) { onBack() }
            is MemberHistoryUiState.Success -> MemberHistoryContent(state)
        }
    }
}

@Composable
private fun MemberHistoryContent(state: MemberHistoryUiState.Success) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = Spacing.base, vertical = Spacing.s),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                modifier = Modifier.padding(bottom = Spacing.s)
            ) {
                Avatar(name = state.memberName, size = 48.dp)
                Column {
                    Text(state.memberName, style = SubTrackType.titleL, color = TextPrimary)
                    Text(
                        text = state.punctualityPercent?.let {
                            stringResource(R.string.member_history_punctuality, it)
                        } ?: stringResource(R.string.member_history_no_decided),
                        style = SubTrackType.monoXS,
                        color = TextSecondary
                    )
                }
            }
        }
        item {
            Eyebrow(
                text = pluralStringResource(
                    R.plurals.member_history_months, state.payments.size, state.payments.size
                )
            )
        }
        if (state.payments.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.member_history_empty),
                    style = SubTrackType.bodyM,
                    color = TextTertiary,
                    modifier = Modifier.padding(vertical = Spacing.xl)
                )
            }
        } else {
            item {
                SettingsGroup {
                    state.payments.forEachIndexed { index, payment ->
                        PaymentHistoryRow(payment, Modifier.padding(horizontal = Spacing.m))
                        if (index < state.payments.lastIndex) HorizontalDivider(color = BorderDefault)
                    }
                }
            }
        }
        item {
            // Legend: what each state means (B4)
            Text(
                stringResource(R.string.member_history_legend),
                style = SubTrackType.bodyXS,
                color = TextTertiary,
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.s, bottom = Spacing.xl)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun MemberHistoryContentPreview() {
    SubTrackTheme {
        MemberHistoryContent(
            MemberHistoryUiState.Success(
                memberName = "Ana Paredes",
                payments = listOf(
                    Payment("1", "s", "m", "2026-11", 8.97, PaymentStatus.PENDING, null, null, null),
                    Payment("2", "s", "m", "2026-10", 8.97, PaymentStatus.LATE, 1_760_000_000_000, null, null),
                    Payment("3", "s", "m", "2026-09", 8.97, PaymentStatus.PAID, 1_757_000_000_000, null, null)
                ),
                punctualityPercent = 50
            )
        )
    }
}
