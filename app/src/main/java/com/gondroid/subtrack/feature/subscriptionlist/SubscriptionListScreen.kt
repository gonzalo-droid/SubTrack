package com.gondroid.subtrack.feature.subscriptionlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary

@Composable
fun SubscriptionListScreen(onNavigateToDetail: (String) -> Unit, onCreateSubscription: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Mis suscripciones", color = TextPrimary)
    }
}

@Preview
@Composable
private fun SubscriptionListScreenPreview() {
    SubTrackTheme { SubscriptionListScreen(onNavigateToDetail = {}, onCreateSubscription = {}) }
}
