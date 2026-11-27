package com.gondroid.subtrack.core.util

import com.gondroid.subtrack.domain.util.formatTwoDecimals

/** I18N-02: the one amount formatter for the UI ("S/ 12.50"); tested in MoneyTest. */
fun Double.asMoney(currency: String = "PEN"): String {
    val symbol = if (currency == "PEN") "S/" else currency
    return "$symbol ${formatTwoDecimals(this)}"
}
