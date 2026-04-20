package com.gondroid.subtrack.feature.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.indicators.ProgressDots
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BgPage
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
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.data.mock.ServiceTemplates
import com.gondroid.subtrack.domain.model.ServiceTemplate

@Composable
fun FirstSubscriptionScreen(
    popularServices: List<ServiceTemplate>,
    selectedService: ServiceTemplate?,
    isCreating: Boolean,
    onSelectService: (ServiceTemplate) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
            .safeDrawingPadding()
            .padding(horizontal = Spacing.base)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressDots(totalSteps = 2, currentStep = 1)
            Text(
                stringResource(R.string.onboarding_skip),
                style = SubTrackType.bodyS,
                color = TextSecondary,
                modifier = Modifier
                    .clickable(onClick = onSkip)
                    .padding(start = Spacing.m, top = Spacing.xs, bottom = Spacing.xs)
            )
        }

        Spacer(Modifier.height(Spacing.m))

        // Header
        Text(
            stringResource(R.string.onboarding_first_sub_title_line1),
            style = SubTrackType.displayS,
            color = TextPrimary
        )
        Row {
            Text(
                stringResource(R.string.onboarding_first_sub_title_accent),
                style = SubTrackType.displayS,
                color = AccentGreen
            )
            Text(
                stringResource(R.string.onboarding_first_sub_title_line2),
                style = SubTrackType.displayS,
                color = TextPrimary
            )
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.onboarding_first_sub_subtitle),
            style = SubTrackType.bodyS,
            color = TextSecondary
        )

        Spacer(Modifier.height(Spacing.m))

        // Service grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
            modifier = Modifier.weight(1f)
        ) {
            items(popularServices) { service ->
                ServiceTile(
                    service = service,
                    isSelected = service.id == selectedService?.id,
                    onClick = { onSelectService(service) }
                )
            }
            // "Other" tile
            item {
                OtherTile(onClick = { /* TODO: custom name input */ })
            }
        }

        // Hint
        Text(
            stringResource(R.string.onboarding_first_sub_hint),
            style = SubTrackType.monoXS,
            color = TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.s)
        )

        // CTA
        if (isCreating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentGreen, modifier = Modifier.size(32.dp))
            }
        } else {
            PrimaryButton(
                text = if (selectedService != null)
                    stringResource(R.string.onboarding_first_sub_cta_selected, selectedService.name)
                else
                    stringResource(R.string.onboarding_first_sub_cta_empty),
                onClick = onContinue,
                enabled = selectedService != null,
                accent = if (selectedService != null) AccentGreen else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.xl)
            )
        }
    }
}

@Composable
private fun ServiceTile(
    service: ServiceTemplate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(SubTrackShapes.l)
            .background(if (isSelected) AccentGreenBg else BgSurface)
            .border(
                1.dp,
                if (isSelected) AccentGreen.copy(alpha = 0.4f) else BorderDefault,
                SubTrackShapes.l
            )
            .clickable(onClick = onClick)
            .padding(Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ServiceLogo(
            serviceName = service.name,
            brandColor = service.brandColor.toComposeColor(),
            size = 34.dp
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            service.name,
            style = SubTrackType.monoXS,
            color = if (isSelected) AccentGreen else TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun OtherTile(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(SubTrackShapes.l)
            .background(BgSurfaceEl)
            .border(1.dp, Color.White.copy(alpha = 0.2f), SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("+", style = SubTrackType.displayS, color = TextSecondary)
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.onboarding_first_sub_other),
            style = SubTrackType.monoXS,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050506)
@Composable
private fun FirstSubscriptionPreview() {
    SubTrackTheme {
        FirstSubscriptionScreen(
            popularServices = ServiceTemplates.popular,
            selectedService = ServiceTemplates.popular.firstOrNull(),
            isCreating = false,
            onSelectService = {},
            onContinue = {},
            onSkip = {}
        )
    }
}
