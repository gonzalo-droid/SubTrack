package com.gondroid.subtrack.domain.model.enums

enum class PaymentStatus { PAID, PENDING, OVERDUE, LATE }

/** Still has to pay this cycle: the admin can collect it ("Cobrar a …"). */
val PaymentStatus.isUnpaid: Boolean get() = this != PaymentStatus.PAID

/** Past the cutoff without paying: counts as debt ("deben", "Te deben", "Debes"). */
val PaymentStatus.isOverdue: Boolean get() = this == PaymentStatus.OVERDUE || this == PaymentStatus.LATE
