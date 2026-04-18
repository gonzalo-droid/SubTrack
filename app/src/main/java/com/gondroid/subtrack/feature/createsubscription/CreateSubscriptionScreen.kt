package com.gondroid.subtrack.feature.createsubscription

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
fun CreateSubscriptionScreen(onBack: () -> Unit, onCreated: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Crear suscripción", color = TextPrimary)
    }
}

@Preview
@Composable
private fun CreateSubscriptionScreenPreview() {
    SubTrackTheme { CreateSubscriptionScreen(onBack = {}, onCreated = {}) }
}
