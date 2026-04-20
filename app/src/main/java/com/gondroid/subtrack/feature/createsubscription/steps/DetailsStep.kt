package com.gondroid.subtrack.feature.createsubscription.steps

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.R
import com.gondroid.subtrack.core.designsystem.components.avatar.ServiceLogo
import com.gondroid.subtrack.core.designsystem.components.input.SegmentedSelector
import com.gondroid.subtrack.core.designsystem.components.text.Eyebrow
import com.gondroid.subtrack.core.designsystem.theme.AccentAmber
import com.gondroid.subtrack.core.designsystem.theme.AccentAmberBg
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
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
import com.gondroid.subtrack.core.util.toComposeColor
import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.enums.BillingCycle
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory

private val CURRENCIES = listOf("PEN", "USD")
private val CYCLES = listOf(BillingCycle.MONTHLY, BillingCycle.YEARLY)
private val DAYS = (1..31).toList()

@Composable
fun DetailsStep(
    service: ServiceTemplate?,
    totalAmount: String,
    currency: String,
    cycle: BillingCycle,
    cutoffDay: Int,
    onAmountChange: (String) -> Unit,
    onApplySuggested: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onCycleChange: (BillingCycle) -> Unit,
    onCutoffDayChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.base)
    ) {
        Spacer(Modifier.height(Spacing.m))
        Eyebrow(text = stringResource(R.string.create_subscription_step2_eyebrow))
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.create_subscription_step2_title),
            style = SubTrackType.displayS,
            color = TextPrimary
        )
        if (service != null) {
            Spacer(Modifier.height(Spacing.xs))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                ServiceLogo(
                    serviceName = service.name,
                    brandColor = service.brandColor.toComposeColor(),
                    size = 16.dp
                )
                Text(service.name, style = SubTrackType.monoXS, color = TextTertiary)
            }
        }
        Spacer(Modifier.height(Spacing.l))

        // Amount card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Eyebrow(text = stringResource(R.string.create_subscription_amount_label))
            Spacer(Modifier.height(Spacing.s))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text("S/", style = SubTrackType.displayM, color = TextTertiary)
                BasicTextField(
                    value = totalAmount,
                    onValueChange = { v ->
                        if (v.count { it == '.' } <= 1 && v.all { it.isDigit() || it == '.' }) {
                            onAmountChange(v)
                        }
                    },
                    textStyle = SubTrackType.displayM.copy(color = TextPrimary),
                    cursorBrush = SolidColor(AccentGreen),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    decorationBox = { inner ->
                        Box {
                            if (totalAmount.isEmpty()) {
                                Text("0.00", style = SubTrackType.displayM.copy(color = TextTertiary))
                            }
                            inner()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            // Suggested amount chip
            val suggested = service?.suggestedMonthly
            if (suggested != null) {
                Spacer(Modifier.height(Spacing.s))
                Row(
                    modifier = Modifier
                        .clip(SubTrackShapes.circle)
                        .background(AccentAmberBg)
                        .border(1.dp, AccentAmber.copy(alpha = 0.3f), SubTrackShapes.circle)
                        .clickable(onClick = onApplySuggested)
                        .padding(horizontal = Spacing.m, vertical = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        stringResource(R.string.create_subscription_suggested_chip, "%.2f".format(suggested)),
                        style = SubTrackType.monoXS,
                        color = AccentAmber
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.m))

        // Currency selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Eyebrow(text = stringResource(R.string.create_subscription_currency_label))
            Spacer(Modifier.height(Spacing.s))
            SegmentedSelector(
                options = CURRENCIES,
                selectedIndex = CURRENCIES.indexOf(currency).coerceAtLeast(0),
                onSelectionChange = { onCurrencyChange(CURRENCIES[it]) },
                labelProvider = { it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Spacing.m))

        // Cycle selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Eyebrow(text = stringResource(R.string.create_subscription_cycle_label))
            Spacer(Modifier.height(Spacing.s))
            val cycleLabels = mapOf(
                BillingCycle.MONTHLY to stringResource(R.string.create_subscription_cycle_monthly),
                BillingCycle.YEARLY  to stringResource(R.string.create_subscription_cycle_yearly),
                BillingCycle.CUSTOM  to stringResource(R.string.create_subscription_cycle_custom)
            )
            SegmentedSelector(
                options = CYCLES,
                selectedIndex = CYCLES.indexOf(cycle).coerceAtLeast(0),
                onSelectionChange = { onCycleChange(CYCLES[it]) },
                labelProvider = { c -> cycleLabels[c] ?: c.name },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Spacing.m))

        // Cutoff day card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SubTrackShapes.l)
                .background(BgSurface)
                .border(1.dp, BorderDefault, SubTrackShapes.l)
                .padding(Spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Eyebrow(text = stringResource(R.string.create_subscription_cutoff_label))
                Text(
                    stringResource(R.string.create_subscription_cutoff_selected, cutoffDay),
                    style = SubTrackType.monoXS,
                    color = AccentGreen
                )
            }
            Spacer(Modifier.height(Spacing.s))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                items(DAYS) { day ->
                    val isSelected = day == cutoffDay
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(SubTrackShapes.s)
                            .background(if (isSelected) AccentGreen.copy(alpha = 0.15f) else BgSurfaceEl)
                            .border(1.dp, if (isSelected) AccentGreen.copy(alpha = 0.5f) else BorderDefault, SubTrackShapes.s)
                            .clickable { onCutoffDayChange(day) }
                            .padding(horizontal = Spacing.s, vertical = Spacing.xs)
                    ) {
                        Text(
                            "$day",
                            style = SubTrackType.monoS,
                            color = if (isSelected) AccentGreen else TextTertiary
                        )
                    }
                }
            }
            Spacer(Modifier.height(Spacing.xs))
            Text(
                stringResource(R.string.create_subscription_cutoff_hint, cutoffDay),
                style = SubTrackType.bodyXS,
                color = TextTertiary
            )
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun DetailsStepPreview() {
    SubTrackTheme {
        DetailsStep(
            service = ServiceTemplate("tpl_netflix", "Netflix", "#E50914", "N", 54.90, SubscriptionCategory.STREAMING),
            totalAmount = "54.90",
            currency = "PEN",
            cycle = BillingCycle.MONTHLY,
            cutoffDay = 15,
            onAmountChange = {},
            onApplySuggested = {},
            onCurrencyChange = {},
            onCycleChange = {},
            onCutoffDayChange = {}
        )
    }
}
