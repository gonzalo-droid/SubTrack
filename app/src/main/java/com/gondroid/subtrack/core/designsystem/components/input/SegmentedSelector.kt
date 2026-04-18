package com.gondroid.subtrack.core.designsystem.components.input

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

private val IndicatorShape = RoundedCornerShape(10.dp)

@Composable
fun <T> SegmentedSelector(
    options: List<T>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier,
    countProvider: ((T) -> Int)? = null
) {
    if (options.isEmpty()) return

    BoxWithConstraints(
        modifier = modifier
            .clip(SubTrackShapes.l)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .background(BgSurface)
            .padding(4.dp)
    ) {
        val itemWidth = maxWidth / options.size
        val indicatorOffset by animateDpAsState(
            targetValue = itemWidth * selectedIndex,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "segmented_offset"
        )

        // Sliding indicator — rendered behind options
        Box(
            modifier = Modifier
                .width(itemWidth)
                .height(36.dp)
                .offset(x = indicatorOffset)
                .clip(IndicatorShape)
                .border(1.dp, BorderDefault, IndicatorShape)
                .background(BgSurfaceEl)
        )

        // Option labels — rendered on top of indicator
        Row(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                val isActive = index == selectedIndex
                val count = countProvider?.invoke(option)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelectionChange(index) }
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = labelProvider(option),
                            style = SubTrackType.monoS,
                            color = if (isActive) TextPrimary else TextTertiary
                        )
                        if (count != null) {
                            Spacer(Modifier.width(4.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(SubTrackShapes.circle)
                                    .background(
                                        if (isActive) AccentGreen
                                        else Color.White.copy(alpha = 0.06f)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$count",
                                    style = SubTrackType.monoXS,
                                    color = if (isActive) Color.Black else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SegmentedSelectorBasicPreview() {
    SubTrackTheme {
        var selected by remember { mutableIntStateOf(0) }
        Column(modifier = Modifier.padding(Spacing.base)) {
            SegmentedSelector(
                options = listOf("Mensual", "Anual"),
                selectedIndex = selected,
                onSelectionChange = { selected = it },
                labelProvider = { it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.base))
            SegmentedSelector(
                options = listOf("Personal", "Compartida"),
                selectedIndex = 1,
                onSelectionChange = {},
                labelProvider = { it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SegmentedSelectorWithCountPreview() {
    SubTrackTheme {
        data class Tab(val label: String, val count: Int)
        var selected by remember { mutableIntStateOf(0) }
        val tabs = listOf(Tab("Todas", 8), Tab("Activas", 5), Tab("Vencidas", 2))
        Column(modifier = Modifier.padding(Spacing.base)) {
            SegmentedSelector(
                options = tabs,
                selectedIndex = selected,
                onSelectionChange = { selected = it },
                labelProvider = { it.label },
                countProvider = { it.count },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
