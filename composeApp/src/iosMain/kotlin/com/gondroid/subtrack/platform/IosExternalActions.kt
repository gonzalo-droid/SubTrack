package com.gondroid.subtrack.platform

import platform.StoreKit.SKStoreReviewController

import platform.SafariServices.SFSafariViewController

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenNotificationSettingsURLString
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController

class IosExternalActions : ExternalActions {

    // The universal link opens WhatsApp when installed and its web page (with the install button) otherwise
    override fun openWhatsApp(phone: String?, message: String): Boolean =
        open("https://wa.me/${phone.orEmpty().removePrefix("+")}?text=${message.encoded()}")

    override fun openInAppBrowser(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        val presenter = topViewController() ?: run { open(url); return }
        presenter.presentViewController(SFSafariViewController(uRL = nsUrl), animated = true, completion = null)
    }

    override fun share(text: String) = present(listOf(text))

    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    override fun shareFile(fileName: String, mimeType: String, content: String, chooserTitle: String) {
        val path = NSTemporaryDirectory() + fileName
        @Suppress("CAST_NEVER_SUCCEEDS")
        val written = (NSString.create(string = content)).writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
        if (written) present(listOf(NSURL.fileURLWithPath(path))) else share(content)
    }

    override fun email(to: String, subject: String, body: String): Boolean =
        open("mailto:$to?subject=${subject.encoded()}&body=${body.encoded()}")

    override fun openStorePage() {
        // Until the listing exists, the app's landing page has the store buttons
        open(if (APP_STORE_ID.isNotEmpty()) "itms-apps://apps.apple.com/app/id$APP_STORE_ID" else "https://nekodojo.app/apps/subtrack")
    }

    override fun openStoreReview() {
        if (APP_STORE_ID.isNotEmpty()) {
            open("itms-apps://apps.apple.com/app/id$APP_STORE_ID?action=write-review")
            return
        }
        // Not in the App Store yet: the system review prompt (it decides whether to show it)
        val scene = UIApplication.sharedApplication.connectedScenes.filterIsInstance<UIWindowScene>().firstOrNull() ?: return
        SKStoreReviewController.requestReviewInScene(scene)
    }

    private companion object {
        // TODO: [publicación iOS] the App Store id once the listing exists
        const val APP_STORE_ID = ""
    }

    override fun openNotificationSettings() {
        open(UIApplicationOpenNotificationSettingsURLString)
    }

    // iOS keeps the per-app language in the app's own settings page
    override val canOpenLanguageSettings: Boolean = true

    override fun openLanguageSettings() {
        open(UIApplicationOpenSettingsURLString)
    }

    private fun open(url: String): Boolean {
        val nsUrl = NSURL.URLWithString(url) ?: return false
        val app = UIApplication.sharedApplication
        if (!app.canOpenURL(nsUrl)) return false
        app.openURL(nsUrl, options = emptyMap<Any?, Any>(), completionHandler = null)
        return true
    }

    private fun present(items: List<Any>) {
        val presenter = topViewController() ?: return
        val controller = UIActivityViewController(activityItems = items, applicationActivities = null)
        // iPad: the sheet needs an anchor
        controller.popoverPresentationController?.sourceView = presenter.view
        presenter.presentViewController(controller, animated = true, completion = null)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun String.encoded(): String =
        (this as NSString).stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLQueryAllowedCharacterSet)
            ?.replace("&", "%26")?.replace("+", "%2B") ?: this
}

/** The controller currently on top, to present system sheets from Kotlin. */
internal fun topViewController(): UIViewController? {
    val window = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .flatMap { scene -> scene.windows.filterIsInstance<UIWindow>() }
        .firstOrNull { it.isKeyWindow() }
    var top = window?.rootViewController
    while (top?.presentedViewController != null) top = top.presentedViewController
    return top
}
