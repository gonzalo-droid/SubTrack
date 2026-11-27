package com.gondroid.subtrack.feature.profile.settings

import com.gondroid.subtrack.resources.common_error_offline

import com.gondroid.subtrack.core.result.isConnectivity

import com.gondroid.subtrack.platform.ExternalActions
import com.gondroid.subtrack.platform.rememberGoogleSignIn
import org.koin.compose.koinInject

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.common_back
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.profile_account_export
import com.gondroid.subtrack.resources.profile_account_locale
import com.gondroid.subtrack.resources.profile_account_notifications
import com.gondroid.subtrack.resources.profile_account_personal
import com.gondroid.subtrack.resources.profile_account_section
import com.gondroid.subtrack.resources.profile_support_feedback
import com.gondroid.subtrack.resources.profile_support_section
import com.gondroid.subtrack.resources.settings_data_section
import com.gondroid.subtrack.resources.settings_delete_account
import com.gondroid.subtrack.resources.settings_delete_account_body
import com.gondroid.subtrack.resources.settings_delete_account_confirm
import com.gondroid.subtrack.resources.settings_delete_account_error
import com.gondroid.subtrack.resources.settings_delete_account_title
import com.gondroid.subtrack.resources.settings_deleting_account
import com.gondroid.subtrack.resources.settings_export_chooser
import com.gondroid.subtrack.resources.settings_export_desc
import com.gondroid.subtrack.resources.settings_export_file_name
import com.gondroid.subtrack.resources.settings_feedback_no_app
import com.gondroid.subtrack.resources.settings_feedback_subject
import com.gondroid.subtrack.resources.settings_language
import com.gondroid.subtrack.resources.settings_language_desc
import com.gondroid.subtrack.resources.settings_legal_section
import com.gondroid.subtrack.resources.settings_locale_value
import com.gondroid.subtrack.resources.settings_notifications_desc
import com.gondroid.subtrack.resources.settings_privacy
import com.gondroid.subtrack.resources.settings_privacy_url
import com.gondroid.subtrack.resources.settings_terms
import com.gondroid.subtrack.resources.settings_terms_url
import com.gondroid.subtrack.resources.settings_title
import com.gondroid.subtrack.resources.settings_version

import org.koin.compose.viewmodel.koinViewModel

