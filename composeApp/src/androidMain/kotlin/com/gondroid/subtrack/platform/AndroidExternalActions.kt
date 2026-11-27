package com.gondroid.subtrack.platform

import androidx.browser.customtabs.CustomTabsIntent

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File

/** Implicit intents from the application context (hence FLAG_ACTIVITY_NEW_TASK). */
class AndroidExternalActions(private val context: Context) : ExternalActions {

    override fun openWhatsApp(phone: String?, message: String): Boolean {
        val uri = "https://wa.me/${phone.orEmpty()}?text=${Uri.encode(message)}".toUri()
        return launch(Intent(Intent.ACTION_VIEW, uri).setPackage(WHATSAPP)) ||
            // WhatsApp not installed: its Play Store page
            launch(Intent(Intent.ACTION_VIEW, "market://details?id=$WHATSAPP".toUri()))
    }

    override fun openInAppBrowser(url: String) {
        val intent = CustomTabsIntent.Builder().setShowTitle(true).build().intent.setData(url.toUri())
        if (!launch(intent)) launch(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    override fun share(text: String) {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        launch(Intent.createChooser(send, null))
    }

    override fun shareFile(fileName: String, mimeType: String, content: String, chooserTitle: String) {
        // A real attachment through the FileProvider (androidApp res/xml/file_paths.xml: cache/exports)
        val file = File(context.cacheDir, EXPORT_DIR).apply { mkdirs() }.resolve(fileName).apply { writeText(content) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        launch(Intent.createChooser(send, chooserTitle))
    }

    override fun email(to: String, subject: String, body: String): Boolean {
        // TRP-07: Gmail ignores an encoded recipient in the URI, so it also goes in EXTRA_EMAIL
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$to".toUri()).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        return launch(intent)
    }

    override fun openStoreReview() {
        val pkg = context.packageName
        if (!launch(Intent(Intent.ACTION_VIEW, "market://details?id=$pkg".toUri()))) {
            launch(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$pkg".toUri()))
        }
    }

    override fun openNotificationSettings() {
        val opened = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            launch(appSettings(Settings.ACTION_APP_NOTIFICATION_SETTINGS))
        if (!opened) openAppDetails()
    }

    override val canOpenLanguageSettings: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun openLanguageSettings() {
        if (!canOpenLanguageSettings || !launch(appSettings(Settings.ACTION_APP_LOCALE_SETTINGS))) openAppDetails()
    }

    private fun appSettings(action: String) = Intent(action)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        .setData(Uri.fromParts("package", context.packageName, null))

    private fun openAppDetails() {
        launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)))
    }

    private fun launch(intent: Intent): Boolean = try {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }

    private companion object {
        const val WHATSAPP = "com.whatsapp"
        const val EXPORT_DIR = "exports"
    }
}
