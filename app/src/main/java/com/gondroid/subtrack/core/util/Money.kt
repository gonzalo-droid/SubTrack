package com.gondroid.subtrack.core.util

fun Double.asMoney(currency: String = "PEN"): String {
    val symbol = if (currency == "PEN") "S/" else currency
    return "$symbol %.2f".format(this)
}
