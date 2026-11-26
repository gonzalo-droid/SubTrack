package com.gondroid.subtrack.data.demo

import com.gondroid.subtrack.domain.model.Member
import com.gondroid.subtrack.domain.model.Payment
import com.gondroid.subtrack.domain.model.ReferralInfo
import com.gondroid.subtrack.domain.model.ReferralRecord
import com.gondroid.subtrack.domain.model.Subscription
import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.model.User
import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.PaymentAliasType
import com.gondroid.subtrack.domain.model.enums.PaymentStatus
import com.gondroid.subtrack.domain.model.enums.ReferralStatus
import com.gondroid.subtrack.domain.model.enums.SplitType
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory
import com.gondroid.subtrack.domain.model.enums.TemplateTone
import java.util.Calendar
import java.util.Locale

object DemoData {

    // ── Timestamps ──────────────────────────────────────────────────────────
    // Recent history is relative to today (M6): "this month" is always the current month and
    // the payment history covers the last months, so the demo never looks stale.
    private val now = System.currentTimeMillis()

    /** "yyyy-MM" for [offset] months from the current one (0 = this month, -1 = last month). */
    private fun monthKey(offset: Int): String {
        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, offset) }
        return String.format(Locale.US, "%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    /** Timestamp for [day] of the month [offset] months away, never later than now. */
    private fun dayOf(offset: Int, day: Int): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.MONTH, offset)
            set(Calendar.DAY_OF_MONTH, day.coerceAtMost(getActualMaximum(Calendar.DAY_OF_MONTH)))
            set(Calendar.HOUR_OF_DAY, 12)
        }
        return minOf(cal.timeInMillis, now)
    }

    private const val T_2025_01 = 1735689600000L  // 2025-01-01
    private val T_2026_01_10 = dayOf(-3, 10)
    private val T_2026_02_15 = dayOf(-2, 15)
    private val T_2026_03_20 = dayOf(-1, 20)
    private val T_2026_04_01 = dayOf(0, 1)
    private const val T_2025_03 = 1740787200000L  // 2025-03-01
    private const val T_2025_07 = 1751328000000L  // 2025-07-01
    private const val T_2025_10 = 1759305600000L  // 2025-10-01
    private val T_2025_11 = dayOf(-5, 1)
    private val T_2026_02_01 = dayOf(-2, 1)
    private val T_2026_02_03 = dayOf(-2, 3)
    private val T_2026_02_05 = dayOf(-2, 5)
    private val T_2026_02_20 = dayOf(-2, 20)
    private val T_2026_03_02 = dayOf(-1, 2)
    private val T_2026_03_03 = dayOf(-1, 3)
    private val T_2026_03_04 = dayOf(-1, 4)
    private val T_2026_04_02 = dayOf(0, 2)
    private val T_2026_04_05 = dayOf(0, 5)
    private val T_2026_02_02 = dayOf(-2, 2)
    private val T_2026_03_01 = dayOf(-1, 1)

    // ── Usuario principal ────────────────────────────────────────────────────
    val currentUser = User(
        id = "usr_gonzalo",
        name = "Gonzalo Meza",
        phone = "+51 987 654 321",
        email = "gonzalo@mail.com",
        isPro = true,
        referralCode = "GONZALO42",
        createdAt = T_2025_01
    )

    // ── Miembros Netflix ─────────────────────────────────────────────────────
    val memMaria = Member(
        id = "mem_maria",
        userId = "usr_maria",
        name = "María Rodríguez",
        phone = "+51 912 345 678",
        profileLabel = "Perfil 2",
        shareAmount = 10.98,
        isArchived = false,
        currentStatus = PaymentStatus.OVERDUE,
        joinedAt = T_2025_03
    )

    val memCarlos = Member(
        id = "mem_carlos",
        userId = "usr_carlos",
        name = "Carlos Lima",
        phone = "+51 934 567 890",
        profileLabel = "Perfil 3",
        shareAmount = 10.98,
        isArchived = true,
        currentStatus = PaymentStatus.OVERDUE,
        joinedAt = T_2025_03
    )

    val memLucia = Member(
        id = "mem_lucia",
        userId = "usr_lucia",
        name = "Lucía Paredes",
        phone = "+51 956 789 012",
        profileLabel = "Perfil 4",
        shareAmount = 10.98,
        isArchived = false,
        currentStatus = PaymentStatus.PAID,
        joinedAt = T_2025_03
    )

    val memRoberto = Member(
        id = "mem_roberto",
        userId = null,
        name = "Roberto Vargas",
        phone = "+51 978 901 234",
        profileLabel = "Perfil 3",
        shareAmount = 10.98,
        isArchived = false,
        currentStatus = PaymentStatus.PENDING,
        joinedAt = T_2025_07
    )

    // ── Miembro Disney+ (Gonzalo como miembro, no admin) ────────────────────
    val memGonzaloDisney = Member(
        id = "mem_gonzalo_disney",
        userId = "usr_gonzalo",
        name = "Gonzalo Meza",
        phone = "+51 987 654 321",
        profileLabel = "Perfil 2",
        shareAmount = 12.25,
        isArchived = false,
        currentStatus = PaymentStatus.PENDING,
        joinedAt = T_2025_10
    )

    // ── Miembros Spotify ─────────────────────────────────────────────────────
    val memAna = Member(
        id = "mem_ana",
        userId = "usr_ana",
        name = "Ana Paredes",
        phone = "+51 991 234 567",
        profileLabel = null,
        shareAmount = 8.97,
        isArchived = false,
        currentStatus = PaymentStatus.PENDING,
        joinedAt = T_2025_07
    )

    val memDiego = Member(
        id = "mem_diego",
        userId = null,
        name = "Diego Rojas",
        phone = "+51 923 456 789",
        profileLabel = null,
        shareAmount = 8.97,
        isArchived = false,
        currentStatus = PaymentStatus.PAID,
        joinedAt = T_2025_07
    )

    // ── Suscripciones ────────────────────────────────────────────────────────
    val subscriptions = listOf(
        Subscription(
            id = "sub_netflix",
            name = "Netflix Premium",
            brandColor = "#E50914",
            serviceTemplateId = "tpl_netflix",
            totalAmount = 54.90,
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 15,
            ownerId = "usr_gonzalo",
            isShared = true,
            splitType = SplitType.EQUAL,
            category = SubscriptionCategory.STREAMING,
            members = listOf(memMaria, memLucia, memRoberto),
            archivedMembers = listOf(memCarlos),
            createdAt = T_2025_03,
            updatedAt = T_2026_04_02
        ),
        Subscription(
            id = "sub_spotify",
            name = "Spotify Familiar",
            brandColor = "#1DB954",
            serviceTemplateId = "tpl_spotify",
            totalAmount = 26.90,
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 1,
            ownerId = "usr_gonzalo",
            isShared = true,
            splitType = SplitType.EQUAL,
            category = SubscriptionCategory.MUSIC,
            members = listOf(memAna, memDiego),
            createdAt = T_2025_07,
            updatedAt = T_2026_04_02
        ),
        Subscription(
            id = "sub_chatgpt",
            name = "ChatGPT Plus",
            brandColor = "#10A37F",
            serviceTemplateId = "tpl_chatgpt",
            totalAmount = 74.90,
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 10,
            ownerId = "usr_gonzalo",
            isShared = false,
            category = SubscriptionCategory.AI,
            createdAt = T_2025_07,
            updatedAt = T_2025_10
        ),
        Subscription(
            id = "sub_icloud",
            name = "iCloud 200GB",
            brandColor = "#147EFB",
            serviceTemplateId = "tpl_icloud",
            totalAmount = 13.00,
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 20,
            ownerId = "usr_gonzalo",
            isShared = false,
            category = SubscriptionCategory.CLOUD,
            createdAt = T_2025_07,
            updatedAt = T_2025_10
        ),
        Subscription(
            id = "sub_disney",
            name = "Disney+",
            brandColor = "#113CCF",
            serviceTemplateId = "tpl_disney",
            totalAmount = 48.90,
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 5,
            ownerId = "usr_maria",
            isShared = true,
            splitType = SplitType.EQUAL,
            category = SubscriptionCategory.STREAMING,
            members = listOf(memGonzaloDisney),
            createdAt = T_2025_10,
            updatedAt = T_2026_04_01
        ),
        Subscription(
            id = "sub_notion",
            name = "Notion Plus",
            brandColor = "#000000",
            serviceTemplateId = "tpl_notion",
            totalAmount = 32.50,
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 3,
            ownerId = "usr_gonzalo",
            isShared = false,
            category = SubscriptionCategory.PRODUCTIVITY,
            lastActivityAt = T_2026_03_02,
            createdAt = T_2025_11,
            updatedAt = T_2026_03_02
        )
    )

    // ── Pagos (historial Netflix · últimos 3 meses) ──────────────────────────
    val payments = listOf(
        // Febrero 2026
        Payment(
            id = "pay_netflix_2026-02_maria",
            subscriptionId = "sub_netflix",
            memberId = "mem_maria",
            monthKey = monthKey(-2),
            amount = 10.98,
            status = PaymentStatus.LATE,
            paidAt = T_2026_02_20,
            proofUrl = null,
            note = "Pagó tarde este mes"
        ),
        Payment(
            id = "pay_netflix_2026-02_carlos",
            subscriptionId = "sub_netflix",
            memberId = "mem_carlos",
            monthKey = monthKey(-2),
            amount = 10.98,
            status = PaymentStatus.OVERDUE,
            paidAt = null,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_netflix_2026-02_lucia",
            subscriptionId = "sub_netflix",
            memberId = "mem_lucia",
            monthKey = monthKey(-2),
            amount = 10.98,
            status = PaymentStatus.PAID,
            paidAt = T_2026_02_03,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_netflix_2026-02_roberto",
            subscriptionId = "sub_netflix",
            memberId = "mem_roberto",
            monthKey = monthKey(-2),
            amount = 10.98,
            status = PaymentStatus.PAID,
            paidAt = T_2026_02_05,
            proofUrl = null,
            note = null
        ),
        // Marzo 2026
        Payment(
            id = "pay_netflix_2026-03_maria",
            subscriptionId = "sub_netflix",
            memberId = "mem_maria",
            monthKey = monthKey(-1),
            amount = 10.98,
            status = PaymentStatus.PAID,
            paidAt = T_2026_03_03,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_netflix_2026-03_lucia",
            subscriptionId = "sub_netflix",
            memberId = "mem_lucia",
            monthKey = monthKey(-1),
            amount = 10.98,
            status = PaymentStatus.PAID,
            paidAt = T_2026_03_02,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_netflix_2026-03_roberto",
            subscriptionId = "sub_netflix",
            memberId = "mem_roberto",
            monthKey = monthKey(-1),
            amount = 10.98,
            status = PaymentStatus.PAID,
            paidAt = T_2026_03_04,
            proofUrl = null,
            note = null
        ),
        // Abril 2026
        Payment(
            id = "pay_netflix_2026-04_maria",
            subscriptionId = "sub_netflix",
            memberId = "mem_maria",
            monthKey = monthKey(0),
            amount = 10.98,
            status = PaymentStatus.OVERDUE,
            paidAt = null,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_netflix_2026-04_lucia",
            subscriptionId = "sub_netflix",
            memberId = "mem_lucia",
            monthKey = monthKey(0),
            amount = 10.98,
            status = PaymentStatus.PAID,
            paidAt = T_2026_04_05,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_netflix_2026-04_roberto",
            subscriptionId = "sub_netflix",
            memberId = "mem_roberto",
            monthKey = monthKey(0),
            amount = 10.98,
            status = PaymentStatus.PENDING,
            paidAt = null,
            proofUrl = null,
            note = null
        ),
        // Disney+ historial Gonzalo
        Payment(
            id = "pay_disney_2025-11_gonzalo",
            subscriptionId = "sub_disney",
            memberId = "mem_gonzalo_disney",
            monthKey = monthKey(-5),
            amount = 12.25,
            status = PaymentStatus.PAID,
            paidAt = T_2025_11,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_disney_2026-02_gonzalo",
            subscriptionId = "sub_disney",
            memberId = "mem_gonzalo_disney",
            monthKey = monthKey(-2),
            amount = 12.25,
            status = PaymentStatus.PAID,
            paidAt = T_2026_02_03,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_disney_2026-03_gonzalo",
            subscriptionId = "sub_disney",
            memberId = "mem_gonzalo_disney",
            monthKey = monthKey(-1),
            amount = 12.25,
            status = PaymentStatus.LATE,
            paidAt = T_2026_03_20,
            proofUrl = null,
            note = "Pagué tarde"
        ),
        Payment(
            id = "pay_disney_2026-04_gonzalo",
            subscriptionId = "sub_disney",
            memberId = "mem_gonzalo_disney",
            monthKey = monthKey(0),
            amount = 12.25,
            status = PaymentStatus.PENDING,
            paidAt = null,
            proofUrl = null,
            note = null
        ),
        // Spotify Febrero 2026
        Payment(
            id = "pay_spotify_2026-02_ana",
            subscriptionId = "sub_spotify",
            memberId = "mem_ana",
            monthKey = monthKey(-2),
            amount = 8.97,
            status = PaymentStatus.PAID,
            paidAt = T_2026_02_02,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_spotify_2026-02_diego",
            subscriptionId = "sub_spotify",
            memberId = "mem_diego",
            monthKey = monthKey(-2),
            amount = 8.97,
            status = PaymentStatus.PAID,
            paidAt = T_2026_02_03,
            proofUrl = null,
            note = null
        ),
        // Spotify Marzo 2026
        Payment(
            id = "pay_spotify_2026-03_ana",
            subscriptionId = "sub_spotify",
            memberId = "mem_ana",
            monthKey = monthKey(-1),
            amount = 8.97,
            status = PaymentStatus.LATE,
            paidAt = T_2026_03_20,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_spotify_2026-03_diego",
            subscriptionId = "sub_spotify",
            memberId = "mem_diego",
            monthKey = monthKey(-1),
            amount = 8.97,
            status = PaymentStatus.PAID,
            paidAt = T_2026_03_01,
            proofUrl = null,
            note = null
        ),
        // Spotify Abril 2026
        Payment(
            id = "pay_spotify_2026-04_ana",
            subscriptionId = "sub_spotify",
            memberId = "mem_ana",
            monthKey = monthKey(0),
            amount = 8.97,
            status = PaymentStatus.PENDING,
            paidAt = null,
            proofUrl = null,
            note = null
        ),
        Payment(
            id = "pay_spotify_2026-04_diego",
            subscriptionId = "sub_spotify",
            memberId = "mem_diego",
            monthKey = monthKey(0),
            amount = 8.97,
            status = PaymentStatus.PAID,
            paidAt = T_2026_04_02,
            proofUrl = null,
            note = null
        )
    )

    // ── Alias de cobranza ────────────────────────────────────────────────────
    val userAlias = UserAlias(
        yape = "987654321",
        plin = "987654321",
        cciBank = "BCP",
        cciNumber = "00219000016789",
        defaultMethod = PaymentAliasType.YAPE
    )

    // ── Referidos ────────────────────────────────────────────────────────────
    val referralInfo = ReferralInfo(
        code = "GONZALO42",
        totalInvited = 7,
        activeReferrals = 5,
        pendingReferrals = 1,
        monthsProEarned = 5,
        nextMilestone = 12,
        nextMilestoneReward = "1 año Pro completo"
    )

    val referralRecords = listOf(
        ReferralRecord("ref_1", "Carlos Mendoza", T_2026_04_01, ReferralStatus.ACTIVE),
        ReferralRecord("ref_2", "Sofía Gómez", T_2026_03_20, ReferralStatus.ACTIVE),
        ReferralRecord("ref_3", "Andrés Ríos", T_2026_02_15, ReferralStatus.PENDING),
        ReferralRecord("ref_4", "Valeria Torres", T_2026_01_10, ReferralStatus.ACTIVE)
    )

    // ── Plantillas de cobro ──────────────────────────────────────────────────
    val templates = listOf(
        Template(
            id = "tpl_friendly",
            name = "Amigable",
            emoji = "👋",
            tone = TemplateTone.FRIENDLY,
            messageBody = "Hola {nombre}! 👋 Te escribo para recordarte que este mes toca pagar el {servicio}. Son S/{monto}. ¿Puedes hacerme la transferencia esta semana? ¡Gracias! 😊",
            isDefault = true
        ),
        Template(
            id = "tpl_direct",
            name = "Directa",
            emoji = "💼",
            tone = TemplateTone.DIRECT,
            messageBody = "Hola {nombre}, recordatorio de pago: {servicio} — S/{monto}. Favor transferir a la brevedad. Gracias.",
            isDefault = true
        ),
        Template(
            id = "tpl_soft",
            name = "Suave",
            emoji = "🌸",
            tone = TemplateTone.SOFT,
            messageBody = "Hola {nombre} 🌸 Espero estés bien. Solo quería recordarte que el {servicio} cayó este mes — son S/{monto} cuando puedas. Sin apuros, pero agradezco tu puntualidad 🙏",
            isDefault = true
        ),
        Template(
            id = "tpl_firm",
            name = "Última llamada",
            emoji = "⚠️",
            tone = TemplateTone.FIRM,
            messageBody = "⚠️ {nombre}, este es el último recordatorio. Llevas más de {dias_mora} días de atraso con el pago del {servicio} (S/{monto}). Por favor regulariza a más tardar mañana o tendré que retirar tu acceso.",
            isDefault = true
        ),
        Template(
            id = "tpl_invitation",
            name = "Invitación",
            emoji = "🎉",
            tone = TemplateTone.FRIENDLY,
            messageBody = "Hola {nombre}! 👋 Te agregué a nuestro grupo de {servicio}. Tu parte es S/{monto}/mes. Yape: {yape}. ¡Descarga SubTrack para ver los detalles!",
            isDefault = true
        )
    )
}
