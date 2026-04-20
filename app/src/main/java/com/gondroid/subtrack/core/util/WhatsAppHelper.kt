package com.gondroid.subtrack.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

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

    fun openWhatsApp(context: Context, phone: String, message: String) {
        val cleanPhone = phone.replace(Regex("[^\\d+]"), "")
        if (cleanPhone.length < 7) {
            Toast.makeText(context, "Número de teléfono inválido", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = Uri.parse("https://wa.me/$cleanPhone?text=${Uri.encode(message)}")
        val whatsAppIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(whatsAppIntent)
        } catch (e: ActivityNotFoundException) {
            // WhatsApp not installed — open Play Store
            try {
                val storeUri = Uri.parse("market://details?id=com.whatsapp")
                context.startActivity(Intent(Intent.ACTION_VIEW, storeUri))
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
