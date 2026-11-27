package com.gondroid.subtrack.core.util

import androidx.compose.ui.graphics.Color

/** "#RRGGBB" or "#AARRGGBB" to a Color; gray when the text is not a valid hex color. */
fun String.toComposeColor(): Color {
    val hex = removePrefix("#")
    val value = hex.toLongOrNull(16)
    return when {
        value == null -> Color.Gray
        hex.length == 6 -> Color(0xFF000000 or value)
        hex.length == 8 -> Color(value)
        else -> Color.Gray
    }
}
