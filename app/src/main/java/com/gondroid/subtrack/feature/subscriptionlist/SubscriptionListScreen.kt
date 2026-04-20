package com.gondroid.subtrack.feature.subscriptionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.SecondaryButton
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary

@Composable
fun SubscriptionListScreen(
    onNavigateToDetail: (String) -> Unit,
    onCreateSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
            modifier = Modifier.padding(Spacing.xl)
        ) {
            Eyebrow(text = "route: SubscriptionList")
            Spacer(Modifier.height(Spacing.xs))
            Text("Mis suscripciones", style = SubTrackType.headlineL, color = TextPrimary)
            Spacer(Modifier.height(Spacing.base))
            PrimaryButton(
                text = "Ver detalle (sub_2)",
                onClick = { onNavigateToDetail("sub_2") },
                modifier = Modifier.fillMaxWidth()
            )
            SecondaryButton(
                text = "Crear suscripción",
                onClick = onCreateSubscription,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SubscriptionListScreenPreview() {
    SubTrackTheme { SubscriptionListScreen(onNavigateToDetail = {}, onCreateSubscription = {}) }
}
