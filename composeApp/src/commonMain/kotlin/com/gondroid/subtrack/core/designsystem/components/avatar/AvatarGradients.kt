package com.gondroid.subtrack.core.designsystem.components.avatar

import com.gondroid.subtrack.core.designsystem.theme.AccentGreenDark

import com.gondroid.subtrack.core.designsystem.theme.AccentCyanDark

import com.gondroid.subtrack.core.designsystem.theme.AccentBlueDark

import com.gondroid.subtrack.core.designsystem.theme.AccentPurpleDark

import com.gondroid.subtrack.core.designsystem.theme.AccentPurple

import com.gondroid.subtrack.core.designsystem.theme.AccentPinkDark

import com.gondroid.subtrack.core.designsystem.theme.AccentPink

import com.gondroid.subtrack.core.designsystem.theme.AccentGreen

import com.gondroid.subtrack.core.designsystem.theme.AccentCyan

import com.gondroid.subtrack.core.designsystem.theme.AccentBlue

import com.gondroid.subtrack.core.designsystem.theme.AccentAmberDark

import com.gondroid.subtrack.core.designsystem.theme.AccentAmber

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

private val gradients = listOf(
    Brush.linearGradient(
        colors = listOf(AccentBlue, AccentBlueDark),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(AccentPurple, AccentPurpleDark),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(AccentGreen, AccentGreenDark),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(AccentAmber, AccentAmberDark),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(AccentPink, AccentPinkDark),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    ),
    Brush.linearGradient(
        colors = listOf(AccentCyan, AccentCyanDark),
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
