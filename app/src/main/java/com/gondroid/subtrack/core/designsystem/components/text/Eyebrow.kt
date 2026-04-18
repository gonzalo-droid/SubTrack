package com.gondroid.subtrack.core.designsystem.components.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

@Composable
fun Eyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextTertiary
) {
    Text(
        text = text.uppercase(),
        style = SubTrackType.monoXS,
        color = color,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun EyebrowPreview() {
    SubTrackTheme {
        Eyebrow(text = "Suscripciones activas")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun EyebrowAccentPreview() {
    SubTrackTheme {
        Eyebrow(
            text = "Pro exclusivo",
            color = com.gondroid.subtrack.core.designsystem.theme.AccentAmber
        )
    }
}
