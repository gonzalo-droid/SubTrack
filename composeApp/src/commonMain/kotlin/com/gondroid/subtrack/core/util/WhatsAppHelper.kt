package com.gondroid.subtrack.core.util

import com.gondroid.subtrack.platform.ExternalActions
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.whatsapp_invalid_phone
import com.gondroid.subtrack.resources.whatsapp_not_installed
import org.jetbrains.compose.resources.StringResource

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

    /** Returns the error to show, or null if WhatsApp (or its store page) opened. */
    fun openWhatsApp(actions: ExternalActions, phone: String, message: String): StringResource? {
        val cleanPhone = phone.replace(Regex("[^\\d+]"), "")
        if (cleanPhone.length < 7) return Res.string.whatsapp_invalid_phone
        return if (actions.openWhatsApp(cleanPhone, message)) null else Res.string.whatsapp_not_installed
    }

    /** Opens WhatsApp with the message pre-filled and lets the user pick the chat. */
    fun shareToWhatsApp(actions: ExternalActions, message: String): StringResource? =
        if (actions.openWhatsApp(null, message)) null else Res.string.whatsapp_not_installed
}
