package com.gondroid.subtrack.feature.createsubscription.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.LogoSize
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
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

private val PRESET_COLORS = listOf("#E50914", "#1DB954", "#0A84FF", "#BF5AF2", "#FFB340", "#888888")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceStep(
    services: List<ServiceTemplate>,
    selectedService: ServiceTemplate?,
    customServiceName: String,
    onSelectService: (ServiceTemplate) -> Unit,
    onUpdateCustomName: (String) -> Unit,
    onUpdateCustomColor: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var dialogName by remember { mutableStateOf(customServiceName) }
    var dialogColor by remember { mutableStateOf("#888888") }

    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text(stringResource(R.string.create_subscription_other_title), color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
                    STTextField(
                        value = dialogName,
                        onValueChange = { dialogName = it },
                        label = stringResource(R.string.create_subscription_other_name_label),
                        placeholder = stringResource(R.string.create_subscription_other_name_placeholder),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Eyebrow(text = stringResource(R.string.create_subscription_other_color_label))
                    Spacer(Modifier.height(Spacing.xs))
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                        PRESET_COLORS.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(SubTrackShapes.circle)
                                    .background(hex.toComposeColor())
                                    .border(
                                        2.dp,
                                        if (hex == dialogColor) Color.White else Color.Transparent,
                                        SubTrackShapes.circle
                                    )
                                    .clickable { dialogColor = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSelectService(ServiceTemplates.other.copy(brandColor = dialogColor))
                    onUpdateCustomName(dialogName)
                    onUpdateCustomColor(dialogColor)
                    showCustomDialog = false
                }) {
                    Text(stringResource(R.string.common_confirm), color = AccentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text(stringResource(R.string.common_cancel), color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base)
    ) {
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(text = stringResource(R.string.create_subscription_step1_eyebrow))
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_step1_title),
            style = SubTrackType.displayS,
            color = TextPrimary
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_step1_subtitle),
            style = SubTrackType.bodyM,
            color = TextSecondary
        )
        Spacer(Modifier.height(Spacing.l))

        // 3-column grid using FlowRow
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
            maxItemsInEachRow = 3
        ) {
            services.forEach { service ->
                ServiceTile(
                    service = service,
                    isSelected = selectedService?.id == service.id,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectService(service) }
                )
            }
            // "Otro" tile
            OtherServiceTile(
                isSelected = selectedService?.id == "tpl_other",
                customName = customServiceName,
                modifier = Modifier.weight(1f),
                onClick = {
                    dialogName = customServiceName
                    showCustomDialog = true
                }
            )
            // Fill remaining cells in last row (maintain grid alignment)
            val totalItems = services.size + 1
            val remainder = totalItems % 3
            if (remainder != 0) {
                repeat(3 - remainder) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun ServiceTile(
    service: ServiceTemplate,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val brandColor = service.brandColor.toComposeColor()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(SubTrackShapes.l)
            .background(if (isSelected) AccentGreenBg else BgSurface)
            .border(
                1.dp,
                if (isSelected) AccentGreen.copy(alpha = 0.5f) else BorderDefault,
                SubTrackShapes.l
            )
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.m, horizontal = Spacing.s)
            .aspectRatio(0.9f, matchHeightConstraintsFirst = false)
    ) {
        ServiceLogo(
            serviceName = service.name,
            brandColor = brandColor,
            size = LogoSize.Large.dp
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = service.name,
            style = SubTrackType.monoXS,
            color = if (isSelected) AccentGreen else TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun OtherServiceTile(
    isSelected: Boolean,
    customName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(SubTrackShapes.l)
            .background(if (isSelected) AccentGreenBg else BgSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) AccentGreen.copy(alpha = 0.5f) else BorderDefault,
                shape = SubTrackShapes.l
            )
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.m, horizontal = Spacing.s)
            .aspectRatio(0.9f, matchHeightConstraintsFirst = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(LogoSize.Large.dp)
                .clip(SubTrackShapes.l)
                .background(BgSurfaceEl)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
        ) {
            Icon(Icons.Outlined.Add, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = if (isSelected && customName.isNotBlank()) customName else stringResource(R.string.create_subscription_other_tile_label),
            style = SubTrackType.monoXS,
            color = if (isSelected) AccentGreen else TextTertiary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ServiceStepPreview() {
    SubTrackTheme {
        ServiceStep(
            services = ServiceTemplates.popular,
            selectedService = ServiceTemplates.popular[0],
            customServiceName = "",
            onSelectService = {},
            onUpdateCustomName = {},
            onUpdateCustomColor = {}
        )
    }
}
