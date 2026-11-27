package com.gondroid.subtrack.feature.createsubscription

import androidx.compose.ui.ExperimentalComposeUiApi
import com.gondroid.subtrack.platform.notificationsNeedPermission
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.common_error_generic
import com.gondroid.subtrack.resources.create_subscription_cancel_confirm
import com.gondroid.subtrack.resources.create_subscription_cancel_desc
import com.gondroid.subtrack.resources.create_subscription_cancel_title
import com.gondroid.subtrack.resources.edit_subscription_not_found
import com.gondroid.subtrack.resources.edit_subscription_saved
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.feature.createsubscription.steps.DetailsStep
import com.gondroid.subtrack.feature.createsubscription.steps.MembersStep
import com.gondroid.subtrack.feature.createsubscription.steps.ServiceStep
import com.gondroid.subtrack.feature.createsubscription.steps.SplitStep
import com.gondroid.subtrack.feature.createsubscription.steps.WhoPaysStep
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger
import androidx.compose.runtime.LaunchedEffect
import com.gondroid.subtrack.feature.onboarding.screens.NotificationsPermissionScreen
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun CreateSubscriptionScreen(
    onBack: () -> Unit,
    onCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    startWithShared: Boolean = false,
    editSubscriptionId: String? = null
) {
    val viewModel = koinViewModel<CreateSubscriptionViewModel>(
        parameters = { parametersOf(startWithShared, editSubscriptionId) }
    )
    val askNotificationPermission by viewModel.askNotificationPermission.collectAsStateWithLifecycle()
    val form by viewModel.formState.collectAsStateWithLifecycle()
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isValid by viewModel.isCurrentStepValid.collectAsStateWithLifecycle()
    val totalSteps by viewModel.totalSteps.collectAsStateWithLifecycle()
    val stepIndex by viewModel.currentStepIndex.collectAsStateWithLifecycle()

    var showCancelDialog by remember { mutableStateOf(false) }

    val messenger = LocalSnackbarMessenger.current
    if (uiState is CreateSubscriptionUiState.Saved) {
        LaunchedEffect(Unit) {
            messenger.show(Res.string.edit_subscription_saved)
            onBack()
        }
    }

    // Success overlay — preceded by the notification prompt the first time (D2)
    if (uiState is CreateSubscriptionUiState.Success) {
        if (askNotificationPermission == null) return // DataStore not read yet: one frame at most
        if (askNotificationPermission == true && notificationsNeedPermission) {
            NotificationsPermissionScreen(
                onGrant = viewModel::onNotificationPromptDone,
                onSkip = viewModel::onNotificationPromptDone,
                modifier = modifier
            )
            return
        }
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

    val hasPreviousStep = previousWizardStep(currentStep, form.shared, form.isEditing) != null
    // Leaving from the first step: creating asks to discard, editing just goes back.
    val leave = { if (form.isEditing) onBack() else showCancelDialog = true }
    val goBack = { if (hasPreviousStep) viewModel.previousStep() else leave() }

    @OptIn(ExperimentalComposeUiApi::class)

    BackHandler(onBack = goBack)

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            CreateSubscriptionUiState.Error -> {
                messenger.show(Res.string.common_error_generic)
                viewModel.errorShown()
            }
            CreateSubscriptionUiState.NotFound -> {
                messenger.show(Res.string.edit_subscription_not_found)
                onBack()
            }
            else -> Unit
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(stringResource(Res.string.create_subscription_cancel_title), color = TextPrimary) },
            text = { Text(stringResource(Res.string.create_subscription_cancel_desc), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(stringResource(Res.string.create_subscription_cancel_confirm), color = AccentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(Res.string.common_cancel), color = TextSecondary)
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
            showProgress = !form.isEditing,
            onCancel = leave
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
                        otherService = viewModel.otherService,
                        selectedService = form.selectedService,
                        customServiceName = form.customServiceName,
                        onSelectService = viewModel::selectService,
                        onUpdateCustomName = viewModel::updateCustomServiceName,
                        onUpdateCustomColor = viewModel::updateCustomServiceColor
                    )
                    WizardStep.WHO_PAYS -> WhoPaysStep(
                        serviceName = form.resolvedServiceName,
                        isShared = form.isShared,
                        suggestsShared = form.selectedService?.suggestsShared == true,
                        onSelect = viewModel::setShared
                    )
                    // Editing: inputs stay hidden until the subscription loads, so typing can't be overwritten
                    WizardStep.DETAILS -> if (form.isEditing && form.selectedService == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentGreen)
                        }
                    } else DetailsStep(
                        isEditing = form.isEditing,
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
                        members = form.members,
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
            isShared = form.shared,
            isEditing = form.isEditing,
            isValid = isValid,
            isSubmitting = uiState is CreateSubscriptionUiState.Submitting,
            amountDisplay = if (form.totalAmount.isNotBlank()) "S/ ${form.totalAmount}" else null,
            serviceDisplay = form.resolvedServiceName.ifBlank { null },
            isFirstStep = !hasPreviousStep,
            onNext = viewModel::nextStep,
            onBack = goBack
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
internal fun CreateSubscriptionScreenPreview() {
    SubTrackTheme { CreateSubscriptionScreen(onBack = {}, onCreated = {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
internal fun CreateSubscriptionSharedPreview() {
    SubTrackTheme { CreateSubscriptionScreen(onBack = {}, onCreated = {}, startWithShared = true) }
}
