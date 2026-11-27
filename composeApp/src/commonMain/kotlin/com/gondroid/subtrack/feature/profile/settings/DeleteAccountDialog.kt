package com.gondroid.subtrack.feature.profile.settings

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.settings_delete_account_body
import com.gondroid.subtrack.resources.settings_delete_account_confirm
import com.gondroid.subtrack.resources.settings_delete_account_title
import com.gondroid.subtrack.resources.settings_deleting_account
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

@Composable
internal fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
internal fun DeletingAccountDialog() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = { Text(stringResource(Res.string.settings_deleting_account), color = TextPrimary) },
        text = { LinearProgressIndicator(color = AccentRed, modifier = Modifier.fillMaxWidth()) },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        containerColor = BgSurface
    )
}

