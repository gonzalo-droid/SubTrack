package com.gondroid.subtrack.feature.createsubscription

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
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
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.feature.createsubscription.steps.DetailsStep
import com.gondroid.subtrack.feature.createsubscription.steps.MembersStep
import com.gondroid.subtrack.feature.createsubscription.steps.ServiceStep
import com.gondroid.subtrack.feature.createsubscription.steps.SplitStep

@Composable
fun CreateSubscriptionScreen(
    onBack: () -> Unit,
    onCreated: (String) -> Unit,
    startWithShared: Boolean = false,
    modifier: Modifier = Modifier
) {
    val viewModel: CreateSubscriptionViewModel = viewModel(
        factory = CreateSubscriptionViewModelFactory(startWithShared)
    )
    val form by viewModel.formState.collectAsStateWithLifecycle()
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isValid by viewModel.isCurrentStepValid.collectAsStateWithLifecycle()
    val totalSteps by viewModel.totalSteps.collectAsStateWithLifecycle()
    val stepIndex by viewModel.currentStepIndex.collectAsStateWithLifecycle()

    var showCancelDialog by remember { mutableStateOf(false) }

    // Success overlay
    if (uiState is CreateSubscriptionUiState.Success) {
        val success = uiState as CreateSubscriptionUiState.Success
        SuccessScreen(
            serviceName = success.serviceName,
            isShared = success.isShared,
            memberCount = success.memberCount,
            onViewSubscription = { onCreated(success.createdId) },
            onCreateAnother = { viewModel.resetWizard() },
            onGoHome = onBack,
            modifier = modifier
        )
        return
    }

    BackHandler {
        if (currentStep == WizardStep.SERVICE) showCancelDialog = true
        else viewModel.previousStep()
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(stringResource(R.string.create_subscription_cancel_title), color = TextPrimary) },
            text = { Text(stringResource(R.string.create_subscription_cancel_desc), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.create_subscription_cancel_confirm), color = AccentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(R.string.common_cancel), color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgApp)
    ) {
        WizardTopBar(
            stepIndex = stepIndex,
            totalSteps = totalSteps,
            onCancel = { showCancelDialog = true }
        )

        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    val isForward = targetState.ordinal >= initialState.ordinal
                    (slideInHorizontally(tween(280)) { if (isForward) it else -it } +
                     fadeIn(tween(280))) togetherWith
                    (slideOutHorizontally(tween(280)) { if (isForward) -it else it } +
                     fadeOut(tween(200)))
                },
                label = "wizard_step"
            ) { step ->
                when (step) {
                    WizardStep.SERVICE -> ServiceStep(
                        services = viewModel.popularServices,
                        selectedService = form.selectedService,
                        customServiceName = form.customServiceName,
                        onSelectService = viewModel::selectService,
                        onUpdateCustomName = viewModel::updateCustomServiceName,
                        onUpdateCustomColor = viewModel::updateCustomServiceColor
                    )
                    WizardStep.DETAILS -> DetailsStep(
                        service = form.selectedService,
                        totalAmount = form.totalAmount,
                        currency = form.currency,
                        cycle = form.cycle,
                        cutoffDay = form.cutoffDay,
                        onAmountChange = viewModel::updateAmount,
                        onApplySuggested = viewModel::applySuggestedAmount,
                        onCurrencyChange = viewModel::updateCurrency,
                        onCycleChange = viewModel::updateCycle,
                        onCutoffDayChange = viewModel::updateCutoffDay
                    )
                    WizardStep.MEMBERS -> MembersStep(
                        isShared = form.isShared,
                        members = form.members,
                        onToggleShared = viewModel::toggleShared,
                        onAddMember = viewModel::addMember,
                        onRemoveMember = viewModel::removeMember
                    )
                    WizardStep.SPLIT -> SplitStep(
                        totalAmount = form.totalAmountDouble,
                        members = form.members,
                        splitType = form.splitType,
                        memberShares = form.memberShares,
                        onSelectSplitType = viewModel::selectSplitType,
                        onUpdateMemberShare = viewModel::updateMemberShare
                    )
                }
            }
        }

        WizardFooter(
            currentStep = currentStep,
            isShared = form.isShared,
            isValid = isValid,
            isSubmitting = uiState is CreateSubscriptionUiState.Submitting,
            amountDisplay = if (form.totalAmount.isNotBlank()) "S/ ${form.totalAmount}" else null,
            serviceDisplay = form.resolvedServiceName.ifBlank { null },
            isFirstStep = currentStep == WizardStep.SERVICE,
            onNext = viewModel::nextStep,
            onBack = {
                if (currentStep == WizardStep.SERVICE) showCancelDialog = true
                else viewModel.previousStep()
            }
        )
    }
}

