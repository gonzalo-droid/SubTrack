package com.gondroid.subtrack.feature.profile

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.AccentPurpleBg
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.AccentRedBg
import com.gondroid.subtrack.core.designsystem.theme.BgSheet
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BgSurfaceEl
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.core.util.maskCci
import com.gondroid.subtrack.domain.model.Template
import com.gondroid.subtrack.domain.model.UserAlias
import com.gondroid.subtrack.domain.model.enums.PaymentAliasType
import com.gondroid.subtrack.domain.model.enums.TemplateTone

@Composable
fun ProfileScreen(
    onNavigateToTemplates: () -> Unit,
    onNavigateToReferral: () -> Unit,
    onNavigateToAliasEdit: () -> Unit = {},
    onNavigateToEditUser: () -> Unit = {},
    onNavigateToEditTemplate: (String?) -> Unit = {},
    onDebugOnboarding: () -> Unit = {},
    onDebugData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ProfileUiState.Loading -> Box(modifier.fillMaxSize())
        is ProfileUiState.Success -> ProfileContent(
            data = state.data,
            onNavigateToAliasEdit = onNavigateToAliasEdit,
            onNavigateToReferral = onNavigateToReferral,
            onNavigateToEditUser = onNavigateToEditUser,
            onNavigateToEditTemplate = onNavigateToEditTemplate,
            onNavigateToTemplates = onNavigateToTemplates,
            modifier = modifier
        )
    }
}

