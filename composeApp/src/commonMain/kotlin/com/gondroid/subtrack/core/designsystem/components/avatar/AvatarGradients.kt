package com.gondroid.subtrack.core.designsystem.components.avatar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

private val gradients = listOf(
    Brush.linearGradient(
        colors = listOf(Color(0xFF0A84FF), Color(0xFF0040CC)),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(Color(0xFFBF5AF2), Color(0xFF7B2FBE)),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(Color(0xFF32D74B), Color(0xFF1A7A28)),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(Color(0xFFFFB340), Color(0xFFCC7A00)),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(Color(0xFFFF375F), Color(0xFFCC0035)),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(Color(0xFF64D2FF), Color(0xFF0090CC)),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
)

internal fun avatarGradientFor(name: String): Brush =
    gradients[abs(name.trim().hashCode()) % gradients.size]

internal fun initialsFor(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}"
        parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2)
        parts.size == 1 && parts[0].isNotEmpty() -> parts[0].first().toString()
        else -> "?"
    }.uppercase()
}
