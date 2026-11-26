package com.gondroid.subtrack.feature.createsubscription.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentBlueBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextSecondary

@Composable
fun WhoPaysStep(
    serviceName: String,
    isShared: Boolean?,
    suggestsShared: Boolean,
    onSelect: (isShared: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base)
    ) {
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(text = stringResource(R.string.create_subscription_who_pays_eyebrow))
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_who_pays_title, serviceName),
            style = SubTrackType.displayS,
            color = TextPrimary
        )
        if (suggestsShared) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                stringResource(R.string.create_subscription_who_pays_hint, serviceName),
                style = SubTrackType.bodyS,
                color = TextSecondary
            )
        }
        Spacer(Modifier.height(Spacing.l))

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
            modifier = Modifier.selectableGroup()
        ) {
            WhoPaysOption(
                icon = Icons.Outlined.Person,
                iconBg = AccentBlueBg,
                iconTint = AccentBlue,
                title = stringResource(R.string.create_subscription_who_pays_me),
                desc = stringResource(R.string.create_subscription_who_pays_me_desc),
                selected = isShared == false,
                onClick = { onSelect(false) }
            )
            WhoPaysOption(
                icon = Icons.Outlined.People,
                iconBg = AccentGreenBg,
                iconTint = AccentGreen,
                title = stringResource(R.string.create_subscription_who_pays_shared),
                desc = stringResource(R.string.create_subscription_who_pays_shared_desc),
                selected = isShared == true,
                onClick = { onSelect(true) }
            )
        }
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun WhoPaysOption(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    desc: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SubTrackShapes.l)
            .background(BgSurface)
            .border(1.dp, if (selected) AccentGreen.copy(alpha = 0.5f) else BorderDefault, SubTrackShapes.l)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(SubTrackShapes.base)
                .background(iconBg)
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = SubTrackType.titleL, color = TextPrimary)
            Text(desc, style = SubTrackType.bodyXS, color = TextSecondary)
        }
        if (selected) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Sin elegir")
@Composable
private fun WhoPaysStepPreview() {
    SubTrackTheme { WhoPaysStep(serviceName = "ChatGPT Plus", isShared = null, suggestsShared = false, onSelect = {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C, name = "Familiar preseleccionado")
@Composable
private fun WhoPaysStepSharedPreview() {
    SubTrackTheme { WhoPaysStep(serviceName = "Spotify", isShared = true, suggestsShared = true, onSelect = {}) }
}
