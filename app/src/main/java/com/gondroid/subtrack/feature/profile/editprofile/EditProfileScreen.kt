package com.gondroid.subtrack.feature.profile.editprofile

import org.koin.compose.viewmodel.koinViewModel

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EditProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val messenger = LocalSnackbarMessenger.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditProfileEvent.Saved -> {
                    messenger.show(R.string.edit_profile_saved_toast)
                    onBack()
                }
            }
        }
    }

    when (val state = uiState) {
        is EditProfileUiState.Loading -> Box(modifier.fillMaxSize())
        is EditProfileUiState.Success -> EditProfileContent(
            data = state.data,
            onBack = onBack,
            onNameChange = viewModel::onNameChange,
            onPhoneChange = viewModel::onPhoneChange,
            onPhotoSelected = viewModel::onPhotoSelected,
            onSave = viewModel::save,
            modifier = modifier
        )
    }
}

@Composable
private fun EditProfileContent(
    data: EditProfileData,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPhotoSelected: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onPhotoSelected(it.toString()) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(com.gondroid.subtrack.core.designsystem.theme.BgApp)
    ) {
        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.s, vertical = Spacing.s)
        ) {
            STIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.common_back),
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = stringResource(R.string.edit_profile_title),
                style = SubTrackType.headlineL,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = stringResource(R.string.edit_profile_save),
                style = SubTrackType.bodyS,
                color = if (data.hasChanges && !data.isSaving) AccentBlue else TextTertiary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = Spacing.s)
                    .clickable(enabled = data.hasChanges && !data.isSaving) { onSave() }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.base),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.l))

            // Avatar con overlay de edición
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Avatar(
                    name = data.name,
                    size = 88.dp,
                    imageUrl = data.imageUrl
                )
                // Overlay de edición
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(SubTrackShapes.circle)
                        .background(AccentBlue)
                        .border(2.dp, com.gondroid.subtrack.core.designsystem.theme.BgApp, SubTrackShapes.circle)
                ) {
                    Text(text = "✎", style = SubTrackType.bodyS, color = Color.White)
                }
            }

            Spacer(Modifier.height(Spacing.s))
            Text(
                text = stringResource(R.string.edit_profile_change_photo),
                style = SubTrackType.bodyS,
                color = AccentBlue
            )
            Text(
                text = stringResource(R.string.edit_profile_change_photo_hint),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )

            Spacer(Modifier.height(Spacing.xl))

            // Campo nombre
            STTextField(
                value = data.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.edit_profile_field_name),
                isError = data.nameError != null,
                errorMessage = data.nameError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.base))

            // Campo teléfono
            STTextField(
                value = data.phone,
                onValueChange = onPhoneChange,
                label = stringResource(R.string.edit_profile_field_phone),
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.base))

            // Email solo lectura
            EmailReadOnlyField(
                email = data.email,
                provider = data.authProvider,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.xl))

            // Botón guardar
            PrimaryButton(
                text = stringResource(R.string.edit_profile_save_button),
                onClick = onSave,
                enabled = data.hasChanges && !data.isSaving,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.base))
        }

        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun EmailReadOnlyField(
    email: String?,
    provider: AuthProvider,
    modifier: Modifier = Modifier
) {
    val providerLabel = when (provider) {
        AuthProvider.GOOGLE -> stringResource(R.string.edit_profile_provider_google)
        AuthProvider.APPLE -> stringResource(R.string.edit_profile_provider_apple)
    }
    val helperText = when (provider) {
        AuthProvider.GOOGLE -> stringResource(R.string.edit_profile_email_managed_google)
        AuthProvider.APPLE -> stringResource(R.string.edit_profile_email_managed_apple)
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.edit_profile_field_email).uppercase(),
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

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun EditProfileScreenPreview() {
    SubTrackTheme {
        EditProfileContent(
            data = EditProfileData(
                id = "usr_1",
                name = "Gonzalo Meza",
                phone = "+51 987 654 321",
                email = "gonzalo@gmail.com",
                authProvider = AuthProvider.GOOGLE,
                imageUrl = null,
                hasChanges = true
            ),
            onBack = {},
            onNameChange = {},
            onPhoneChange = {},
            onPhotoSelected = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Apple provider")
@Composable
private fun EditProfileApplePreview() {
    SubTrackTheme {
        EditProfileContent(
            data = EditProfileData(
                id = "usr_2",
                name = "María García",
                phone = "+51 912 345 678",
                email = "m.garcia@icloud.com",
                authProvider = AuthProvider.APPLE,
                imageUrl = null,
                hasChanges = false
            ),
            onBack = {},
            onNameChange = {},
            onPhoneChange = {},
            onPhotoSelected = {},
            onSave = {}
        )
    }
}
