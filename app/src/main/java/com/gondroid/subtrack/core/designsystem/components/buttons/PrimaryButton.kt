package com.gondroid.subtrack.core.designsystem.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentRed
import com.gondroid.subtrack.core.designsystem.theme.BgApp
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: ImageVector? = null,
    accent: Color? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "primary_btn_scale"
    )
    val containerColor = accent ?: TextPrimary

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = SubTrackShapes.l,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = BgApp,
            disabledContainerColor = containerColor.copy(alpha = 0.3f),
            disabledContentColor = BgApp.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = 14.dp),
        interactionSource = interactionSource,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Text(text = text, style = SubTrackType.titleL)
        if (trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun PrimaryButtonPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            PrimaryButton(text = "Crear suscripción", onClick = {}, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.s))
            PrimaryButton(
                text = "Continuar",
                onClick = {},
                trailingIcon = Icons.Outlined.ArrowForward,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.s))
            PrimaryButton(
                text = "Guardar cambios",
                onClick = {},
                accent = AccentGreen,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.s))
            PrimaryButton(
                text = "Eliminar suscripción",
                onClick = {},
                accent = AccentRed,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun PrimaryButtonStatesPreview() {
    SubTrackTheme {
        Column(modifier = Modifier.padding(Spacing.base)) {
            PrimaryButton(text = "Habilitado", onClick = {}, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.s))
            PrimaryButton(
                text = "Crear y enviar invitaciones a todos los miembros",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.s))
            PrimaryButton(
                text = "Desactivado",
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
