package com.gondroid.subtrack.feature.subscriptiondetail

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
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SubscriptionDetailScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onMemberHistory: (memberId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm = hiltViewModel<SubscriptionDetailRouterViewModel, SubscriptionDetailRouterViewModel.Factory>(
        key = subscriptionId,
        creationCallback = { it.create(subscriptionId) }
    )
    val roleState by vm.roleState.collectAsStateWithLifecycle()

    when (val state = roleState) {
        is RoleState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando…", style = SubTrackType.bodyM, color = TextSecondary)
        }
        is RoleState.Admin -> AdminDetailScreen(
            subscriptionId = state.subscriptionId,
            onBack = onBack,
            onEdit = onEdit,
            onMemberHistory = onMemberHistory,
            modifier = modifier
        )
        is RoleState.MemberOf -> MemberDetailScreen(
            subscriptionId = state.subscriptionId,
            memberId = state.memberId,
            onBack = onBack,
            modifier = modifier
        )
        // Also reached right after the admin deletes the subscription
        is RoleState.NotFound -> LaunchedEffect(Unit) { onBack() }
    }
}
