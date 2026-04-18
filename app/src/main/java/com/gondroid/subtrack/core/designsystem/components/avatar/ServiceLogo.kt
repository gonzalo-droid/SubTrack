package com.gondroid.subtrack.core.designsystem.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.core.designsystem.theme.SpaceGrotesk
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme

// TODO: [fase-2] agregar soporte de logoUrl via Coil cuando brandColor sea insuficiente

enum class LogoSize(val dp: Dp) {
    Small(24.dp), Medium(36.dp), Large(48.dp), XLarge(60.dp)
}

@Composable
fun ServiceLogo(
    serviceName: String,
    brandColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = LogoSize.Medium.dp
) {
    val cornerRadius = (size.value * 0.29f).dp
    val shape = RoundedCornerShape(cornerRadius)
    val initial = serviceName.trim().firstOrNull()?.uppercase() ?: "?"
    val fontSize = (size.value * 0.42f).sp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(brandColor.copy(alpha = 0.18f))
    ) {
        Text(
            text = initial,
            style = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                lineHeight = fontSize,
                color = brandColor
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ServiceLogoPreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Large.dp)
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Spotify", brandColor = Color(0xFF1DB954), size = LogoSize.Large.dp)
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "iCloud+", brandColor = Color(0xFF147EFB), size = LogoSize.Large.dp)
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "ChatGPT Plus", brandColor = Color(0xFF10A37F), size = LogoSize.Large.dp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ServiceLogoSizesPreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Small.dp)
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Medium.dp)
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Large.dp)
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.XLarge.dp)
        }
    }
}
