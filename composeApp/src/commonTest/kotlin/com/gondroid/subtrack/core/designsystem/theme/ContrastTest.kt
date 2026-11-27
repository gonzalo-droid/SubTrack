package com.gondroid.subtrack.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * COD-02 / TST-06: contrast is tested, not eyeballed. WCAG AA: 4.5:1 for text, 3:1 for icons and
 * other non-text elements. SubTrack is dark-only, so every background is a dark surface.
 */
class ContrastTest {

    private val backgrounds = mapOf(
        "BgPage" to BgPage, "BgApp" to BgApp, "BgSurface" to BgSurface,
        "BgSurfaceHi" to BgSurfaceHi, "BgSurfaceEl" to BgSurfaceEl, "BgSheet" to BgSheet
    )

    private fun ratio(foreground: Color, background: Color): Double {
        val fg = foreground.compositeOver(background).luminance() + 0.05
        val bg = background.luminance() + 0.05
        return (max(fg, bg) / min(fg, bg)).toDouble()
    }

    private fun assertContrast(name: String, color: Color, minimum: Double, on: Map<String, Color> = backgrounds) {
        on.forEach { (bgName, bg) ->
            val r = ratio(color, bg)
            assertTrue(r >= minimum, "$name on $bgName is ${(r * 100).toInt() / 100.0}:1, needs $minimum:1")
        }
    }

    @Test fun textColorsReadOnEveryBackground() {
        assertContrast("TextPrimary", TextPrimary, 4.5)
        assertContrast("TextSecondary", TextSecondary, 4.5)
        assertContrast("TextTertiary", TextTertiary, 4.5)
    }

    @Test fun accentsUsedAsTextReadOnEveryBackground() {
        mapOf(
            "AccentGreen" to AccentGreen, "AccentAmber" to AccentAmber, "AccentRed" to AccentRed,
            "AccentBlue" to AccentBlue, "AccentPurple" to AccentPurple, "AccentPink" to AccentPink, "AccentCyan" to AccentCyan
        ).forEach { (name, color) -> assertContrast(name, color, 4.5) }
    }

    @Test fun accentsReadOnTheirOwnTintedBackground() {
        // Pills and icon badges: the accent over its 12 % tint, on every surface
        listOf(
            Triple("AccentGreen", AccentGreen, AccentGreenBg), Triple("AccentAmber", AccentAmber, AccentAmberBg),
            Triple("AccentRed", AccentRed, AccentRedBg), Triple("AccentBlue", AccentBlue, AccentBlueBg),
            Triple("AccentPurple", AccentPurple, AccentPurpleBg)
        ).forEach { (name, accent, tint) ->
            backgrounds.forEach { (surfaceName, surface) ->
                assertContrast("$name on its tint", accent, 4.5, mapOf("tint over $surfaceName" to tint.compositeOver(surface)))
            }
        }
    }

    @Test fun strongBorderIsVisibleAsAnElement() {
        // Borders are decorative separators; the strong one marks selected cards and must reach 1.3:1 at least
        assertTrue(ratio(BorderStrong, BgSurface) >= 1.3)
    }
}
