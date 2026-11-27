package com.gondroid.subtrack.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import com.gondroid.subtrack.domain.model.GoogleCredential

/** Google's account picker. A null credential means the user closed it. */
fun interface GoogleSignInLauncher {
    suspend fun signIn(): Result<GoogleCredential?>
}

/** Credential Manager on Android, the GoogleSignIn package (through Swift) on iOS. */
@Composable
expect fun rememberGoogleSignIn(): GoogleSignInLauncher

/** True where the system asks before showing notifications (Android 13+, iOS). */
expect val notificationsNeedPermission: Boolean

/** Returns the action that shows the system notification prompt; [onResult] gets whether it was granted. */
@Composable
expect fun rememberNotificationPermissionRequest(onResult: (granted: Boolean) -> Unit): () -> Unit

/** Black-on-white QR code of [content], [sizePx] wide; null if it cannot be encoded. */
expect fun qrCodeImage(content: String, sizePx: Int): ImageBitmap?

/**
 * Returns the action that opens the system photo picker. [onPicked] gets something Coil can load
 * (a content URI on Android, a file path on iOS), or null when the user cancels.
 */
@Composable
expect fun rememberImagePicker(onPicked: (String?) -> Unit): () -> Unit
