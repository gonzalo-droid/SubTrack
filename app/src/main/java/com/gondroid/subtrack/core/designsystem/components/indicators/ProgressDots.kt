package com.gondroid.subtrack.core.designsystem.components.indicators

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary

private val DotShape = RoundedCornerShape(2.dp)
private val DotHeight = 3.dp
private val DotWidthInactive = 22.dp
private val DotWidthActive = 32.dp

@Composable
fun ProgressDots(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val isDone = index < currentStep

            val dotWidth by animateDpAsState(
                targetValue = if (isActive) DotWidthActive else DotWidthInactive,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "dot_width_$index"
            )
            val dotColor = when {
                isActive -> TextPrimary
                isDone -> AccentGreen
                else -> Color.White.copy(alpha = 0.1f)
            }

            Box(
                modifier = Modifier
                    .width(dotWidth)
                    .height(DotHeight)
                    .then(
                        if (isActive) Modifier.shadow(
                            elevation = 4.dp,
                            shape = DotShape,
                            spotColor = Color.White.copy(alpha = 0.3f),
                            ambientColor = Color.White.copy(alpha = 0.15f)
                        ) else Modifier
                    )
                    .clip(DotShape)
                    .background(dotColor)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ProgressDotsPreview() {
    SubTrackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.base),
            modifier = Modifier.padding(Spacing.base)
        ) {
            ProgressDots(totalSteps = 5, currentStep = 0)
            ProgressDots(totalSteps = 5, currentStep = 2)
            ProgressDots(totalSteps = 5, currentStep = 4)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ProgressDotsCompletedPreview() {
    SubTrackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.base),
            modifier = Modifier.padding(Spacing.base)
        ) {
            ProgressDots(totalSteps = 4, currentStep = 0)
            ProgressDots(totalSteps = 4, currentStep = 1)
            ProgressDots(totalSteps = 4, currentStep = 2)
            ProgressDots(totalSteps = 4, currentStep = 3)
        }
    }
}
