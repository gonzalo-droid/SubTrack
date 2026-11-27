package com.gondroid.subtrack.domain.model

enum class TemplateVariable(
    val token: String,
    val displayLabel: String,
    val exampleValue: String
) {
    MEMBER_NAME("{nombre}", "Nombre del deudor", "María"),
    SERVICE_NAME("{servicio}", "Servicio", "Netflix"),
    AMOUNT("{monto}", "Monto a cobrar", "S/ 13.73"),
    DUE_DATE("{fecha}", "Fecha de corte", "17 de abril"),
    YAPE("{yape}", "Yape del admin", "987 654 321"),
    PLIN("{plin}", "Plin del admin", "987 654 321"),
    CCI("{cci}", "CCI del admin", "00219•••••6789"),
    DAYS_OVERDUE("{dias_atraso}", "Días de atraso", "3"),
    ADMIN_NAME("{mi_nombre}", "Tu nombre", "Gonzalo")
}
