package com.gondroid.subtrack.core.designsystem.animation

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

// Slide transition duration between onboarding screens
const val ONBOARDING_TRANSITION_MS = 400

// Float animation for orbit cards in ValueTracker
@Composable
fun rememberFloatOffset(delayMs: Int = 0): State<Float> {
    val inf = rememberInfiniteTransition(label = "float_$delayMs")
    return inf.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset_$delayMs"
    )
}

// Infinite rotation for auth orbit ring
@Composable
fun rememberRotation(): State<Float> {
    val inf = rememberInfiniteTransition(label = "rotation")
    return inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rotation"
    )
}

// Sparkle animation (scale + alpha cycle)
@Composable
fun rememberSparkleScale(delayMs: Int): State<Float> {
    val inf = rememberInfiniteTransition(label = "sparkle_$delayMs")
    return inf.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_scale_$delayMs"
    )
}

@Composable
fun rememberSparkleAlpha(delayMs: Int): State<Float> {
    val inf = rememberInfiniteTransition(label = "sparkle_alpha_$delayMs")
    return inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha_$delayMs"
    )
}

// Success pop spring spec
val successPopSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)
