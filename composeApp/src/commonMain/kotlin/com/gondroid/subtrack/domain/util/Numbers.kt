package com.gondroid.subtrack.domain.util

import kotlin.math.abs
import kotlin.math.roundToLong

/** [value] with exactly two decimals and a dot ("12.50"), the same on every platform and locale. */
fun formatTwoDecimals(value: Double): String {
    val cents = (abs(value) * 100).roundToLong()
    val sign = if (value < 0 && cents != 0L) "-" else ""
    return "$sign${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
}
