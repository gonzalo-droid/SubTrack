package com.gondroid.subtrack.feature.profile.templates

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
fun TemplatesScreen(onBack: () -> Unit, onEditTemplate: (String?) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Plantillas", color = TextPrimary)
    }
}

@Preview
@Composable
private fun TemplatesScreenPreview() {
    SubTrackTheme { TemplatesScreen(onBack = {}, onEditTemplate = {}) }
}
