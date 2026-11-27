package com.gondroid.subtrack.core.designsystem.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgSurface
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.BorderStrong
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

@Composable
fun STTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> AccentRed
            isFocused -> BorderStrong
            else -> BorderDefault
        },
        animationSpec = tween(durationMillis = 200),
        label = "text_field_border"
    )

    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = SubTrackType.monoXS,
            color = TextTertiary
        )
        Spacer(Modifier.height(Spacing.xs))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = SubTrackType.bodyL.copy(color = TextPrimary),
            cursorBrush = SolidColor(TextPrimary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            interactionSource = interactionSource
        ) { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SubTrackShapes.l)
                    .border(1.dp, borderColor, SubTrackShapes.l)
                    .background(BgSurface)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(Spacing.s))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = SubTrackType.bodyL,
                            color = TextTertiary
                        )
                    }
                    innerTextField()
                }
                if (trailingIcon != null) {
                    Spacer(Modifier.width(Spacing.s))
                    trailingIcon()
                }
            }
        }
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = errorMessage,
                style = SubTrackType.monoS,
                color = AccentRed
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun STTextFieldPreview() {
    SubTrackTheme {
        var name by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("54.90") }
        Column(modifier = Modifier.padding(Spacing.base)) {
            STTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre del servicio",
                placeholder = "Ej. Netflix, Spotify…",
                leadingIcon = Icons.Outlined.Search,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.base))
            STTextField(
                value = amount,
                onValueChange = { amount = it },
                label = "Monto mensual",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun STTextFieldErrorPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            STTextField(
                value = "abc",
                onValueChange = {},
                label = "Monto",
                isError = true,
                errorMessage = "Ingresa un monto válido mayor a 0",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.base))
            STTextField(
                value = "",
                onValueChange = {},
                label = "Desactivado",
                placeholder = "No disponible",
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
