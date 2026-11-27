package com.gondroid.subtrack.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.geist
import com.gondroid.subtrack.resources.jetbrains_mono
import com.gondroid.subtrack.resources.space_grotesk
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

// Bundled variable fonts (OFL, licenses in docs/licenses): the same on Android and iOS, also offline.
// Compose Multiplatform loads resource fonts from composition, so the families and styles are getters.

@Composable
private fun variableFamily(resource: FontResource, vararg weights: FontWeight) = FontFamily(
    weights.map { weight ->
        Font(resource, weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))
    }
)

val SpaceGrotesk: FontFamily
    @Composable get() = variableFamily(Res.font.space_grotesk, FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold)

val Geist: FontFamily
    @Composable get() = variableFamily(Res.font.geist, FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold)

val JetBrainsMono: FontFamily
    @Composable get() = variableFamily(Res.font.jetbrains_mono, FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold)

// Custom type scale — use SubTrackType for components, Material3 slots for compatibility
object SubTrackType {
    val displayXL: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 56.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.04).em)
    val displayL: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 42.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.04).em)
    val displayM: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.03).em)
    val displayS: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.03).em)
    val headlineL: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
    val headlineM: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
    val headlineS: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
    val titleL: TextStyle @Composable get() = TextStyle(fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.01).em)
    val titleM: TextStyle @Composable get() = TextStyle(fontFamily = Geist, fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.01).em)
    val bodyL: TextStyle @Composable get() = TextStyle(fontFamily = Geist, fontSize = 14.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.01).em)
    val bodyM: TextStyle @Composable get() = TextStyle(fontFamily = Geist, fontSize = 13.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.01).em)
    val bodyS: TextStyle @Composable get() = TextStyle(fontFamily = Geist, fontSize = 12.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.01).em)
    val bodyXS: TextStyle @Composable get() = TextStyle(fontFamily = Geist, fontSize = 11.sp, fontWeight = FontWeight.Normal)
    val mono: TextStyle @Composable get() = TextStyle(fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.02.em)
    val monoS: TextStyle @Composable get() = TextStyle(fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.15.em)
    val monoXS: TextStyle @Composable get() = TextStyle(fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.18.em)
}

val SubTrackTypography: Typography
    @Composable get() = Typography(
    displayLarge = SubTrackType.displayL,
    displayMedium = SubTrackType.displayM,
    displaySmall = SubTrackType.displayS,
    headlineLarge = SubTrackType.headlineL,
    headlineMedium = SubTrackType.headlineM,
    headlineSmall = SubTrackType.headlineS,
    titleLarge = SubTrackType.titleL,
    titleMedium = SubTrackType.titleM,
    titleSmall = SubTrackType.bodyM,
    bodyLarge = SubTrackType.bodyL,
    bodyMedium = SubTrackType.bodyM,
    bodySmall = SubTrackType.bodyS,
    labelLarge = SubTrackType.bodyXS,
    labelMedium = SubTrackType.mono,
    labelSmall = SubTrackType.monoS,
)