@Composable
private fun ProfileContent(
    data: ProfileData,
    onNavigateToAliasEdit: () -> Unit,
    onNavigateToReferral: () -> Unit,
    onNavigateToEditUser: () -> Unit,
    onNavigateToEditTemplate: (String?) -> Unit,
    onNavigateToTemplates: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLegalSheet by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(true) }
    var faceIdEnabled by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.profile_logout_title), color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.profile_logout_confirm), color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.common_cancel), color = TextSecondary)
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
                    text = stringResource(R.string.profile_title),
                    style = SubTrackType.headlineL,
                    color = TextPrimary
                )
                STIconButton(icon = Icons.Outlined.Settings, contentDescription = "", onClick = {})
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
                Avatar(name = data.user.name, size = 48.dp)
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
                        text = stringResource(R.string.profile_edit),
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
                text = stringResource(R.string.profile_alias_section),
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
                        text = stringResource(R.string.profile_templates_title),
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
                    text = stringResource(R.string.profile_templates_new),
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
        item {
            Eyebrow(
                text = stringResource(R.string.profile_account_section),
                modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.s)
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
                    icon = Icons.Outlined.AccountCircle,
                    iconBg = AccentBlueBg,
                    iconTint = AccentBlue,
                    title = stringResource(R.string.profile_account_personal),
                    onClick = onNavigateToEditUser
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Language,
                    iconBg = AccentPurpleBg,
                    iconTint = AccentPurple,
                    title = stringResource(R.string.profile_account_locale),
                    value = "Perú · S/ PEN",
                    onClick = {}
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.NotificationsNone,
                    iconBg = AccentAmberBg,
                    iconTint = AccentAmber,
                    title = stringResource(R.string.profile_account_notifications),
                    onClick = {}
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Lock,
                    iconBg = AccentBlueBg,
                    iconTint = AccentBlue,
                    title = stringResource(R.string.profile_account_faceid),
                    trailing = {
                        Switch(
                            checked = faceIdEnabled,
                            onCheckedChange = { faceIdEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = AccentGreen)
                        )
                    }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.CloudDownload,
                    iconBg = Color.White.copy(alpha = 0.06f),
                    iconTint = TextSecondary,
                    title = stringResource(R.string.profile_account_export),
                    onClick = {}
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.DarkMode,
                    iconBg = Color.White.copy(alpha = 0.06f),
                    iconTint = TextSecondary,
                    title = stringResource(R.string.profile_account_dark_theme),
                    trailing = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = AccentGreen)
                        )
                    }
                )
            }
        }

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
                text = stringResource(R.string.profile_support_section),
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
                    icon = Icons.Outlined.HelpOutline,
                    iconBg = AccentGreenBg,
                    iconTint = AccentGreen,
                    title = stringResource(R.string.profile_support_help),
                    onClick = {}
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.NotificationsNone,
                    iconBg = AccentPurpleBg,
                    iconTint = AccentPurple,
                    title = stringResource(R.string.profile_support_feedback),
                    onClick = {}
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Lock,
                    iconBg = Color.White.copy(alpha = 0.06f),
                    iconTint = TextSecondary,
                    title = stringResource(R.string.legal_logos_title),
                    onClick = { showLegalSheet = true }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    iconBg = AccentRedBg,
                    iconTint = AccentRed,
                    title = stringResource(R.string.profile_logout),
                    titleColor = AccentRed,
                    showChevron = false,
                    onClick = { showLogoutDialog = true }
                )
            }
        }


        // Footer
        item {
            Text(
                text = stringResource(R.string.profile_version, "0.1.0", 128),
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

@Composable
private fun ProCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(Brush.linearGradient(listOf(Color(0xFF1A1A1A), Color(0xFF0F0F0F))))
            .border(1.dp, AccentAmber.copy(alpha = 0.25f), SubTrackShapes.l)
            .clickable {}
            .padding(Spacing.base),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(SubTrackShapes.base)
                .background(Brush.linearGradient(listOf(AccentAmber, Color(0xFFFF9500))))
        ) {
            Icon(Icons.Outlined.Star, null, tint = Color.Black, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text(stringResource(R.string.profile_pro_title), style = SubTrackType.titleL, color = TextPrimary)
                Badge(text = stringResource(R.string.profile_pro_badge), variant = BadgeVariant.PRO)
            }
            Text(stringResource(R.string.profile_pro_subtitle), style = SubTrackType.monoXS, color = TextSecondary)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ProCtaCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .clickable {}
            .padding(Spacing.base),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(SubTrackShapes.base)
                .background(AccentAmberBg)
        ) {
            Icon(Icons.Outlined.Star, null, tint = AccentAmber, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.profile_pro_cta), style = SubTrackType.titleL, color = AccentAmber)
            Text(stringResource(R.string.profile_pro_cta_sub), style = SubTrackType.monoXS, color = TextSecondary)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun AliasCard(
    alias: UserAlias,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, BorderDefault, SubTrackShapes.l)
            .padding(Spacing.base)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(SubTrackShapes.base)
                        .background(AccentGreenBg)
                ) {
                    Icon(Icons.Outlined.AccountCircle, null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                }
                Text(stringResource(R.string.profile_alias_title), style = SubTrackType.titleL, color = TextPrimary)
            }
            Text(
                text = stringResource(R.string.profile_alias_edit),
                style = SubTrackType.bodyS,
                color = AccentBlue,
                modifier = Modifier.clickable(onClick = onEditClick)
            )
        }
        Spacer(Modifier.height(Spacing.s))
        Text(
            text = stringResource(R.string.profile_alias_subtitle),
            style = SubTrackType.bodyXS,
            color = TextTertiary
        )
        Spacer(Modifier.height(Spacing.m))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.base)
                .background(AccentGreen.copy(alpha = 0.04f))
                .border(1.dp, AccentGreen.copy(alpha = 0.15f), SubTrackShapes.base)
                .padding(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            AliasRow(
                logoText = "Y",
                logoBg = Color(0xFF6B2A86),
                brand = "Yape",
                value = alias.yape?.let { formatPhone(it) } ?: "—",
                isDefault = alias.defaultMethod == PaymentAliasType.YAPE
            )
            AliasRow(
                logoText = "P",
                logoBg = Color(0xFF00B5E2),
                brand = "Plin",
                value = alias.plin?.let { formatPhone(it) } ?: "—",
                isDefault = alias.defaultMethod == PaymentAliasType.PLIN
            )
            AliasRow(
                logoText = "CC",
                logoBg = BgSurfaceEl,
                brand = "CCI · ${alias.cciBank ?: "—"}",
                value = alias.cciNumber?.let { maskCci(it) } ?: "—",
                isDefault = alias.defaultMethod == PaymentAliasType.CCI
            )
        }
    }
}

@Composable
private fun AliasRow(
    logoText: String,
    logoBg: Color,
    brand: String,
    value: String,
    isDefault: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(SubTrackShapes.s)
                .background(logoBg)
        ) {
            Text(logoText, style = SubTrackType.monoXS.copy(fontSize = androidx.compose.ui.unit.TextUnit(8f, androidx.compose.ui.unit.TextUnitType.Sp)), color = Color.White)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(brand, style = SubTrackType.bodyS, color = TextPrimary)
            Text(value, style = SubTrackType.monoXS, color = TextSecondary)
        }
        if (isDefault) {
            Box(
                modifier = Modifier
                    .clip(SubTrackShapes.s)
                    .background(AccentGreenBg)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_alias_default_tag),
                    style = SubTrackType.monoXS.copy(fontSize = androidx.compose.ui.unit.TextUnit(7f, androidx.compose.ui.unit.TextUnitType.Sp)),
                    color = AccentGreen
                )
            }
        }
    }
}

