package com.gondroid.subtrack.feature.subscriptiondetail

import com.gondroid.subtrack.resources.common_loading

import com.gondroid.subtrack.resources.Res

import org.jetbrains.compose.resources.stringResource

import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.feature.subscriptiondetail.admin.AdminDetailScreen
import com.gondroid.subtrack.feature.subscriptiondetail.member.MemberDetailScreen
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SubscriptionDetailScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onMemberHistory: (memberId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<SubscriptionDetailRouterViewModel>(
        key = subscriptionId,
        parameters = { parametersOf(subscriptionId) }
    )
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SubscriptionDetailUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(Res.string.common_loading), style = SubTrackType.bodyM, color = TextSecondary)
        }
        is SubscriptionDetailUiState.Admin -> AdminDetailScreen(
            subscriptionId = state.subscriptionId,
            onBack = onBack,
            onEdit = onEdit,
            onMemberHistory = onMemberHistory,
            modifier = modifier
        )
        is SubscriptionDetailUiState.MemberOf -> MemberDetailScreen(
            subscriptionId = state.subscriptionId,
            memberId = state.memberId,
            onBack = onBack,
            modifier = modifier
        )
        // Also reached right after the admin deletes the subscription
        is SubscriptionDetailUiState.NotFound -> LaunchedEffect(Unit) { onBack() }
    }
}
