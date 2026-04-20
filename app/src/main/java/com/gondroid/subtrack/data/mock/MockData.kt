package com.gondroid.subtrack.data.mock

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

object MockData {

    // ── Timestamps ──────────────────────────────────────────────────────────
    private const val T_2025_01 = 1735689600000L  // 2025-01-01
    private const val T_2026_01_10 = 1768204800000L  // 2026-01-10
    private const val T_2026_02_15 = 1739577600000L  // 2026-02-15
    private const val T_2026_03_20 = 1742428800000L  // 2026-03-20
    private const val T_2026_04_01 = 1743465600000L  // 2026-04-01
    private const val T_2025_03 = 1740787200000L  // 2025-03-01
    private const val T_2025_07 = 1751328000000L  // 2025-07-01
    private const val T_2025_10 = 1759305600000L  // 2025-10-01
    private const val T_2025_11 = 1761984000000L  // 2025-11-01
    private const val T_2026_02_01 = 1769904000000L
    private const val T_2026_02_03 = 1770076800000L
    private const val T_2026_02_05 = 1770249600000L
    private const val T_2026_02_20 = 1771545600000L
    private const val T_2026_03_02 = 1772409600000L
    private const val T_2026_03_03 = 1772496000000L
    private const val T_2026_03_04 = 1772582400000L
    private const val T_2026_04_02 = 1775174400000L
    private const val T_2026_04_05 = 1775433600000L
    private const val T_2026_02_02 = 1769990400000L
    private const val T_2026_03_01 = 1772323200000L

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
            monthKey = "2026-02",
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
            monthKey = "2026-02",
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
            monthKey = "2026-02",
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
            monthKey = "2026-02",
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
            monthKey = "2026-03",
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
            monthKey = "2026-03",
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
            monthKey = "2026-03",
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
            monthKey = "2026-04",
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
            monthKey = "2026-04",
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
            monthKey = "2026-04",
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
            monthKey = "2025-11",
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
            monthKey = "2026-02",
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
            monthKey = "2026-03",
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
            monthKey = "2026-04",
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
            monthKey = "2026-02",
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
            monthKey = "2026-02",
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
            monthKey = "2026-03",
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
            monthKey = "2026-03",
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
            monthKey = "2026-04",
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
            monthKey = "2026-04",
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
