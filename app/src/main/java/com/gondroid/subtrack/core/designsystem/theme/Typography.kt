package com.gondroid.subtrack.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.R

val googleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val SpaceGrotesk = FontFamily(
    Font(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.Normal),
    Font(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.Medium),
    Font(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.SemiBold),
    Font(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.Bold)
)

val Geist = FontFamily(
    Font(GoogleFont("Geist"), googleFontsProvider, FontWeight.Normal),
    Font(GoogleFont("Geist"), googleFontsProvider, FontWeight.Medium),
    Font(GoogleFont("Geist"), googleFontsProvider, FontWeight.SemiBold),
    Font(GoogleFont("Geist"), googleFontsProvider, FontWeight.Bold)
)

val JetBrainsMono = FontFamily(
    Font(GoogleFont("JetBrains Mono"), googleFontsProvider, FontWeight.Normal),
    Font(GoogleFont("JetBrains Mono"), googleFontsProvider, FontWeight.Medium),
    Font(GoogleFont("JetBrains Mono"), googleFontsProvider, FontWeight.SemiBold)
)

// Custom type scale — use SubTrackType for components, Material3 slots for compatibility
object SubTrackType {
    val displayXL = TextStyle(fontFamily = SpaceGrotesk, fontSize = 56.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.04).em)
    val displayL = TextStyle(fontFamily = SpaceGrotesk, fontSize = 42.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.04).em)
    val displayM = TextStyle(fontFamily = SpaceGrotesk, fontSize = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.03).em)
    val displayS = TextStyle(fontFamily = SpaceGrotesk, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.03).em)
    val headlineL = TextStyle(fontFamily = SpaceGrotesk, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
    val headlineM = TextStyle(fontFamily = SpaceGrotesk, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
    val headlineS = TextStyle(fontFamily = SpaceGrotesk, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
    val titleL = TextStyle(fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.01).em)
    val titleM = TextStyle(fontFamily = Geist, fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.01).em)
    val bodyL = TextStyle(fontFamily = Geist, fontSize = 14.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.01).em)
    val bodyM = TextStyle(fontFamily = Geist, fontSize = 13.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.01).em)
    val bodyS = TextStyle(fontFamily = Geist, fontSize = 12.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.01).em)
    val bodyXS = TextStyle(fontFamily = Geist, fontSize = 11.sp, fontWeight = FontWeight.Normal)
    val mono = TextStyle(fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.02.em)
    val monoS = TextStyle(fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.15.em)
    val monoXS = TextStyle(fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.18.em)
}

val SubTrackTypography = Typography(
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
