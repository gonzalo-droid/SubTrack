package com.gondroid.subtrack.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import com.gondroid.subtrack.core.auth.AuthConfig
import com.gondroid.subtrack.domain.model.GoogleCredential
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.koin.compose.koinInject
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.createCGImage
import platform.CoreImage.filterWithName
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataUsingEncoding
import platform.Foundation.setValue
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

@Composable
actual fun rememberGoogleSignIn(): GoogleSignInLauncher {
    val bridge = koinInject<GoogleSignInNativeBridge>()
    return remember(bridge) {
        GoogleSignInLauncher {
            if (AuthConfig.useFakeAuth) return@GoogleSignInLauncher Result.success(GoogleCredential(AuthConfig.FAKE_TOKEN))
            suspendCancellableCoroutine { cont ->
                bridge.signIn { idToken, accessToken, error ->
                    cont.resume(
                        when {
                            error != null -> Result.failure(NativeBridgeException(error))
                            idToken == null -> Result.success(null)
                            else -> Result.success(GoogleCredential(idToken, accessToken))
                        }
                    )
                }
            }
        }
    }
}

actual val notificationsNeedPermission: Boolean = true

@Composable
actual fun rememberNotificationPermissionRequest(onResult: (granted: Boolean) -> Unit): () -> Unit = {
    UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
        UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound
    ) { granted, _ -> dispatch_async(dispatch_get_main_queue()) { onResult(granted) } }
}

@OptIn(ExperimentalForeignApi::class)
actual fun qrCodeImage(content: String, sizePx: Int): ImageBitmap? {
    @Suppress("CAST_NEVER_SUCCEEDS")
    val data = (content as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return null
    val filter = CIFilter.filterWithName("CIQRCodeGenerator") ?: return null
    filter.setValue(data, forKey = "inputMessage")
    filter.setValue("M", forKey = "inputCorrectionLevel")
    val output = filter.outputImage ?: return null
    val scale = sizePx / output.extent.useContents { size.width }
    val scaled = output.imageByApplyingTransform(CGAffineTransformMakeScale(scale, scale))
    val cgImage = CIContext.contextWithOptions(null).createCGImage(scaled, fromRect = scaled.extent) ?: return null
    val png = UIImagePNGRepresentation(UIImage.imageWithCGImage(cgImage)) ?: return null
    return png.toByteArray().decodeToImageBitmap()
}

@Composable
actual fun rememberImagePicker(onPicked: (String?) -> Unit): () -> Unit {
    val delegate = remember { PhotoPickerDelegate() }
    delegate.onPicked = onPicked
    return {
        val configuration = PHPickerConfiguration().apply {
            filter = PHPickerFilter.imagesFilter()
            selectionLimit = 1
        }
        val picker = PHPickerViewController(configuration)
        picker.delegate = delegate
        topViewController()?.presentViewController(picker, animated = true, completion = null)
    }
}

private const val IMAGE_TYPE = "public.image"

/** Copies the picked photo into Documents (as JPEG) and returns its path. */
private class PhotoPickerDelegate : NSObject(), PHPickerViewControllerDelegateProtocol {
    var onPicked: (String?) -> Unit = {}

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val provider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider
        if (provider == null || !provider.hasItemConformingToTypeIdentifier(IMAGE_TYPE)) {
            onPicked(null)
            return
        }
        provider.loadDataRepresentationForTypeIdentifier(IMAGE_TYPE) { data, _ ->
            val path = data?.let { UIImage.imageWithData(it) }?.let(::saveProfilePhoto)
            dispatch_async(dispatch_get_main_queue()) { onPicked(path) }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun saveProfilePhoto(image: UIImage): String? {
    val jpeg = UIImageJPEGRepresentation(image, 0.85) ?: return null
    val documents = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).firstOrNull()
        as? platform.Foundation.NSURL ?: return null
    // A new name each time, so Coil does not show the previous photo from its cache
    val path = documents.path + "/profile_photo_${NSDate().timeIntervalSince1970.toLong()}.jpg"
    return if (jpeg.writeToFile(path, atomically = true)) path else null
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    if (isNotEmpty()) usePinned { platform.posix.memcpy(it.addressOf(0), bytes, length) }
}
