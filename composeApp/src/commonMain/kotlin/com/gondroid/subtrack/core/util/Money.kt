package com.gondroid.subtrack.core.util

import com.gondroid.subtrack.domain.util.formatTwoDecimals

fun Double.asMoney(currency: String = "PEN"): String {
    val symbol = if (currency == "PEN") "S/" else currency
    return "$symbol ${formatTwoDecimals(this)}"
}
