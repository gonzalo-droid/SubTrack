package com.gondroid.subtrack.core.designsystem.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gondroid.subtrack.core.designsystem.components.avatar.Avatar
import com.gondroid.subtrack.core.designsystem.components.avatar.AvatarStack
import com.gondroid.subtrack.core.designsystem.components.avatar.LogoSize
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.buttons.PrimaryButton
import com.gondroid.subtrack.core.designsystem.components.buttons.STIconButton
import com.gondroid.subtrack.core.designsystem.components.buttons.SecondaryButton
import com.gondroid.subtrack.core.designsystem.components.cards.HeroCard
import com.gondroid.subtrack.core.designsystem.components.cards.SurfaceCard
import com.gondroid.subtrack.core.designsystem.components.indicators.ProgressBar
import com.gondroid.subtrack.core.designsystem.components.indicators.ProgressDots
import com.gondroid.subtrack.core.designsystem.components.input.STTextField
import com.gondroid.subtrack.core.designsystem.components.input.SegmentedSelector
import com.gondroid.subtrack.core.designsystem.components.input.ToggleSwitch
import com.gondroid.subtrack.core.designsystem.components.text.AmountDisplay
import com.gondroid.subtrack.core.designsystem.components.text.AmountSize
import com.gondroid.subtrack.core.designsystem.components.text.Badge
import com.gondroid.subtrack.core.designsystem.components.text.BadgeVariant
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.components.text.StatusPill
import com.gondroid.subtrack.core.designsystem.theme.AccentBlue
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentPurple
import com.gondroid.subtrack.core.designsystem.theme.Spacing
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.SubTrackType
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary
import com.gondroid.subtrack.domain.model.enums.PaymentStatus

