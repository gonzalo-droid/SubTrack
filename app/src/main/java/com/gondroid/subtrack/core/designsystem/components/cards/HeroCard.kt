package com.gondroid.subtrack.core.designsystem.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

@Composable
fun HeroCard(
    accentColor: Color,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val linearGradient = Brush.linearGradient(
        colors = listOf(accentColor.copy(alpha = 0.1f), BgSurface),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Column(
        modifier = modifier
            .clip(SubTrackShapes.xxl)
            .border(1.dp, accentColor.copy(alpha = 0.2f), SubTrackShapes.xxl)
            .background(BgSurface)
            .background(linearGradient)
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width * 0.85f
                    )
                )
            }
            .padding(contentPadding),
        content = content
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun HeroCardPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            HeroCard(accentColor = AccentBlue, modifier = Modifier.fillMaxWidth()) {
                Text("GASTO MENSUAL", style = SubTrackType.monoXS, color = TextSecondary)
                Spacer(Modifier.height(Spacing.xs))
                Text("S/ 124.70", style = SubTrackType.displayM)
                Spacer(Modifier.height(Spacing.xs))
                Text("4 suscripciones activas", style = SubTrackType.bodyS, color = TextSecondary)
            }
            Spacer(Modifier.height(Spacing.s))
            HeroCard(accentColor = AccentGreen, modifier = Modifier.fillMaxWidth()) {
                Text("LO QUE COBRAS", style = SubTrackType.monoXS, color = TextSecondary)
                Spacer(Modifier.height(Spacing.xs))
                Text("S/ 49.89", style = SubTrackType.displayM)
                Spacer(Modifier.height(Spacing.xs))
                Text("5 miembros activos", style = SubTrackType.bodyS, color = TextSecondary)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun HeroCardVariantsPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            HeroCard(accentColor = AccentPurple, modifier = Modifier.fillMaxWidth()) {
                Text("PRÓXIMO COBRO", style = SubTrackType.monoXS, color = TextSecondary)
                Spacer(Modifier.height(Spacing.xs))
                Text("17 abr", style = SubTrackType.headlineL)
            }
        }
    }
}