private fun formatPhone(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    return if (digits.length == 9) "${digits.take(3)} ${digits.drop(3).take(3)} ${digits.drop(6)}"
    else raw
}

@Composable
private fun TemplateCard(template: Template, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isDefault = template.isDefault
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(
                if (isDefault) Brush.linearGradient(
                    listOf(AccentGreen.copy(alpha = 0.06f), AccentGreen.copy(alpha = 0.02f))
                ) else Brush.linearGradient(listOf(BgSurface, BgSurface))
            )
            .border(1.dp, if (isDefault) AccentGreen.copy(alpha = 0.2f) else BorderDefault, SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.m)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text(template.emoji, style = SubTrackType.headlineS)
                Text(template.name, style = SubTrackType.titleL, color = TextPrimary)
                if (isDefault) Badge(text = "DEFAULT", variant = BadgeVariant.ADMIN)
            }
            Icon(Icons.Outlined.MoreVert, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "\"${template.messageBody}\"",
            style = SubTrackType.bodyXS.copy(fontStyle = FontStyle.Italic),
            color = TextSecondary,
            maxLines = 2
        )
        Spacer(Modifier.height(Spacing.s))
        HorizontalDivider(color = BorderDefault)
        Spacer(Modifier.height(Spacing.s))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            val (dotColor, toneLabel) = when (template.tone) {
                com.gondroid.subtrack.domain.model.enums.TemplateTone.FRIENDLY -> AccentGreen to "Amigable"
                com.gondroid.subtrack.domain.model.enums.TemplateTone.DIRECT -> AccentBlue to "Directa"
                com.gondroid.subtrack.domain.model.enums.TemplateTone.SOFT -> AccentPurple to "Suave"
                com.gondroid.subtrack.domain.model.enums.TemplateTone.FIRM -> AccentAmber to "Firme"
            }
            Box(modifier = Modifier.size(5.dp).clip(SubTrackShapes.circle).background(dotColor))
            Text(toneLabel, style = SubTrackType.monoXS, color = TextTertiary)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    titleColor: Color = TextPrimary,
    value: String? = null,
    showChevron: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.m, vertical = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(32.dp).clip(SubTrackShapes.base).background(iconBg)
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = SubTrackType.bodyS, color = titleColor)
            if (value != null) Text(value, style = SubTrackType.monoXS, color = TextTertiary)
        }
        if (trailing != null) trailing()
        else if (showChevron) Icon(Icons.Outlined.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun SettingsDivider() = HorizontalDivider(
    modifier = Modifier.padding(start = 56.dp),
    color = BorderDefault
)

@Composable
private fun ReferralEntryCard(
    monthsEarned: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(Brush.linearGradient(listOf(AccentAmber.copy(alpha = 0.12f), AccentAmber.copy(alpha = 0.04f))))
            .border(1.dp, AccentAmber.copy(alpha = 0.25f), SubTrackShapes.l)
            .clickable(onClick = onClick)
            .padding(Spacing.base),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(SubTrackShapes.base)
                .background(Brush.linearGradient(listOf(AccentAmber, Color(0xFFFF9500))))
        ) {
            Icon(Icons.Outlined.Star, null, tint = Color.Black, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.profile_referral_title), style = SubTrackType.titleL, color = TextPrimary)
            Text(
                text = stringResource(R.string.profile_referral_months, monthsEarned),
                style = SubTrackType.monoXS,
                color = AccentAmber
            )
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun LegalLogosSheet(onDismiss: () -> Unit) {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BgSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.base)
                .padding(bottom = Spacing.xl)
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(R.string.legal_logos_title),
                style = SubTrackType.headlineM,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.s))
            Text(
                text = stringResource(R.string.legal_logos_body),
                style = SubTrackType.bodyS,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(Spacing.m))
            Text(
                text = stringResource(R.string.legal_logos_simpleicons),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ProfileScreenPreview() {
    SubTrackTheme {
        ProfileScreen(onNavigateToTemplates = {}, onNavigateToReferral = {})
    }
}