@Composable
fun ComponentGalleryScreen() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = Spacing.base, vertical = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.base)
    ) {
        // ── Texto ────────────────────────────────────────────────────────────
        item { GallerySectionHeader("Texto") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                Eyebrow(text = "Secciones activas")
                AmountDisplay(amount = 1234.90, size = AmountSize.LARGE)
                AmountDisplay(amount = 49.90, size = AmountSize.MEDIUM)
                AmountDisplay(amount = 12.50, size = AmountSize.SMALL)
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    StatusPill(PaymentStatus.PAID)
                    StatusPill(PaymentStatus.PENDING)
                    StatusPill(PaymentStatus.OVERDUE)
                    StatusPill(PaymentStatus.LATE)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Badge(text = "Pro", variant = BadgeVariant.PRO)
                    Badge(text = "Admin", variant = BadgeVariant.ADMIN)
                    Badge(text = "Nuevo", variant = BadgeVariant.NEW)
                    Badge(text = "Error", variant = BadgeVariant.ERROR)
                }
            }
        }

        // ── Botones ──────────────────────────────────────────────────────────
        item { GalleryDivider() }
        item { GallerySectionHeader("Botones") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                PrimaryButton(text = "Crear suscripción", onClick = {}, modifier = Modifier.fillMaxWidth())
                PrimaryButton(
                    text = "Continuar",
                    onClick = {},
                    trailingIcon = Icons.Outlined.ArrowForward,
                    modifier = Modifier.fillMaxWidth()
                )
                PrimaryButton(
                    text = "Guardar cambios",
                    onClick = {},
                    accent = AccentGreen,
                    modifier = Modifier.fillMaxWidth()
                )
                PrimaryButton(text = "Desactivado", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
                SecondaryButton(text = "Cancelar", onClick = {}, modifier = Modifier.fillMaxWidth())
                SecondaryButton(
                    text = "Compartir enlace",
                    trailingIcon = Icons.Outlined.Share,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                    STIconButton(icon = Icons.Outlined.Add, contentDescription = "Agregar", onClick = {})
                    STIconButton(icon = Icons.Outlined.Share, contentDescription = "Compartir", onClick = {})
                    STIconButton(
                        icon = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        onClick = {},
                        showBadge = true
                    )
                }
            }
        }

        // ── Avatares ─────────────────────────────────────────────────────────
        item { GalleryDivider() }
        item { GallerySectionHeader("Avatares") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Gonzalo Meza", "Ana Torres", "Carlos Pérez", "María García", "Diego Quispe", "Lucía Vega")
                        .forEach { Avatar(name = it, size = 40.dp) }
                }
                AvatarStack(
                    names = listOf("Ana Torres", "Carlos Pérez", "María García", "Diego Quispe", "Lucía Vega"),
                    maxVisible = 3,
                    size = 32.dp
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ServiceLogo(serviceName = "Netflix", brandColor = Color(0xFFE50914), size = LogoSize.Large.dp)
                    ServiceLogo(serviceName = "Spotify", brandColor = Color(0xFF1DB954), size = LogoSize.Large.dp)
                    ServiceLogo(serviceName = "iCloud+", brandColor = Color(0xFF147EFB), size = LogoSize.Large.dp)
                    ServiceLogo(serviceName = "ChatGPT Plus", brandColor = Color(0xFF10A37F), size = LogoSize.Large.dp)
                }
            }
        }

        // ── Cards ────────────────────────────────────────────────────────────
        item { GalleryDivider() }
        item { GallerySectionHeader("Cards") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                SurfaceCard(modifier = Modifier.fillMaxWidth()) {
                    Text("SurfaceCard estática", style = SubTrackType.headlineS)
                    Spacer(Modifier.height(Spacing.xs))
                    Text("Fondo bg_surface, borde sutil, sin interacción", style = SubTrackType.bodyM, color = TextTertiary)
                }
                SurfaceCard(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("SurfaceCard clickable", style = SubTrackType.headlineS)
                    Spacer(Modifier.height(Spacing.xs))
                    Text("Cambia a bg_surface_hi al presionar", style = SubTrackType.bodyM, color = TextTertiary)
                }
                HeroCard(accentColor = AccentBlue, modifier = Modifier.fillMaxWidth()) {
                    Eyebrow(text = "Gasto mensual")
                    Spacer(Modifier.height(Spacing.xs))
                    AmountDisplay(amount = 124.70, size = AmountSize.LARGE)
                    Spacer(Modifier.height(Spacing.xs))
                    Text("4 suscripciones activas", style = SubTrackType.bodyS, color = TextTertiary)
                }
                HeroCard(accentColor = AccentGreen, modifier = Modifier.fillMaxWidth()) {
                    Eyebrow(text = "Lo que cobras")
                    Spacer(Modifier.height(Spacing.xs))
                    AmountDisplay(amount = 49.89, size = AmountSize.LARGE)
                }
                HeroCard(accentColor = AccentPurple, modifier = Modifier.fillMaxWidth()) {
                    Eyebrow(text = "Próximo cobro")
                    Spacer(Modifier.height(Spacing.xs))
                    Text("17 abr", style = SubTrackType.headlineL)
                }
            }
        }

        // ── Inputs ───────────────────────────────────────────────────────────
        item { GalleryDivider() }
        item { GallerySectionHeader("Inputs") }
        item {
            var name by remember { mutableStateOf("") }
            var amount by remember { mutableStateOf("") }
            var toggle1 by remember { mutableStateOf(true) }
            var toggle2 by remember { mutableStateOf(false) }
            var segSelected by remember { mutableIntStateOf(0) }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
                STTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre del servicio",
                    placeholder = "Ej. Netflix, Spotify…",
                    leadingIcon = Icons.Outlined.Search,
                    modifier = Modifier.fillMaxWidth()
                )
                STTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Email de contacto",
                    placeholder = "correo@ejemplo.com",
                    leadingIcon = Icons.Outlined.Email,
                    isError = amount.isNotEmpty() && !amount.contains("@"),
                    errorMessage = "Ingresa un correo válido",
                    modifier = Modifier.fillMaxWidth()
                )
                SegmentedSelector(
                    options = listOf("Mensual", "Trimestral", "Anual"),
                    selectedIndex = segSelected,
                    onSelectionChange = { segSelected = it },
                    labelProvider = { it },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.base)
                ) {
                    ToggleSwitch(
                        checked = toggle1,
                        onCheckedChange = { toggle1 = it },
                        contentDescription = "Notificaciones"
                    )
                    Text("Notificaciones", style = SubTrackType.bodyM, color = TextPrimary)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.base)
                ) {
                    ToggleSwitch(
                        checked = toggle2,
                        onCheckedChange = { toggle2 = it },
                        contentDescription = "Recordatorios"
                    )
                    Text("Recordatorios", style = SubTrackType.bodyM, color = TextPrimary)
                }
            }
        }

        // ── Indicators ───────────────────────────────────────────────────────
        item { GalleryDivider() }
        item { GallerySectionHeader("Indicators") }
        item {
            var currentDot by remember { mutableIntStateOf(0) }
            var progressVal by remember { mutableFloatStateOf(0.4f) }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
                ProgressDots(totalSteps = 5, currentStep = currentDot)
                PrimaryButton(
                    text = if (currentDot < 4) "Siguiente paso →" else "Reiniciar",
                    onClick = { currentDot = if (currentDot < 4) currentDot + 1 else 0 },
                    modifier = Modifier.fillMaxWidth()
                )
                ProgressBar(progress = progressVal, modifier = Modifier.fillMaxWidth())
                ProgressBar(progress = progressVal, showShimmer = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                    SecondaryButton(text = "-10%", onClick = { progressVal = (progressVal - 0.1f).coerceIn(0f, 1f) })
                    SecondaryButton(text = "+10%", onClick = { progressVal = (progressVal + 0.1f).coerceIn(0f, 1f) })
                }
            }
        }

        item { Spacer(Modifier.height(Spacing.huge)) }
    }
}

@Composable
private fun GallerySectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = SubTrackType.monoS,
        color = TextTertiary,
        modifier = Modifier.padding(vertical = Spacing.xs)
    )
}

@Composable
private fun GalleryDivider() {
    HorizontalDivider(color = TextTertiary.copy(alpha = 0.15f))
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun ComponentGalleryPreview() {
    SubTrackTheme {
        ComponentGalleryScreen()
    }
}
