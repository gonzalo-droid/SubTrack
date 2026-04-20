package com.gondroid.subtrack.feature.onboarding.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.indicators.ProgressDots
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

@Composable
fun ValueSlideLayout(
    progressCurrent: Int,
    progressTotal: Int = 3,
    eyebrow: String,
    eyebrowColor: Color,
    title: String,
    description: AnnotatedString,
    visualContent: @Composable BoxScope.() -> Unit,
    primaryCta: String,
    primaryCtaAccent: Color? = null,
    onPrimaryClick: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = Spacing.base)
    ) {
        // Top bar: progress dots + skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.m, bottom = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressDots(totalSteps = progressTotal, currentStep = progressCurrent)
            Text(
                stringResource(R.string.onboarding_skip),
                style = SubTrackType.bodyS,
                color = TextSecondary,
                modifier = Modifier
                    .clickable(onClick = onSkip)
                    .padding(start = Spacing.m, top = Spacing.xs, bottom = Spacing.xs)
            )
        }

        // Visual hero area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
            content = visualContent
        )

        // Text content
        Column(modifier = Modifier.padding(bottom = Spacing.s)) {
            Text(eyebrow.uppercase(), style = SubTrackType.monoXS, color = eyebrowColor)
            Spacer(Modifier.height(Spacing.s))
            Text(title, style = SubTrackType.displayS, color = TextPrimary)
            Spacer(Modifier.height(Spacing.s))
            Text(description, style = SubTrackType.bodyM, color = TextSecondary)
        }

        // Footer CTA
        Column(modifier = Modifier.padding(bottom = Spacing.xl)) {
            PrimaryButton(
                text = primaryCta,
                onClick = onPrimaryClick,
                accent = primaryCtaAccent,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
