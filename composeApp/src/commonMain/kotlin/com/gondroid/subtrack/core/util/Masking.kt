package com.gondroid.subtrack.core.util

/** "00212•••••1234": the account number with its middle hidden. */
fun maskCci(number: String): String {
    if (number.length < 8) return number
    val prefix = number.take(5)
    val suffix = number.takeLast(4)
    return "$prefix•••••$suffix"
}
