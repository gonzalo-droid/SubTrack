package com.gondroid.subtrack.feature.profile.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.BuildConfig
import com.gondroid.subtrack.R
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
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory())
    val context = LocalContext.current
    val messenger = LocalSnackbarMessenger.current
    val scope = rememberCoroutineScope()
    val exportChooserTitle = stringResource(R.string.settings_export_chooser)

    SettingsContent(
        onBack = onBack,
        onEditUser = onEditUser,
        onNotifications = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.openAppSettings(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            } else {
                context.openAppDetails()
            }
        },
        onLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            { context.openAppSettings(Settings.ACTION_APP_LOCALE_SETTINGS) }
        } else null,
        onExport = {
            scope.launch {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, "subtrack.csv")
                    putExtra(Intent.EXTRA_TEXT, viewModel.buildExport())
                }
                context.startActivity(Intent.createChooser(send, exportChooserTitle))
            }
        },
        onFeedback = {
            val mail = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
                putExtra(Intent.EXTRA_SUBJECT, "SubTrack ${BuildConfig.VERSION_NAME} · feedback")
            }
            try {
                context.startActivity(mail)
            } catch (e: ActivityNotFoundException) {
                messenger.show(R.string.settings_feedback_no_app)
            }
        },
        modifier = modifier
    )
}

/** Opens this app's page in a system settings screen (API 26+), falling back to "App info". */
@RequiresApi(Build.VERSION_CODES.O)
private fun Context.openAppSettings(action: String) {
    val intent = Intent(action)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        .setData(Uri.fromParts("package", packageName, null))
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        openAppDetails()
    }
}

private fun Context.openAppDetails() {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)))
}

@Composable
private fun SettingsContent(
    onBack: () -> Unit,
    onEditUser: () -> Unit,
    onNotifications: () -> Unit,
    onLanguage: (() -> Unit)?,
    onExport: () -> Unit,
    onFeedback: () -> Unit,
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
                contentDescription = stringResource(R.string.common_back),
                onClick = onBack
            )
            Text(
                stringResource(R.string.settings_title),
                style = SubTrackType.headlineL,
                color = TextPrimary,
                modifier = Modifier.semantics { heading() }
            )
        }

        Eyebrow(text = stringResource(R.string.profile_account_section))
        SettingsGroup {
            SettingsRow(
                Icons.Outlined.AccountCircle, AccentBlueBg, AccentBlue,
                stringResource(R.string.profile_account_personal), onClick = onEditUser
            )
            SettingsDivider()
            SettingsRow(
                Icons.Outlined.Public, AccentPurpleBg, AccentPurple,
                stringResource(R.string.profile_account_locale),
                value = stringResource(R.string.settings_locale_value)
            )
            SettingsDivider()
            SettingsRow(
                Icons.Outlined.NotificationsNone, AccentAmberBg, AccentAmber,
                stringResource(R.string.profile_account_notifications),
                value = stringResource(R.string.settings_notifications_desc),
                onClick = onNotifications
            )
            if (onLanguage != null) {
                SettingsDivider()
                SettingsRow(
                    Icons.Outlined.Language, AccentGreenBg, AccentGreen,
                    stringResource(R.string.settings_language),
                    value = stringResource(R.string.settings_language_desc),
                    onClick = onLanguage
                )
            }
        }

        Eyebrow(text = stringResource(R.string.settings_data_section), modifier = Modifier.padding(top = Spacing.s))
        SettingsGroup {
            SettingsRow(
                Icons.Outlined.CloudDownload, Color.White.copy(alpha = 0.06f), TextSecondary,
                stringResource(R.string.profile_account_export),
                value = stringResource(R.string.settings_export_desc),
                onClick = onExport
            )
        }

        Eyebrow(text = stringResource(R.string.profile_support_section), modifier = Modifier.padding(top = Spacing.s))
        SettingsGroup {
            SettingsRow(
                Icons.Outlined.MailOutline, AccentPurpleBg, AccentPurple,
                stringResource(R.string.profile_support_feedback), onClick = onFeedback
            )
            SettingsDivider()
            SettingsRow(
                Icons.Outlined.Info, Color.White.copy(alpha = 0.06f), TextSecondary,
                stringResource(R.string.settings_version),
                value = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            )
        }
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun SettingsContentPreview() {
    SubTrackTheme {
        SettingsContent(
            onBack = {}, onEditUser = {}, onNotifications = {}, onLanguage = {}, onExport = {}, onFeedback = {}
        )
    }
}
