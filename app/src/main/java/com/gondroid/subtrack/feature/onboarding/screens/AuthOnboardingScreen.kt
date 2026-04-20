package com.gondroid.subtrack.feature.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.animation.rememberRotation
import com.gondroid.subtrack.core.designsystem.icons.CustomIcons
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BgPage
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.feature.onboarding.AuthState

@Composable
fun AuthOnboardingScreen(
    authState: AuthState,
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by rememberRotation()
    val isLoading = authState is AuthState.Authenticating

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPage)
            .safeDrawingPadding()
            .padding(horizontal = Spacing.base),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(Spacing.xl))

        // Logo
        Text(
            buildString { append("SubTrack") },
            style = SubTrackType.displayM,
            color = TextPrimary
        )
        Text(".", style = SubTrackType.displayM, color = AccentGreen)

        Spacer(Modifier.weight(1f))

        // Visual hero
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            // Orbit ring
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .rotate(rotation)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), SubTrackShapes.circle)
            )
            // Center gradient square
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(70.dp)
                    .shadow(24.dp, SubTrackShapes.xxl, spotColor = AccentGreen.copy(alpha = 0.35f))
                    .clip(SubTrackShapes.xxl)
                    .background(Brush.linearGradient(listOf(AccentGreen, AccentBlue)))
            ) {
                Icon(Icons.Outlined.Lock, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        // Text
        Text(
            stringResource(R.string.onboarding_auth_title),
            style = SubTrackType.displayS,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(R.string.onboarding_auth_subtitle),
            style = SubTrackType.bodyM,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.xl))

        // Error message
        if (authState is AuthState.Error) {
            Text(
                authState.message,
                style = SubTrackType.bodyXS,
                color = com.gondroid.subtrack.core.designsystem.theme.AccentRed,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.s))
        }

        // Google button
        AuthButton(
            icon = { Icon(CustomIcons.Google, null, modifier = Modifier.size(20.dp), tint = Color.Unspecified) },
            text = stringResource(R.string.onboarding_auth_google),
            containerColor = Color.White,
            contentColor = Color(0xFF202124),
            enabled = !isLoading,
            onClick = onGoogleSignIn,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(Spacing.s))

        // Apple button
        AuthButton(
            icon = { Icon(CustomIcons.Apple, null, modifier = Modifier.size(20.dp), tint = Color.White) },
            text = stringResource(R.string.onboarding_auth_apple),
            containerColor = Color(0xFF1A1A1E),
            contentColor = Color.White,
            enabled = !isLoading,
            onClick = onAppleSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderStrong, SubTrackShapes.l)
        )

        Spacer(Modifier.height(Spacing.m))

        // Terms
        Text(
            stringResource(R.string.onboarding_auth_terms),
            style = SubTrackType.bodyXS,
            color = TextTertiary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun AuthButton(
    icon: @Composable () -> Unit,
    text: String,
    containerColor: Color,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(SubTrackShapes.l)
            .background(containerColor)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = Spacing.m, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            icon()
        }
        Spacer(Modifier.width(Spacing.s))
        Text(text, style = SubTrackType.titleL, color = contentColor)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050506)
@Composable
private fun AuthOnboardingPreview() {
    SubTrackTheme {
        AuthOnboardingScreen(
            authState = AuthState.Idle,
            onGoogleSignIn = {},
            onAppleSignIn = {}
        )
    }
}
