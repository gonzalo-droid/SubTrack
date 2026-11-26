package com.gondroid.subtrack.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import com.gondroid.subtrack.R

object WhatsAppHelper {

    fun buildMemberQueryMessage(adminName: String, serviceName: String): String =
        "Hola $adminName! 👋 Te escribo sobre la suscripción de $serviceName. ¿Me puedes ayudar con una consulta?"

    fun buildReminderMessage(
        memberName: String,
        serviceName: String,
        amount: String,
        adminName: String
    ): String =
        "Hola $memberName! 👋 Te escribo para recordarte que este mes toca pagar el $serviceName. " +
        "Son S/$amount. ¿Puedes hacerme la transferencia esta semana? ¡Gracias! — $adminName"

    /** Returns a string resource describing the error, or null if WhatsApp (or the store) opened. */
    @StringRes
    fun openWhatsApp(context: Context, phone: String, message: String): Int? {
        val cleanPhone = phone.replace(Regex("[^\\d+]"), "")
        if (cleanPhone.length < 7) return R.string.whatsapp_invalid_phone
        return launch(context, Uri.parse("https://wa.me/$cleanPhone?text=${Uri.encode(message)}"))
    }

    /** Opens WhatsApp with the message pre-filled and lets the user pick the chat. */
    @StringRes
    fun shareToWhatsApp(context: Context, message: String): Int? =
        launch(context, Uri.parse("https://wa.me/?text=${Uri.encode(message)}"))

    @StringRes
    private fun launch(context: Context, uri: Uri): Int? {
        val whatsAppIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }
        return try {
            context.startActivity(whatsAppIntent)
            null
        } catch (e: ActivityNotFoundException) {
            // WhatsApp not installed — open Play Store
            try {
                val storeUri = Uri.parse("market://details?id=com.whatsapp")
                context.startActivity(Intent(Intent.ACTION_VIEW, storeUri))
                null
            } catch (e2: ActivityNotFoundException) {
                R.string.whatsapp_not_installed
            }
        }
    }
}
