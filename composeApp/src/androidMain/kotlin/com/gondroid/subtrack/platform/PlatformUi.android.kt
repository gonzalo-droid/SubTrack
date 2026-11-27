package com.gondroid.subtrack.platform

import android.Manifest
import android.graphics.Color
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.gondroid.subtrack.core.auth.GoogleIdTokenProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

@Composable
actual fun rememberGoogleSignIn(): GoogleSignInLauncher {
    // Credential Manager needs the Activity, which is the composition's context
    val context = LocalContext.current
    return remember(context) { GoogleSignInLauncher { GoogleIdTokenProvider.request(context) } }
}

actual val notificationsNeedPermission: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@Composable
actual fun rememberNotificationPermissionRequest(onResult: (granted: Boolean) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission(), onResult)
    return {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onResult(true)
        }
    }
}

actual fun qrCodeImage(content: String, sizePx: Int): ImageBitmap? = try {
    val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, mapOf(EncodeHintType.MARGIN to 1))
    val bitmap = createBitmap(sizePx, sizePx)
    for (x in 0 until sizePx) {
        for (y in 0 until sizePx) bitmap[x, y] = if (bits[x, y]) Color.BLACK else Color.WHITE
    }
    bitmap.asImageBitmap()
} catch (_: WriterException) {
    null
}

@Composable
actual fun rememberImagePicker(onPicked: (String?) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onPicked(uri?.toString())
    }
    return { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
}
