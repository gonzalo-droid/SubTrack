package com.gondroid.subtrack.feature.profile.alias

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
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
import com.gondroid.subtrack.domain.model.enums.PaymentAliasType

@Composable
fun AliasEditScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AliasEditViewModel = viewModel(factory = AliasEditViewModelFactory())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AliasEditEvent.Saved -> {
                    Toast.makeText(context, context.getString(R.string.alias_saved_toast), Toast.LENGTH_SHORT).show()
                    onBack()
                }
            }
        }
    }

    when (val state = uiState) {
        is AliasEditUiState.Loading -> Box(modifier.fillMaxSize())
        is AliasEditUiState.Success -> AliasEditContent(
            data = state.data,
            onBack = onBack,
            onYapeChange = viewModel::onYapeChange,
            onPlinChange = viewModel::onPlinChange,
            onCciBankChange = viewModel::onCciBankChange,
            onCciNumberChange = viewModel::onCciNumberChange,
            onDefaultMethodChange = viewModel::onDefaultMethodChange,
            onSave = viewModel::save,
            modifier = modifier
        )
    }
}

@Composable
private fun AliasEditContent(
    data: AliasEditData,
    onBack: () -> Unit,
    onYapeChange: (String) -> Unit,
    onPlinChange: (String) -> Unit,
    onCciBankChange: (String) -> Unit,
    onCciNumberChange: (String) -> Unit,
    onDefaultMethodChange: (PaymentAliasType) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.base, vertical = Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            STIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "", onClick = onBack)
            Text(stringResource(R.string.alias_title), style = SubTrackType.headlineS, color = TextPrimary)
            Text(
                text = stringResource(R.string.alias_save),
                style = SubTrackType.titleL,
                color = if (data.hasChanges) AccentBlue else TextTertiary,
                modifier = Modifier.clickable(enabled = data.hasChanges, onClick = onSave)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.base),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Spacer(Modifier.height(Spacing.s))

            STTextField(
                value = data.yape,
                onValueChange = onYapeChange,
                label = stringResource(R.string.alias_yape_label),
                placeholder = stringResource(R.string.alias_yape_placeholder),
                isError = data.yapeError != null,
                errorMessage = data.yapeError,
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            STTextField(
                value = data.plin,
                onValueChange = onPlinChange,
                label = stringResource(R.string.alias_plin_label),
                placeholder = stringResource(R.string.alias_plin_placeholder),
                isError = data.plinError != null,
                errorMessage = data.plinError,
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            STTextField(
                value = data.cciBank,
                onValueChange = onCciBankChange,
                label = stringResource(R.string.alias_cci_bank_label),
                placeholder = stringResource(R.string.alias_cci_bank_placeholder),
                modifier = Modifier.fillMaxWidth()
            )

            STTextField(
                value = data.cciNumber,
                onValueChange = onCciNumberChange,
                label = stringResource(R.string.alias_cci_number_label),
                placeholder = stringResource(R.string.alias_cci_number_placeholder),
                isError = data.cciError != null,
                errorMessage = data.cciError,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )

            // Default method radio
            Text(
                text = stringResource(R.string.alias_default_label),
                style = SubTrackType.monoXS,
                color = TextTertiary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                PaymentAliasType.entries.forEach { method ->
                    val selected = data.defaultMethod == method
                    val label = when (method) {
                        PaymentAliasType.YAPE -> "Yape"
                        PaymentAliasType.PLIN -> "Plin"
                        PaymentAliasType.CCI -> "CCI"
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(SubTrackShapes.base)
                            .background(if (selected) AccentGreenBg else BgSurfaceEl)
                            .border(1.dp, if (selected) AccentGreen.copy(alpha = 0.3f) else BorderDefault, SubTrackShapes.base)
                            .clickable { onDefaultMethodChange(method) }
                            .padding(vertical = Spacing.m)
                    ) {
                        Text(label, style = SubTrackType.bodyS, color = if (selected) AccentGreen else TextSecondary)
                    }
                }
            }

            // Info box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.base)
                    .background(BgSurface)
                    .border(1.dp, BorderDefault, SubTrackShapes.base)
                    .padding(Spacing.m)
            ) {
                Text(
                    text = stringResource(R.string.alias_info_box),
                    style = SubTrackType.bodyXS,
                    color = TextTertiary
                )
            }

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun AliasEditPreview() {
    SubTrackTheme {
        AliasEditContent(
            data = AliasEditData(
                yape = "987654321",
                plin = "987654321",
                cciBank = "BCP",
                cciNumber = "00219000016789",
                defaultMethod = PaymentAliasType.YAPE,
                hasChanges = true
            ),
            onBack = {}, onYapeChange = {}, onPlinChange = {},
            onCciBankChange = {}, onCciNumberChange = {}, onDefaultMethodChange = {}, onSave = {}
        )
    }
}
