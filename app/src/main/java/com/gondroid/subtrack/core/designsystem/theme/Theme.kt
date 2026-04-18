package com.gondroid.subtrack.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SubTrackColorScheme = darkColorScheme(
    primary = TextPrimary,
    onPrimary = Color.Black,
    primaryContainer = BgSurfaceEl,
    onPrimaryContainer = TextPrimary,
    secondary = AccentGreen,
    onSecondary = Color.Black,
    secondaryContainer = AccentGreenBg,
    onSecondaryContainer = AccentGreen,
    tertiary = AccentBlue,
    onTertiary = Color.Black,
    tertiaryContainer = AccentBlueBg,
    onTertiaryContainer = AccentBlue,
    error = AccentRed,
    onError = Color.Black,
    errorContainer = AccentRedBg,
    onErrorContainer = AccentRed,
    background = BgApp,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgSurfaceHi,
    onSurfaceVariant = TextSecondary,
    inverseSurface = TextPrimary,
    inverseOnSurface = BgApp,
    inversePrimary = BgSurface,
    outline = BorderDefault,
    outlineVariant = BorderStrong,
    scrim = Color(0xCC000000),
    surfaceContainerLowest = BgPage,
    surfaceContainerLow = BgApp,
    surfaceContainer = BgSurface,
    surfaceContainerHigh = BgSurfaceHi,
    surfaceContainerHighest = BgSurfaceEl,
)

@Composable
fun SubTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SubTrackColorScheme,
        typography = SubTrackTypography,
        content = content
    )
}
