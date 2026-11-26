package com.gondroid.subtrack.core.designsystem.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    size: Dp = LogoSize.Medium.dp,
    serviceId: String? = null
) {
    val asset = remember(serviceId) { ServiceLogoRegistry.get(serviceId) }
    val cornerRadius = (size.value * 0.29f).dp
    val shape = RoundedCornerShape(cornerRadius)

    if (asset != null) {
        val bgColor = if (asset.useWhiteBackground) Color.White else Color(asset.brandColor)
        val iconSize = size * 0.55f
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(bgColor)
                .then(
                    if (asset.useWhiteBackground)
                        Modifier.border(1.dp, Color.Black.copy(alpha = 0.08f), shape)
                    else Modifier
                )
        ) {
            Icon(
                painter = painterResource(asset.iconRes),
                contentDescription = serviceName,
                tint = if (asset.useWhiteBackground) Color(asset.brandColor) else Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    } else {
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
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ServiceLogoPreview() {
    SubTrackTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.base)
        ) {
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Large.dp, serviceId = "tpl_netflix")
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Spotify", brandColor = Color(0xFF1DB954), size = LogoSize.Large.dp, serviceId = "tpl_spotify")
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "iCloud+", brandColor = Color(0xFF147EFB), size = LogoSize.Large.dp, serviceId = "tpl_icloud")
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Notion", brandColor = Color(0xFF000000), size = LogoSize.Large.dp, serviceId = "tpl_notion")
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Disney+", brandColor = Color(0xFF113CCF), size = LogoSize.Large.dp)
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
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Small.dp, serviceId = "tpl_netflix")
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Medium.dp, serviceId = "tpl_netflix")
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Large.dp, serviceId = "tpl_netflix")
            Spacer(Modifier.width(Spacing.s))
            ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.XLarge.dp, serviceId = "tpl_netflix")
        }
    }
}
