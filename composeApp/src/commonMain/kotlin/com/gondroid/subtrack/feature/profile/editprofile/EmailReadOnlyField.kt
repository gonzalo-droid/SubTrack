package com.gondroid.subtrack.feature.profile.editprofile

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.edit_profile_email_managed_apple
import com.gondroid.subtrack.resources.edit_profile_email_managed_google
import com.gondroid.subtrack.resources.edit_profile_field_email
import com.gondroid.subtrack.resources.edit_profile_provider_apple
import com.gondroid.subtrack.resources.edit_profile_provider_google
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

@Composable
internal fun EmailReadOnlyField(
    email: String?,
    provider: AuthProvider,
    modifier: Modifier = Modifier
) {
    val providerLabel = when (provider) {
        AuthProvider.GOOGLE -> stringResource(Res.string.edit_profile_provider_google)
        AuthProvider.APPLE -> stringResource(Res.string.edit_profile_provider_apple)
    }
    val helperText = when (provider) {
        AuthProvider.GOOGLE -> stringResource(Res.string.edit_profile_email_managed_google)
        AuthProvider.APPLE -> stringResource(Res.string.edit_profile_email_managed_apple)
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.edit_profile_field_email).uppercase(),
            style = SubTrackType.monoXS,
            color = TextTertiary
        )
        Spacer(Modifier.height(Spacing.xs))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.base)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.base)
                .padding(horizontal = Spacing.base, vertical = 13.dp)
        ) {
            Text(
                text = email ?: "",
                style = SubTrackType.bodyL,
                color = TextTertiary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = 80.dp)
            )
            // Badge proveedor
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(SubTrackShapes.s)
                    .background(BgSurfaceEl)
                    .border(1.dp, BorderDefault, SubTrackShapes.s)
                    .padding(horizontal = Spacing.s, vertical = Spacing.xs)
            ) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = providerLabel,
                        style = SubTrackType.monoXS,
                        color = TextTertiary
                    )
                }
            }
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = helperText,
            style = SubTrackType.monoXS,
            color = TextTertiary.copy(alpha = 0.6f)
        )
    }
}

