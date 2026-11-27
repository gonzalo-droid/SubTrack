package com.gondroid.subtrack.feature.createsubscription

import androidx.compose.ui.platform.LocalUriHandler
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.cd_close_wizard
import com.gondroid.subtrack.resources.common_loading
import com.gondroid.subtrack.resources.create_subscription_footer_back
import com.gondroid.subtrack.resources.create_subscription_footer_continue
import com.gondroid.subtrack.resources.create_subscription_footer_create
import com.gondroid.subtrack.resources.create_subscription_footer_create_shared
import com.gondroid.subtrack.resources.create_subscription_success_another
import com.gondroid.subtrack.resources.create_subscription_success_another_desc
import com.gondroid.subtrack.resources.create_subscription_success_home
import com.gondroid.subtrack.resources.create_subscription_success_personal_desc
import com.gondroid.subtrack.resources.create_subscription_success_shared_desc
import com.gondroid.subtrack.resources.create_subscription_success_title
import com.gondroid.subtrack.resources.create_subscription_success_view
import com.gondroid.subtrack.resources.create_subscription_success_view_desc
import com.gondroid.subtrack.resources.create_subscription_success_whatsapp
import com.gondroid.subtrack.resources.create_subscription_success_whatsapp_desc
import com.gondroid.subtrack.resources.edit_subscription_save
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.AccentPurpleBg
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

@Composable
internal fun WizardTopBar(
    stepIndex: Int,
    totalSteps: Int,
    showProgress: Boolean,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.base, vertical = Spacing.m)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            STIconButton(icon = Icons.Outlined.Close, contentDescription = stringResource(Res.string.cd_close_wizard), onClick = onCancel)
            if (showProgress) {
                Text("${stepIndex + 1}/$totalSteps", style = SubTrackType.monoS, color = TextTertiary)
            }
        }
        if (!showProgress) return@Column
        Spacer(Modifier.height(Spacing.s))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(SubTrackShapes.circle)
                        .background(if (index <= stepIndex) AccentGreen else BgSurfaceEl)
                )
            }
        }
    }
}

@Composable
internal fun WizardFooter(
    currentStep: WizardStep,
    isShared: Boolean,
    isEditing: Boolean,
    isValid: Boolean,
    isSubmitting: Boolean,
    amountDisplay: String?,
    serviceDisplay: String?,
    isFirstStep: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val isFinalStep = nextWizardStep(currentStep, isShared, isEditing) == null
    val buttonText = when {
        isSubmitting -> stringResource(Res.string.common_loading)
        isEditing -> stringResource(Res.string.edit_subscription_save)
        isFinalStep && isShared -> stringResource(Res.string.create_subscription_footer_create_shared)
        isFinalStep -> stringResource(Res.string.create_subscription_footer_create)
        else -> stringResource(Res.string.create_subscription_footer_continue)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgApp)
    ) {
        HorizontalDivider(color = BorderDefault)
        AnimatedVisibility(visible = amountDisplay != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.base, vertical = Spacing.s),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(serviceDisplay ?: "", style = SubTrackType.bodyS, color = TextSecondary)
                Text(amountDisplay ?: "", style = SubTrackType.monoS, color = AccentGreen)
            }
        }
        Column(modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.m)) {
            PrimaryButton(
                text = buttonText,
                onClick = onNext,
                enabled = isValid && !isSubmitting,
                trailingIcon = if (!isFinalStep) Icons.Outlined.ArrowForward else null,
                accent = if (isFinalStep) AccentGreen else null,
                modifier = Modifier.fillMaxWidth()
            )
            if (!isFirstStep) {
                Spacer(Modifier.height(Spacing.s))
                TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(Res.string.create_subscription_footer_back),
                        style = SubTrackType.bodyS,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
internal fun SuccessScreen(
    serviceName: String,
    isShared: Boolean,
    memberCount: Int,
    onViewSubscription: () -> Unit,
    onCreateAnother: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgApp)
            .padding(Spacing.base),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(SubTrackShapes.circle)
                .background(AccentGreenBg)
        ) {
            Icon(Icons.Outlined.CheckCircle, null, tint = AccentGreen, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(Spacing.l))
        Text(
            stringResource(Res.string.create_subscription_success_title),
            style = SubTrackType.displayS,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = if (isShared)
                pluralStringResource(Res.plurals.create_subscription_success_shared_desc, memberCount, serviceName, memberCount)
            else
                stringResource(Res.string.create_subscription_success_personal_desc, serviceName),
            style = SubTrackType.bodyM,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.xxl))

        if (isShared) {
            SuccessActionCard(
                icon = Icons.Outlined.CheckCircle,
                iconBg = AccentGreenBg,
                iconTint = AccentGreen,
                title = stringResource(Res.string.create_subscription_success_whatsapp),
                subtitle = stringResource(Res.string.create_subscription_success_whatsapp_desc),
                onClick = { runCatching { uriHandler.openUri("https://wa.me/") } }
            )
            Spacer(Modifier.height(Spacing.s))
        }
        SuccessActionCard(
            icon = Icons.Outlined.Visibility,
            iconBg = AccentBlueBg,
            iconTint = AccentBlue,
            title = stringResource(Res.string.create_subscription_success_view),
            subtitle = stringResource(Res.string.create_subscription_success_view_desc, serviceName),
            onClick = onViewSubscription
        )
        Spacer(Modifier.height(Spacing.s))
        SuccessActionCard(
            icon = Icons.Outlined.Refresh,
            iconBg = AccentPurpleBg,
            iconTint = AccentPurple,
            title = stringResource(Res.string.create_subscription_success_another),
            subtitle = stringResource(Res.string.create_subscription_success_another_desc),
            onClick = onCreateAnother
        )
        Spacer(Modifier.height(Spacing.xl))
        TextButton(onClick = onGoHome) {
            Text(stringResource(Res.string.create_subscription_success_home), style = SubTrackType.bodyS, color = TextTertiary)
        }
    }
}

@Composable
internal fun SuccessActionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(SubTrackShapes.base)
                .background(iconBg)
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = SubTrackType.titleL, color = TextPrimary)
            Text(subtitle, style = SubTrackType.bodyXS, color = TextSecondary)
        }
    }
}

