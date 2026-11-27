package com.gondroid.subtrack.platform

/**
 * Everything that leaves the app (PLT-01). The app never sends anything by itself: it only opens
 * the system app (WhatsApp, share sheet, mail, settings) with the content filled in.
 */
interface ExternalActions {
    /**
     * Opens WhatsApp with [message] filled in: on the chat with [phone] (digits, optional +) or,
     * when null, on the chat picker. Falls back to WhatsApp's store page; false when nothing opened.
     */
    fun openWhatsApp(phone: String?, message: String): Boolean

    /**
     * Opens one of our own pages (privacy, terms) without leaving the app (LNK-01): Custom Tabs on
     * Android, SFSafariViewController on iOS.
     */
    fun openInAppBrowser(url: String)

    /** System share sheet with plain text. */
    fun share(text: String)

    /** Shares [content] as a file attachment named [fileName] (e.g. the CSV export). */
    fun shareFile(fileName: String, mimeType: String, content: String, chooserTitle: String)

    /** Opens the mail app to [to] with [subject] and [body]; false when there is none. */
    fun email(to: String, subject: String, body: String): Boolean

    /** The app's store page, ready to write a review (AJU-05). */
    fun openStoreReview()

    /** This app's notification settings (or its settings page where there is none). */
    fun openNotificationSettings()

    /** Whether the system has a per-app language screen (Android 13+, iOS). */
    val canOpenLanguageSettings: Boolean

    fun openLanguageSettings()
}