import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.gondroid.subtrack.core.config.AppInfo
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger
import com.gondroid.subtrack.core.designsystem.components.list.SettingsDivider
import com.gondroid.subtrack.core.designsystem.components.list.SettingsGroup
import com.gondroid.subtrack.core.designsystem.components.list.SettingsRow
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.AccentPurpleBg
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onEditUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val externalActions = koinInject<ExternalActions>()
    val googleSignIn = rememberGoogleSignIn()
    val messenger = LocalSnackbarMessenger.current
    val scope = rememberCoroutineScope()
    val exportChooserTitle = stringResource(Res.string.settings_export_chooser)
    val exportFileName = stringResource(Res.string.settings_export_file_name)
    val feedbackSubject = stringResource(Res.string.settings_feedback_subject, AppInfo.VERSION_NAME)
    val uriHandler = LocalUriHandler.current
    val privacyUrl = stringResource(Res.string.settings_privacy_url)
    val termsUrl = stringResource(Res.string.settings_terms_url)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.deleteFailed.collect { error ->
            messenger.show(if (error.isConnectivity) Res.string.common_error_offline else Res.string.settings_delete_account_error)
        }
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onConfirm = {
                showDeleteDialog = false
                // Firebase asks for a fresh Google sign-in before deleting; a closed picker cancels
                scope.launch {
                    googleSignIn.signIn()
                        .onSuccess { token -> token?.let(viewModel::deleteAccount) }
                        .onFailure { viewModel.onReauthFailed() }
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    if (uiState.isDeletingAccount) DeletingAccountDialog()

    SettingsContent(
        onBack = onBack,
        onEditUser = onEditUser,
        onNotifications = externalActions::openNotificationSettings,
        onLanguage = if (externalActions.canOpenLanguageSettings) externalActions::openLanguageSettings else null,
        onExport = {
            scope.launch {
                // Shared as a real .csv attachment
                val csv = viewModel.buildExport()
                externalActions.shareFile(exportFileName, "text/csv", csv, exportChooserTitle)
            }
        },
        onPrivacy = { uriHandler.openUri(privacyUrl) },
        onTerms = { uriHandler.openUri(termsUrl) },
        onDeleteAccount = { showDeleteDialog = true },
        onFeedback = {
            if (!externalActions.email(feedbackSubject)) messenger.show(Res.string.settings_feedback_no_app)
        },
        modifier = modifier
    )
}


@Composable
private fun SettingsContent(
    onBack: () -> Unit,
    onEditUser: () -> Unit,
    onNotifications: () -> Unit,
    onLanguage: (() -> Unit)?,
    onExport: () -> Unit,
    onFeedback: () -> Unit,
    onPrivacy: () -> Unit,
    onTerms: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base),
        verticalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            modifier = Modifier.padding(vertical = Spacing.s)
        ) {
            STIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(Res.string.common_back),
                onClick = onBack
            )
            Text(
                stringResource(Res.string.settings_title),
                style = SubTrackType.headlineL,
                color = TextPrimary,
                modifier = Modifier.semantics { heading() }
            )
        }

        Eyebrow(text = stringResource(Res.string.profile_account_section))
        SettingsGroup {
            SettingsRow(
                Icons.Outlined.AccountCircle, AccentBlueBg, AccentBlue,
                stringResource(Res.string.profile_account_personal), onClick = onEditUser
            )
            SettingsDivider()
            SettingsRow(
                Icons.Outlined.Public, AccentPurpleBg, AccentPurple,
                stringResource(Res.string.profile_account_locale),
                value = stringResource(Res.string.settings_locale_value)
            )
            SettingsDivider()
            SettingsRow(
                Icons.Outlined.NotificationsNone, AccentAmberBg, AccentAmber,
                stringResource(Res.string.profile_account_notifications),
                value = stringResource(Res.string.settings_notifications_desc),
                onClick = onNotifications
            )
            if (onLanguage != null) {
                SettingsDivider()
                SettingsRow(
                    Icons.Outlined.Language, AccentGreenBg, AccentGreen,
                    stringResource(Res.string.settings_language),
                    value = stringResource(Res.string.settings_language_desc),
                    onClick = onLanguage
                )
            }
        }

        Eyebrow(text = stringResource(Res.string.settings_data_section), modifier = Modifier.padding(top = Spacing.s))
        SettingsGroup {
            SettingsRow(
                Icons.Outlined.CloudDownload, Color.White.copy(alpha = 0.06f), TextSecondary,
                stringResource(Res.string.profile_account_export),
                value = stringResource(Res.string.settings_export_desc),
                onClick = onExport
            )
        }

        Eyebrow(text = stringResource(Res.string.profile_support_section), modifier = Modifier.padding(top = Spacing.s))
        SettingsGroup {
            SettingsRow(
                Icons.Outlined.MailOutline, AccentPurpleBg, AccentPurple,
                stringResource(Res.string.profile_support_feedback), onClick = onFeedback
            )
            SettingsDivider()
            SettingsRow(
                Icons.Outlined.Info, Color.White.copy(alpha = 0.06f), TextSecondary,
                stringResource(Res.string.settings_version),
                value = "${AppInfo.VERSION_NAME} (${AppInfo.VERSION_CODE})"
            )
        }

        Eyebrow(text = stringResource(Res.string.settings_legal_section), modifier = Modifier.padding(top = Spacing.s))
        SettingsGroup {
            SettingsRow(
                Icons.Outlined.PrivacyTip, AccentBlueBg, AccentBlue,
                stringResource(Res.string.settings_privacy), onClick = onPrivacy
            )
            SettingsDivider()
            SettingsRow(
                Icons.Outlined.Description, Color.White.copy(alpha = 0.06f), TextSecondary,
                stringResource(Res.string.settings_terms), onClick = onTerms
            )
        }

        // Destructive action last and on its own, away from everyday rows
        SettingsGroup(modifier = Modifier.padding(top = Spacing.m)) {
            SettingsRow(
                Icons.Outlined.DeleteForever, AccentRedBg, AccentRed,
                stringResource(Res.string.settings_delete_account),
                titleColor = AccentRed,
                showChevron = false,
                onClick = onDeleteAccount
            )
        }
        Spacer(Modifier.height(Spacing.xl))
    }
}


@Composable
private fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_delete_account_title), color = TextPrimary) },
        text = { Text(stringResource(Res.string.settings_delete_account_body), color = TextSecondary) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.settings_delete_account_confirm), color = AccentRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.common_cancel), color = TextSecondary)
            }
        },
        containerColor = BgSurface
    )
}

/** Blocks the screen while the account is deleted; it cannot be dismissed halfway. */
@Composable
private fun DeletingAccountDialog() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = { Text(stringResource(Res.string.settings_deleting_account), color = TextPrimary) },
        text = { LinearProgressIndicator(color = AccentRed, modifier = Modifier.fillMaxWidth()) },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        containerColor = BgSurface
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SettingsContentPreview() {
    SubTrackTheme {
        SettingsContent(
            onBack = {}, onEditUser = {}, onNotifications = {}, onLanguage = {}, onExport = {}, onFeedback = {},
            onPrivacy = {}, onTerms = {}, onDeleteAccount = {}
        )
    }
}
