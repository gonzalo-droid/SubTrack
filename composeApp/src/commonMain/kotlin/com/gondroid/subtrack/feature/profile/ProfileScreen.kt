package com.gondroid.subtrack.feature.profile

import com.gondroid.subtrack.resources.common_error_offline
import com.gondroid.subtrack.core.result.isConnectivity
import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.cd_settings
import com.gondroid.subtrack.resources.common_cancel
import com.gondroid.subtrack.resources.legal_logos_title
import com.gondroid.subtrack.resources.profile_alias_section
import com.gondroid.subtrack.resources.profile_edit
import com.gondroid.subtrack.resources.profile_logout
import com.gondroid.subtrack.resources.profile_logout_confirm
import com.gondroid.subtrack.resources.profile_logout_error
import com.gondroid.subtrack.resources.profile_logout_title
import com.gondroid.subtrack.resources.profile_support_section
import com.gondroid.subtrack.resources.profile_templates_new
import com.gondroid.subtrack.resources.profile_templates_title
import com.gondroid.subtrack.resources.profile_title
import com.gondroid.subtrack.resources.profile_version
import org.koin.compose.viewmodel.koinViewModel
import com.gondroid.subtrack.core.designsystem.components.feedback.LocalSnackbarMessenger
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.designsystem.components.list.SettingsRow
import com.gondroid.subtrack.core.designsystem.components.list.SettingsDivider
import com.gondroid.subtrack.core.config.AppInfo

@Composable
fun ProfileScreen(
    onNavigateToTemplates: () -> Unit,
    onNavigateToReferral: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAliasEdit: () -> Unit = {},
    onNavigateToEditUser: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToEditTemplate: (String?) -> Unit = {},
    onDebugOnboarding: () -> Unit = {},
    onDebugData: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messenger = LocalSnackbarMessenger.current
    LaunchedEffect(Unit) {
        viewModel.signOutFailed.collect { error ->
            messenger.show(if (error.isConnectivity) Res.string.common_error_offline else Res.string.profile_logout_error)
        }
    }

    when (val state = uiState) {
        is ProfileUiState.Loading -> Box(modifier.fillMaxSize())
        is ProfileUiState.Success -> ProfileContent(
            data = state.data,
            onNavigateToAliasEdit = onNavigateToAliasEdit,
            onNavigateToReferral = onNavigateToReferral,
            onNavigateToEditUser = onNavigateToEditUser,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToEditTemplate = onNavigateToEditTemplate,
            onNavigateToTemplates = onNavigateToTemplates,
            onSignOut = viewModel::signOut,
            modifier = modifier
        )
    }
}

