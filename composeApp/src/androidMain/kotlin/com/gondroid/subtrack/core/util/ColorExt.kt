package com.gondroid.subtrack.core.util

import androidx.core.graphics.toColorInt
import androidx.compose.ui.graphics.Color

fun String.toComposeColor(): Color = try {
    Color(toColorInt())
} catch (e: IllegalArgumentException) {
    Color.Gray
}