@Composable
private fun WizardTopBar(
    stepIndex: Int,
    totalSteps: Int,
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
            STIconButton(icon = Icons.Outlined.Close, contentDescription = "", onClick = onCancel)
            Text("${stepIndex + 1}/$totalSteps", style = SubTrackType.monoS, color = TextTertiary)
        }
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
private fun WizardFooter(
    currentStep: WizardStep,
    isShared: Boolean,
    isValid: Boolean,
    isSubmitting: Boolean,
    amountDisplay: String?,
    serviceDisplay: String?,
    isFirstStep: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val isFinalStep = currentStep == WizardStep.SPLIT || (currentStep == WizardStep.DETAILS && !isShared)
    val buttonText = when {
        isSubmitting -> stringResource(R.string.common_loading)
        isFinalStep && isShared -> stringResource(R.string.create_subscription_footer_create_shared)
        isFinalStep -> stringResource(R.string.create_subscription_footer_create)
        else -> stringResource(R.string.create_subscription_footer_continue)
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
                        stringResource(R.string.create_subscription_footer_back),
                        style = SubTrackType.bodyS,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    serviceName: String,
    isShared: Boolean,
    memberCount: Int,
    onViewSubscription: () -> Unit,
    onCreateAnother: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
            stringResource(R.string.create_subscription_success_title),
            style = SubTrackType.displayS,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = if (isShared)
                stringResource(R.string.create_subscription_success_shared_desc, serviceName, memberCount)
            else
                stringResource(R.string.create_subscription_success_personal_desc, serviceName),
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
                title = stringResource(R.string.create_subscription_success_whatsapp),
                subtitle = stringResource(R.string.create_subscription_success_whatsapp_desc),
                onClick = {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/")))
                    }
                }
            )
            Spacer(Modifier.height(Spacing.s))
        }
        SuccessActionCard(
            icon = Icons.Outlined.Visibility,
            iconBg = AccentBlueBg,
            iconTint = AccentBlue,
            title = stringResource(R.string.create_subscription_success_view),
            subtitle = stringResource(R.string.create_subscription_success_view_desc, serviceName),
            onClick = onViewSubscription
        )
        Spacer(Modifier.height(Spacing.s))
        SuccessActionCard(
            icon = Icons.Outlined.Refresh,
            iconBg = AccentPurpleBg,
            iconTint = AccentPurple,
            title = stringResource(R.string.create_subscription_success_another),
            subtitle = stringResource(R.string.create_subscription_success_another_desc),
            onClick = onCreateAnother
        )
        Spacer(Modifier.height(Spacing.xl))
        TextButton(onClick = onGoHome) {
            Text(stringResource(R.string.create_subscription_success_home), style = SubTrackType.bodyS, color = TextTertiary)
        }
    }
}

@Composable
private fun SuccessActionCard(
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

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun CreateSubscriptionScreenPreview() {
    SubTrackTheme { CreateSubscriptionScreen(onBack = {}, onCreated = {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun CreateSubscriptionSharedPreview() {
    SubTrackTheme { CreateSubscriptionScreen(onBack = {}, onCreated = {}, startWithShared = true) }
}