@Composable
internal fun ProfileContent(
    data: ProfileData,
    onNavigateToAliasEdit: () -> Unit,
    onNavigateToReferral: () -> Unit,
    onNavigateToEditUser: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditTemplate: (String?) -> Unit,
    onNavigateToTemplates: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLegalSheet by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(Res.string.profile_logout_title), color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onSignOut()
                }) {
                    Text(stringResource(Res.string.profile_logout_confirm), color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(Res.string.common_cancel), color = TextSecondary)
                }
            },
            containerColor = BgSurface
        )
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.base, vertical = Spacing.m),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.profile_title),
                    style = SubTrackType.headlineL,
                    color = TextPrimary
                )
                STIconButton(icon = Icons.Outlined.Settings, contentDescription = stringResource(Res.string.cd_settings), onClick = onNavigateToSettings)
            }
        }

        // Hero mini
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = Spacing.base)
                    .padding(bottom = Spacing.base)
                    .fillMaxWidth()
                    .clip(SubTrackShapes.xl)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                AccentGreen.copy(alpha = 0.10f),
                                AccentBlue.copy(alpha = 0.06f),
                                BgSurface
                            )
                        )
                    )
                    .border(1.dp, BorderDefault, SubTrackShapes.xl)
                    .padding(Spacing.base),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                Avatar(name = data.user.name, size = 48.dp, imageUrl = data.user.profileImageUrl)
                Column(modifier = Modifier.weight(1f)) {
                    Text(data.user.name, style = SubTrackType.titleL, color = TextPrimary)
                    Text(data.user.email ?: "", style = SubTrackType.monoXS, color = TextTertiary)
                }
                Box(
                    modifier = Modifier
                        .clip(SubTrackShapes.circle)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, BorderDefault, SubTrackShapes.circle)
                        .clickable(onClick = onNavigateToEditUser)
                        .padding(horizontal = Spacing.m, vertical = Spacing.xs)
                ) {
                    Text(
                        text = stringResource(Res.string.profile_edit),
                        style = SubTrackType.bodyS,
                        color = TextPrimary
                    )
                }
            }
        }

        // Pro card
        item {
            if (data.isPro) {
                ProCard(modifier = Modifier.padding(horizontal = Spacing.base).padding(bottom = Spacing.base))
            } else {
                ProCtaCard(modifier = Modifier.padding(horizontal = Spacing.base).padding(bottom = Spacing.base))
            }
        }

        // Alias section
        item {
            Eyebrow(
                text = stringResource(Res.string.profile_alias_section),
                modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.s)
            )
        }
        item {
            AliasCard(
                alias = data.userAlias,
                onEditClick = onNavigateToAliasEdit,
                modifier = Modifier.padding(horizontal = Spacing.base).padding(bottom = Spacing.base)
            )
        }

        // Templates section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.base)
                    .padding(bottom = Spacing.s),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    Text(
                        text = stringResource(Res.string.profile_templates_title),
                        style = SubTrackType.titleL,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(SubTrackShapes.s)
                            .background(BgSurfaceEl)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${data.templates.size}",
                            style = SubTrackType.monoXS,
                            color = TextSecondary
                        )
                    }
                }
                Text(
                    text = stringResource(Res.string.profile_templates_new),
                    style = SubTrackType.bodyS,
                    color = AccentBlue,
                    modifier = Modifier.clickable { onNavigateToEditTemplate(null) }
                )
            }
        }
        items(data.templates, key = { it.id }) { template ->
            TemplateCard(
                template = template,
                onClick = { onNavigateToEditTemplate(template.id) },
                modifier = Modifier
                    .padding(horizontal = Spacing.base)
                    .padding(bottom = 6.dp)
            )
        }

        // Spacer
        item { Spacer(Modifier.height(Spacing.base)) }

        // Account section
        // Referral card
        item {
            ReferralEntryCard(
                monthsEarned = data.monthsProEarned,
                onClick = onNavigateToReferral,
                modifier = Modifier
                    .padding(horizontal = Spacing.base)
                    .padding(top = Spacing.base)
            )
        }

        // Support section
        item {
            Eyebrow(
                text = stringResource(Res.string.profile_support_section),
                modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.s).padding(top = Spacing.base)
            )
        }
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.base)
                    .clip(SubTrackShapes.l)
                    .background(BgSurface)
                    .border(1.dp, BorderDefault, SubTrackShapes.l)
            ) {
                SettingsRow(
                    icon = Icons.Outlined.Lock,
                    iconBg = BorderDefault,
                    iconTint = TextSecondary,
                    title = stringResource(Res.string.legal_logos_title),
                    onClick = { showLegalSheet = true }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    iconBg = AccentRedBg,
                    iconTint = AccentRed,
                    title = stringResource(Res.string.profile_logout),
                    titleColor = AccentRed,
                    showChevron = false,
                    onClick = { showLogoutDialog = true }
                )
            }
        }


        // Footer
        item {
            Text(
                text = stringResource(Res.string.profile_version, AppInfo.VERSION_NAME, AppInfo.VERSION_CODE),
                style = SubTrackType.monoXS,
                color = TextTertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.base)
                    .padding(vertical = Spacing.base),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    if (showLegalSheet) {
        LegalLogosSheet(onDismiss = { showLegalSheet = false })
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
internal fun ProfileScreenPreview() {
    SubTrackTheme {
        ProfileScreen(onNavigateToTemplates = {}, onNavigateToReferral = {})
    }
}
