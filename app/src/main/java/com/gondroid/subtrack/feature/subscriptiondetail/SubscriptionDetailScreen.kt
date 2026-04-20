package com.gondroid.subtrack.feature.subscriptiondetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.feature.subscriptiondetail.admin.AdminDetailScreen
import com.gondroid.subtrack.feature.subscriptiondetail.member.MemberDetailScreen

@Composable
fun SubscriptionDetailScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: SubscriptionDetailRouterViewModel = viewModel(
        factory = SubscriptionDetailRouterViewModelFactory(subscriptionId)
    )
    val roleState by vm.roleState.collectAsStateWithLifecycle()

    when (val state = roleState) {
        is RoleState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando…", style = SubTrackType.bodyM, color = TextSecondary)
        }
        is RoleState.Admin -> AdminDetailScreen(
            subscriptionId = state.subscriptionId,
            onBack = onBack,
            modifier = modifier
        )
        is RoleState.MemberOf -> MemberDetailScreen(
            subscriptionId = state.subscriptionId,
            memberId = state.memberId,
            onBack = onBack,
            modifier = modifier
        )
        is RoleState.NotFound -> {
            onBack()
        }
    }
}
