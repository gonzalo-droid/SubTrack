package com.gondroid.subtrack.feature.profile.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.gondroid.subtrack.AppStateViewModelFactory
import com.gondroid.subtrack.AppStateViewModel
import com.gondroid.subtrack.core.designsystem.components.buttons.SecondaryButton
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.data.mock.MockData

@Composable
fun DebugScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appStateVm: AppStateViewModel = viewModel(
        factory = AppStateViewModelFactory(context.applicationContext)
    )

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        item {
            Eyebrow(text = "route: Debug")
            Spacer(Modifier.height(Spacing.xs))
            Text("Mock Data Debug", style = SubTrackType.headlineL, color = TextPrimary)
            Spacer(Modifier.height(Spacing.base))
            SecondaryButton(
                text = "🔄 Reset onboarding",
                onClick = {
                    appStateVm.resetOnboarding()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.s))
        }

        // Usuario
        item {
            Text("USUARIO", style = SubTrackType.monoS, color = TextTertiary)
            Spacer(Modifier.height(4.dp))
            val u = MockData.currentUser
            Text(
                "${u.id} · ${u.name} · ${u.email} · pro=${u.isPro} · ref=${u.referralCode}",
                style = SubTrackType.mono,
                color = TextSecondary
            )
            Spacer(Modifier.height(Spacing.s))
            HorizontalDivider(thickness = 1.dp)
            Spacer(Modifier.height(Spacing.s))
        }

        // Suscripciones
        item {
            Text("SUSCRIPCIONES (${MockData.subscriptions.size})", style = SubTrackType.monoS, color = TextTertiary)
            Spacer(Modifier.height(4.dp))
        }
        items(MockData.subscriptions) { sub ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${sub.id} · ${sub.name} · S/${sub.totalAmount} · cutoff=${sub.cutoffDay} · shared=${sub.isShared}",
                    style = SubTrackType.mono,
                    color = TextPrimary
                )
                Text(
                    "  activos=${sub.members.size} archivados=${sub.archivedMembers.size}",
                    style = SubTrackType.mono,
                    color = TextSecondary
                )
                sub.members.forEach { m ->
                    Text(
                        "    ↳ ${m.id} ${m.name} · ${m.currentStatus} · S/${m.shareAmount}",
                        style = SubTrackType.mono,
                        color = TextSecondary
                    )
                }
                sub.archivedMembers.forEach { m ->
                    Text(
                        "    ✗ ${m.id} ${m.name} · ${m.currentStatus} [archivado]",
                        style = SubTrackType.mono,
                        color = TextTertiary
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xs))
        }

        item {
            Spacer(Modifier.height(Spacing.s))
            HorizontalDivider(thickness = 1.dp)
            Spacer(Modifier.height(Spacing.s))
            Text("PAGOS (${MockData.payments.size})", style = SubTrackType.monoS, color = TextTertiary)
            Spacer(Modifier.height(4.dp))
        }
        items(MockData.payments) { pay ->
            Text(
                "${pay.id} · ${pay.monthKey} · ${pay.status} · paidAt=${pay.paidAt != null}",
                style = SubTrackType.mono,
                color = TextSecondary
            )
        }

        item {
            Spacer(Modifier.height(Spacing.s))
            HorizontalDivider(thickness = 1.dp)
            Spacer(Modifier.height(Spacing.s))
            Text("PLANTILLAS (${MockData.templates.size})", style = SubTrackType.monoS, color = TextTertiary)
            Spacer(Modifier.height(4.dp))
        }
        items(MockData.templates) { tpl ->
            Text(
                "${tpl.id} · ${tpl.emoji} ${tpl.name} · ${tpl.tone}",
                style = SubTrackType.mono,
                color = TextSecondary
            )
        }

        item {
            Spacer(Modifier.height(Spacing.l))
            SecondaryButton(
                text = "← Volver",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun DebugScreenPreview() {
    SubTrackTheme { DebugScreen(onBack = {}) }
}
